package cn.edu.fudan.ISTBI.test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test2
{
    public static void main(String[] args) throws Exception
    {
        String folder = "G:\\huashan_hospital\\zongyuanResult";
        List<String> tar = new ArrayList<>();
        List<String> res;
//        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File("G:\\huashan_hospital\\40zongyuanlower_result\\zhuyuan.csv")));
        BufferedReader reader = new BufferedReader(new FileReader("G:\\huashan_hospital\\40zongyuanlower_result\\zhuyuan.csv"));
        String line;
        line = reader.readLine();
        while (line != null)
        {
            tar.add(line);
            line = reader.readLine();
        }
        res = Arrays.stream(new File(folder).listFiles()).map(file -> file.getName()).collect(Collectors.toList());
        for (String r : res)
        {
            if (!tar.contains(r))
            {
                System.out.println("res has " + r + " but tar do not have");
            }
        }
    }
}
