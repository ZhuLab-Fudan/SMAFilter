package cn.edu.fudan.ISTBI.test;

import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.rtf.RTFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMAFilterTest
{
    public static void main(String[] args)
    {
//        textPDF();
//        OCRPDF();
//        textDOCX();
//        textDOC();
//        textRTF();
        String test = "神 经";
        String t = removeSpace(test);
        System.out.println(t);
    }


    public static void OCRPDF()
    {
        String path = "G:\\huashan_hospital\\tests\\0000张三.pdf";
//        String path = "G:\\huashan_hospital\\tests\\0000张三.png";
//        String path = "G:\\huashan_hospital\\tests\\0000张三.tif";
//        String path = "G:\\huashan_hospital\\tests\\0000张三.tiff";
        File file = new File(path);
        try
        {
            Tesseract tesseract = new Tesseract();
//            tesseract.setDatapath("E:\\a-MyCode\\OCR");
            tesseract.setDatapath("src/main/resources/tessdata");
            tesseract.setLanguage("chi_sim");
//            tesseract.set
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);
            String text = tesseract.doOCR(file);
            String res = removeSpace(text);
            System.out.println(res);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void textPDF()
    {
        String path = "G:\\huashan_hospital\\40zongyuanlower_pdf\\0000丁文仙.pdf";
        File file = new File(path);
        try (PDDocument document = PDDocument.load(file))
        {
            String text = new PDFTextStripper().getText(document);
            System.out.println(text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void textDOCX()
    {
        String path = "G:\\huashan_hospital\\tests\\0000张三.docx";
        try (
                FileInputStream inputStream = new FileInputStream(path);
                XWPFDocument document = new XWPFDocument(inputStream)
        )
        {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            String text = extractor.getText();
            System.out.println(text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void textDOC()
    {
        String path = "G:\\huashan_hospital\\tests\\0000张三.doc";
        try (
                FileInputStream inputStream = new FileInputStream(path);
                HWPFDocument document = new HWPFDocument(inputStream)
        )
        {
            WordExtractor extractor = new WordExtractor(document);
            String text = extractor.getText();
            System.out.println(text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void textRTF()
    {
        String path = "G:\\huashan_hospital\\tests\\0000张三.rtf";
        try (FileInputStream inputStream = new FileInputStream(path))
        {
            ContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            RTFParser parser = new RTFParser();

            parser.parse(inputStream, handler, metadata, new ParseContext());

            String text = handler.toString();
            System.out.println(text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static String removeSpace(String text)
    {
        Pattern pattern = Pattern.compile("\\p{IsHan} +\\p{IsHan}");
        Matcher matcher = pattern.matcher(text);

        // Replace spaces between Chinese characters with an empty string
        String result = matcher.replaceAll(matchResult -> matchResult.group().replace(" ", ""));

        return result;
    }
}
