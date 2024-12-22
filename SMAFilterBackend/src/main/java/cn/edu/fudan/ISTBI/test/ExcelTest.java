package cn.edu.fudan.ISTBI.test;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

public class ExcelTest
{
    public static void main(String[] args)
    {
        String excelPath = "G:\\SMAData\\上海六院.xlsx";
//        String excelPath = "G:\\SMAData\\上海六院.xls";
//        String excelPath = "G:\\SMAData\\上海六院.csv";
        try
        {
            FileInputStream fileInputStream = new FileInputStream(excelPath);

            Workbook workbook = new XSSFWorkbook(fileInputStream);  // for xlsx
//            Workbook workbook = new HSSFWorkbook(fileInputStream);   // for xls
//            Workbook workbook = WorkbookFactory.create(fileInputStream);  // for csv, not support

            Sheet sheet = workbook.getSheetAt(0);
            int i = 0;
            for (Row row : sheet)
            {
                System.out.print(row.getRowNum() + ": ");
                for (Cell cell : row)
                {
                    System.out.print(cell.toString().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") + "\t");

                }
                System.out.println();
                i++;
                if (i >= 10)
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
