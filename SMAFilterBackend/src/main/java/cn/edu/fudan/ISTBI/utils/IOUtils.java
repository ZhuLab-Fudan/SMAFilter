package cn.edu.fudan.ISTBI.utils;

import cn.edu.fudan.ISTBI.entity.Center;
import cn.edu.fudan.ISTBI.result.ChartResult;
import cn.edu.fudan.ISTBI.result.SheetResult;
import cn.edu.fudan.ISTBI.result.SingleResult;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.TypeReference;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.rtf.RTFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IOUtils
{
    public static Map.Entry<String, Boolean> getText(File file)
    {
        String fileName = file.getName();
        Boolean isOCR = false;
        int dotIndex = fileName.lastIndexOf('.');
        String extension = "";
        String result = "";
        if (dotIndex > 0)
        {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }

        switch (extension)
        {
            case "txt":
            case "csv":
            case "json":
                result = getSimpleText(file);
                break;
            case "pdf":
                result = getPDFText(file);
                if (result.strip().equals(""))
                {
                    result = getOCRText(file);
                    isOCR = true;
                }
                break;
            case "docx":
            case "docm":
                result = getDOCXText(file);
                if (result == null)
                {
                    result = getDOCText(file);
                }
                if (result == null)
                {
                    result = getRTFText(file);
                }
                if (result == null)
                {
                    result = "";
                }
                break;
            case "doc":
                result = getDOCText(file);
                if (result == null)
                {
                    result = getRTFText(file);
                }
                if (result == null)
                {
                    result = getDOCXText(file);
                }
                if (result == null)
                {
                    result = "";
                }
                break;
            case "rtf":
                result = getRTFText(file);
                if (result == null)
                {
                    result = getDOCText(file);
                }
                if (result == null)
                {
                    result = getDOCXText(file);
                }
                if (result == null)
                {
                    result = "";
                }
                break;
            default:
                result = getOCRText(file);
                isOCR = true;
        }
//        System.out.println(result);
        return new AbstractMap.SimpleEntry<>(result, isOCR);
    }

    private static String getPDFText(File file)
    {
        String text = "";
        try (PDDocument document = PDDocument.load(file))
        {
            text = new PDFTextStripper().getText(document);
        }
        catch (Exception e)
        {
//            e.printStackTrace();
        }
        return text;
    }

    private static String getDOCText(File file)
    {
        String text = null;
        try
        {
            FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
            HWPFDocument document = new HWPFDocument(inputStream);
            WordExtractor extractor = new WordExtractor(document);
            text = extractor.getText();
        }
        catch (Exception e)
        {
//            e.printStackTrace();
        }
        return text;
    }

    private static String getDOCXText(File file)
    {
        String text = null;
        try
        {
            FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
            XWPFDocument document = new XWPFDocument(inputStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            text = extractor.getText();
        }
        catch (Exception e)
        {
//            e.printStackTrace();
        }
        return text;
    }

    private static String getRTFText(File file)
    {
        String text = null;
        try
        {
            FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
            ContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            RTFParser parser = new RTFParser();

            parser.parse(inputStream, handler, metadata, new ParseContext());

            text = handler.toString();
        }
        catch (Exception e)
        {
//            e.printStackTrace();
        }
        return text;
    }

    private static String getOCRText(File file)
    {
        System.out.println("[OCR] scanning " + file.getName());
        String text = "";
        try
        {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("tessdata");
            tesseract.setLanguage("chi_sim");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);
            text = removeSpace(tesseract.doOCR(file));
//            System.out.println(text);

        }
        catch (Exception e)
        {
//            text = null;
//            e.printStackTrace();
        }
        System.out.println("[OCR] scanned " + file.getName());
        return text;
    }

    private static String getSimpleText(File file)
    {
        StringBuilder builder = new StringBuilder("");
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            builder.append(reader.readLine() + "  ");
        }
        catch (Exception e)
        {
//            e.printStackTrace();
        }
        return builder.toString();
    }

    private static String removeSpace(String text)
    {
        Pattern pattern = Pattern.compile("\\p{IsHan} +\\p{IsHan}");
        Matcher matcher = pattern.matcher(text);

        // Replace spaces between Chinese characters with an empty string
        String result = matcher.replaceAll(matchResult -> matchResult.group().replace(" ", ""));
        matcher = pattern.matcher(result);
        result = matcher.replaceAll(matchResult -> matchResult.group().replace(" ", ""));


        return result;
    }

    public static void saveSingleResults(List<SingleResult> results)
    {
        saveJSON(ConfigUtils.BACKEND_PATH + ConfigUtils.SINGLE_RESULT_PATH, results);
    }

    public static void saveChartResults(List<ChartResult> results)
    {
        saveJSON(ConfigUtils.BACKEND_PATH + ConfigUtils.CHART_RESULT_PATH, results);
    }

    public static List<SingleResult> getSingleResults()
    {
        return (List<SingleResult>) getJSON(ConfigUtils.BACKEND_PATH + ConfigUtils.SINGLE_RESULT_PATH, new TypeReference<List<SingleResult>>(){});
    }

    public static List<ChartResult> getChartResults()
    {
        return (List<ChartResult>) getJSON(ConfigUtils.BACKEND_PATH + ConfigUtils.CHART_RESULT_PATH, new TypeReference<List<ChartResult>>(){});
    }

    public static void saveJSON(String filePath, Object obj)
    {
        try
        {
            Writer writer = new FileWriter(filePath, StandardCharsets.UTF_8);
            JSONWriter jsonWriter = new JSONWriter(writer);
            jsonWriter.writeObject(obj);
            jsonWriter.flush();
            jsonWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Object getJSON(String filePath, TypeReference type)
    {
        Object result = null;
        File file = new File(filePath);
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
            JSONReader jsonReader = new JSONReader(reader);
            result = jsonReader.readObject(type);
            jsonReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static void copySingleCases(String folderPath)
    {
        List<SingleResult> singleResults = getSingleResults();
        if (singleResults != null)
        {
            for (SingleResult result : singleResults)
            {
                try
                {
                    Files.copy(Paths.get(result.getFilePath()), Paths.get(folderPath + "/" + result.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
                catch (Exception e)
                {

                }
            }
        }
    }



    public static void saveChartCases(String filePath)
    {
        List<ChartResult> chartResults = getChartResults();
        if (chartResults != null)
        {
            XSSFWorkbook workbook = new XSSFWorkbook();
            for (ChartResult chartResult : chartResults)
            {
                for (SheetResult sheetResult : chartResult.getSheetResults())
                {
                    Sheet sheet = workbook.createSheet(chartResult.getFileName() + "-" + sheetResult.getSheetName());
                    Row header = sheet.createRow(0);
                    List<String> columnNames = sheetResult.getColumnNames();
                    for (int j = 0;j < columnNames.size();j++)
                    {
                        header.createCell(j).setCellValue(columnNames.get(j));
                    }

                    for (int i = 0;i < sheetResult.getValues().size(); i++)
                    {
                        List<String> vals = sheetResult.getValues().get(i);
                        Row row = sheet.createRow(i + 1);
                        for (int j = 0;j < vals.size();j++)
                        {
                            String text = vals.get(j);
                            text = text.replaceAll("<span style='color: orangered'>", "").replace("</span>", "").replaceAll("&nbsp;", " ");
                            row.createCell(j).setCellValue(text);
                        }
                    }
                }
            }

            if (!filePath.endsWith(".xlsx"))
            {
                filePath += ".xlsx";
            }
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                workbook.write(fileOutputStream);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    workbook.close();
                }
                catch (Exception e)
                {}
            }
        }
    }

}
