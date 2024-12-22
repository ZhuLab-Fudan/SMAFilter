package cn.edu.fudan.ISTBI.test;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Huashan
{

    public static void main(String[] args)
    {
//        String x = "abc";
//        String y = "bc";
//        String z = "cb";
//        System.out.println(x.indexOf(y));
//        System.out.println(x.indexOf(z));
//        if (true)
//        {
//            return;
//        }
        String folder = "G:\\huashan_hospital\\40zongyuanlower_pdf";
        String resPath = "G:\\huashan_hospital\\zongyuanResult";
//        String folder = "G:\\huashan_hospital\\40xiyuanlower_pdf";
//        String resPath = "G:\\huashan_hospital\\xiyuanResult";
        File[] files = new File(folder).listFiles();
        String path;
        Pattern pattern = Pattern.compile("年龄\\s*[\\:：]\\s*(\\d+)");
        Matcher m;
        File file;
        Path src;
        Path dst;
        int count = 0;
        if (files != null)
        {
            for (int i = 0;i < files.length;i++)
            {
                file = files[i];
                if (!file.isDirectory())
                {
                    try (PDDocument document = PDDocument.load(file))
                    {
                        String text = new PDFTextStripper().getText(document);
                        if (text.contains("神经源性") && text.contains("脊髓前角") && text.contains("上下肢"))
                        {
                            m = pattern.matcher(text);
                            if (m.find())
                            {
                                String ageStr = m.group(1);
                                int age = Integer.parseInt(ageStr);
                                if (age <= 40)
                                {
                                    count ++;
                                    src = Paths.get(file.getPath());
                                    dst = Paths.get(resPath + "\\" + file.getName());
                                    Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                System.out.print("[INFO] found " + count  + " in " + (i + 1) + "/" + files.length + "\r");

            }
        }

    }
}
