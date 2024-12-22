package cn.edu.fudan.ISTBI.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SheetResult
{
    String sheetName;
    List<String> columnNames;
    List<Integer> columnSizes;
    List<List<String>> values;

    public SheetResult(String sheetName, List<String> columnNames)
    {
        this.sheetName = sheetName;
        this.columnNames = columnNames;
        this.values = new ArrayList<>();
        columnSizes = new ArrayList<>();
        for (int i = 0;i < columnNames.size();i++)
        {
            columnSizes.add(5);
        }
        resetColumnSize(columnNames);
    }

    public void addRow(List<String> row)
    {
        this.values.add(row);
        resetColumnSize(row);
    }

    private void resetColumnSize(List<String> texts)
    {
        for (int i = 0;i < texts.size();i++)
        {
            if (i >= columnSizes.size())
            {
                break;
            }
            int size = texts.get(i).length();
            if (size > columnSizes.get(i))
            {
                columnSizes.set(i, size);
            }
            if (size > 20)
            {
                columnSizes.set(i, 20);
            }
        }
    }

}
