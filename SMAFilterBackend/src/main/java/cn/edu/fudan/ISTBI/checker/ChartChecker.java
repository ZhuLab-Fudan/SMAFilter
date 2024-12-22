package cn.edu.fudan.ISTBI.checker;

import cn.edu.fudan.ISTBI.entity.ChartFilter;
import cn.edu.fudan.ISTBI.entity.Rule;
import cn.edu.fudan.ISTBI.result.ChartResult;
import cn.edu.fudan.ISTBI.result.Result;
import cn.edu.fudan.ISTBI.result.SheetResult;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartChecker implements FileChecker
{
    List<ChartFilter> filters;

    public ChartChecker(List<ChartFilter> filters)
    {
        this.filters = filters;
    }

    @Override
    public Map.Entry<Result, String> evaluate(File file)
    {
        ChartResult result = null;
        String errorInfo = null;
        Workbook workbook = null;
        try
        {
             // currently, only these two format supported
            if (file.getName().endsWith("xlsx"))
            {
                workbook = new XSSFWorkbook(new FileInputStream(file));
            }
            else if (file.getName().endsWith("xls"))
            {
                workbook = new HSSFWorkbook(new FileInputStream(file));
            }

            result = new ChartResult(file.getPath(), file.getName());
            StringBuilder builder = new StringBuilder();
            for (Sheet sheet : workbook)
            {
                Map.Entry<SheetResult, String> res = evaluateSheet(sheet);
                errorInfo = res.getValue();
                if (errorInfo == null)
                {
                    result.addSheetResult(res.getKey());
                }
                else
                {
                    builder.append(errorInfo + "; ");
                }
            }
            errorInfo = builder.toString();
            if (errorInfo.length() == 0)
            {
                errorInfo = null;
            }
        }
        catch (Exception e)
        {
            errorInfo = "Failed to read file";
        }





        return new AbstractMap.SimpleEntry<>(result, errorInfo);
    }

    private Map.Entry<SheetResult, String> evaluateSheet(Sheet sheet)
    {
        String errorInfo = null;
        SheetResult sheetResult = null;
        Row header = sheet.getRow(0);
        List<String> cells = new ArrayList<>();
        int startRow = 0;

        // get first row and check if it is the columnName
        for (Cell cell : header)
        {
            cells.add(cell.toString().replaceAll("\n", "").replaceAll("\r", ""));
        }

//        // check columnName and columnIndex available
//        Map.Entry<Integer, String> checkResult = checkAllColumnAvailable(cells);
//        int checkResultCode = checkResult.getKey();
//        errorInfo = checkResult.getValue();
//        if (errorInfo != null)
//        {
//            errorInfo = "Sheet '" + sheet.getSheetName() +"' 中，" + errorInfo;
//            return new AbstractMap.SimpleEntry<>(null, errorInfo);
//        }

        boolean useColumnName = false;
        for (ChartFilter filter : filters)
        {
            for (Rule rule : filter.getRules())
            {
                if (rule.getColumnSelectType() == 1)
                {
                    useColumnName = true;
                    break;
                }
            }
            if (useColumnName)
            {
                break;
            }
        }

        // set result header
        cells.add(0, "行号");
        RowChecker rowCheckers[] = new RowChecker[filters.size()];
        if (useColumnName)   // use columnName and columnIndex to select column
        {
            startRow = 1;
        }
        else  // only use columnIndex to select column
        {
            cells.add(0, "行号");
            for (int i = 1;i < cells.size();i++)
            {
                cells.set(i, "第 " + i + " 列");
            }
        }
        sheetResult = new SheetResult(sheet.getSheetName(), cells);

        // build rowCheckers
        for (int i = 0;i < filters.size();i++)
        {
            rowCheckers[i] = new RowChecker();
            errorInfo = rowCheckers[i].setRules(filters.get(i).getRules(), cells);
            if (errorInfo != null)
            {
                errorInfo = "sheet '" + sheet.getSheetName() +"' , Group " + (i + 1) + " conflicts. " + errorInfo;
                return new AbstractMap.SimpleEntry<>(null, errorInfo);
            }
        }

        // evaluate for each row
        for (Row row : sheet)
        {
            if (row.getRowNum() < startRow)
            {
                continue;
            }
            for (RowChecker rowChecker : rowCheckers)
            {
                List<String> res = rowChecker.evaluate(row);
                if (res != null)
                {
                    sheetResult.addRow(res);
                    break;
                }
            }
        }

        return new AbstractMap.SimpleEntry<>(sheetResult, null);
    }

//    private Map.Entry<Integer, String> checkAllColumnAvailable(List<String> cells)
//    {
//        int result = 0x3;
//        String errorInfo = null;
//        for (ChartFilter filter : filters)
//        {
//            for (Rule rule : filter.getRules())
//            {
//                if (rule.getColumnSelectType() == 0)
//                {
//                    if (rule.getColumnIndex() >= cells.size())
//                    {
//                        result = result & (~COLUMN_INDEX_VALID);
//                        errorInfo = "仅有 " + cells.size() + " 列，不存在第 " + (rule.getColumnIndex() + 1) + " 列";
//                    }
//                }
//                else
//                {
//                    result = result | COLUMN_HAS_NAME;
//                    if (!cells.contains(rule.getColumnName()))
//                    {
//                        result = result & (~COLUMN_NAME_VALID);
//                        errorInfo = "找不到名为 '" + rule.getColumnName() + "' 的列";
//                    }
//                }
//            }
//        }
//        return new AbstractMap.SimpleEntry<>(result, errorInfo);
//    }

//    public static void main(String[] args)
//    {
//        CellChecker checkers[] = new CellChecker[10];
//        checkers[0] = new CellChecker("");
//        int a = 0x1;
//        int b = 0x2;
//        int c = 0x3;
//        int d = c & (~b);
//        d = d & (~b);
//        int e = c & (~a);
//        e = e & (~a);
//        System.out.println(c & (~b));
//        System.out.println(c & (~a));
//        System.out.println(d);
//        System.out.println(e);
//
//    }

//    public static void main(String[] args)
//    {
//        StringBuilder builder = new StringBuilder();
//        String txt = builder.toString();
//        System.out.println(txt);
//    }
}
