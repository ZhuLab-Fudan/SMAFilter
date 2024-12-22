package cn.edu.fudan.ISTBI.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChartResult extends Result
{
    final String filePath;
    final String fileName;
    List<SheetResult> sheetResults;


    public ChartResult(String filePath, String fileName)
    {
        this.filePath = filePath;
        this.fileName = fileName;
        sheetResults = new ArrayList<>();
    }

    public void addSheetResult(SheetResult result)
    {
        this.sheetResults.add(result);
    }

}
