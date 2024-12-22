package cn.edu.fudan.ISTBI.checker;

import cn.edu.fudan.ISTBI.entity.Rule;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class RowChecker
{
    CellChecker checkers[];
    public String setRules(List<Rule> rules, List<String> header)
    {
        String errorInfo = null;
        checkers = new CellChecker[header.size() - 1];
        for (Rule rule : rules)
        {
            int targetIndex = -1;
            if (rule.getColumnSelectType() == 0)  // by index
            {
                targetIndex = rule.getColumnIndex();
            }
            else
            {
                targetIndex = header.indexOf(rule.getColumnName()) - 1;
            }

            if (targetIndex == -1)
            {
                errorInfo = "No column named '" + rule.getColumnName() + "'";
            }
            else if (targetIndex >= checkers.length)
            {
                errorInfo = "Cannot find column " + (targetIndex + 1) + ", only " + checkers.length + " columns available";
            }
            else if (checkers[targetIndex]!= null)
            {
                errorInfo = "Column " + (targetIndex + 1) + " has multiple conditions";
            }
            else
            {
                CellChecker checker;
                if (rule.getColumnType() == 0)  // number
                {
                    checker = new CellChecker(rule.getNumberPattern());
                }
                else
                {
                    checker = new CellChecker(rule.getStringPattern());
                }
                checkers[targetIndex] = checker;
            }
        }

        return errorInfo;
    }
    public List<String> evaluate(Row row)
    {
        List<String> result = new ArrayList<>();
        result.add(Integer.toString(row.getRowNum() + 1));
        for (int i = 0;i < checkers.length;i++)
        {
            Cell cell = row.getCell(i);
            String text = "";
            if (cell != null)
            {
                text = cell.toString();
            }
            if (checkers[i] != null)
            {
                text = checkers[i].evaluate(text);
                if (text != null)
                {
                    result.add(text);
                }
                else  // this cell does not satisfy the filter. skip this row
                {
                    result = null;
                    break;
                }
            }
            else
            {
                result.add(text);
            }
        }

        return result;
    }
}
