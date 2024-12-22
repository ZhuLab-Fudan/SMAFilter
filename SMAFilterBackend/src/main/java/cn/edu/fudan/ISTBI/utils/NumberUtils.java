package cn.edu.fudan.ISTBI.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils
{
    public static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    public static List<Double> extractNumbers(String text)
    {
        Matcher matcher = pattern.matcher(text);
        List<Double> numbers = new ArrayList<>();
        while (matcher.find())
        {
            numbers.add(Double.parseDouble(matcher.group()));
        }
        return numbers;
    }
}
