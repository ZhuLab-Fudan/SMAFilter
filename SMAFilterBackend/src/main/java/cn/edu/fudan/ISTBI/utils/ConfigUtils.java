package cn.edu.fudan.ISTBI.utils;

import cn.edu.fudan.ISTBI.entity.Center;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.TypeReference;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils
{
    public static String BACKEND_PATH;
    public static final String CONFIG_PATH = "application.json";
    public static final String SINGLE_RESULT_PATH = "result.json";
    public static final String CHART_RESULT_PATH = "chartResult.json";

    public static void setBackendPath(String path)
    {
        BACKEND_PATH = path;
        checkFileExists(BACKEND_PATH + CONFIG_PATH);
        checkFileExists(BACKEND_PATH + SINGLE_RESULT_PATH);
        checkFileExists(BACKEND_PATH + CHART_RESULT_PATH);
    }

    private static void checkFileExists(String filePath)
    {
        File file = new File(filePath);
        if (!file.exists())
        {
            try
            {
                Writer writer = new FileWriter(filePath, StandardCharsets.UTF_8);
                writer.write("[]");
                writer.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static List<Center> getConfig()
    {
        return getConfig(ConfigUtils.BACKEND_PATH + ConfigUtils.CONFIG_PATH);
    }

    public static List<Center> getConfig(String configPath)
    {
        List<Center> centers = new ArrayList<>();
        File file = new File(configPath);
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
            JSONReader jsonReader = new JSONReader(reader);
            centers = jsonReader.readObject(new TypeReference<>(){});
            jsonReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return centers;
    }

    public static void saveConfig(List<Center> centers)
    {
        try
        {
            Writer writer = new FileWriter(ConfigUtils.BACKEND_PATH + ConfigUtils.CONFIG_PATH, StandardCharsets.UTF_8);
            JSONWriter jsonWriter = new JSONWriter(writer);
            jsonWriter.writeObject(centers);
            jsonWriter.flush();
            jsonWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
