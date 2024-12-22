package cn.edu.fudan.ISTBI.checker;

import cn.edu.fudan.ISTBI.entity.Filter;
import cn.edu.fudan.ISTBI.entity.ReportPattern;
import cn.edu.fudan.ISTBI.result.Result;
import cn.edu.fudan.ISTBI.result.SingleResult;
import cn.edu.fudan.ISTBI.utils.IOUtils;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SingleFileChecker implements FileChecker
{
    final List<ReportPattern> reportPatterns;
    final List<ExpressionChecker> checkers;
    final List<Filter> filters;


    public SingleFileChecker(List<ReportPattern> reportPatterns, List<Filter> filters)
    {
        this.checkers = filters.stream().map(filter -> new ExpressionChecker(filter.getText())).collect(Collectors.toList());
        this.filters = filters;
        this.reportPatterns = reportPatterns;
    }

    @Override
    public Map.Entry<Result, String> evaluate(File file)
    {
        SingleResult result = null;
        String errorInfo = null;
        Matcher m;
        Map.Entry<String, Boolean> getTextRes = IOUtils.getText(file);
        String text = getTextRes.getKey();
        Boolean isOCR = getTextRes.getValue();


        boolean foundAge = false;

        int age = -1;


        // if have age. try different report patterns, until this is the target patient
        for (ReportPattern reportPattern : reportPatterns)
        {
            if (reportPattern.getAgePattern() == null)
            {
                continue;
            }
            Pattern pattern = Pattern.compile(reportPattern.getAgePattern());
            int ageIndexInPattern = reportPattern.getIndex();
            m = pattern.matcher(text);
            if (m.find())
            {
                foundAge = true;
                age = Integer.parseInt(m.group(ageIndexInPattern));
                boolean foundFilter = false;
                for (int j = 0;j < filters.size();j++)
                {
                    Filter filter = filters.get(j);
                    boolean isSmallerThan = filter.getAgeType() == 0 && age <= filter.getMaxAge();
                    boolean isBetween = filter.getAgeType() == 1 && age <= filter.getMaxAge() && age >= filter.getMinAge();
                    boolean isLargerThan = filter.getAgeType() == 2 && age >= filter.getMinAge();
                    boolean isTolerate = filter.getAgeType() == 3;
                    if (isSmallerThan || isBetween || isLargerThan || isTolerate)
                    {
                        foundFilter = true;
                        List<String> keywords = checkers.get(j).calculate(text.replace("\r", "").replace("\n", ""), reportPattern, true);
                        if (keywords!= null)  // is the target
                        {
                            if (isOCR)
                            {
                                keywords.add(0, "<span style='color: blue;'>该文件为图片，文字识别可能有误</span>");
                            }
                            result = new SingleResult(file.getPath(), file.getName(), age, keywords);

                            break;
                        }
                    }
                }
                if (!foundFilter)
                {
                    errorInfo = "Not found filter for age " + age;
                }
                // should not break even if find the age. There may be many report patterns have same age pattern
//                break;
            }
        }
        if (!foundAge)
        {
            errorInfo = "Unrecognisable age pattern";
        }

        if (result == null)  // is not result, then check no age report pattern with global filters
        {
            for (ReportPattern reportPattern : reportPatterns)
            {
                if (reportPattern.getAgePattern() == null)
                {
                    for (int j = 0;j < filters.size();j ++)
                    {
                        Filter filter = filters.get(j);
                        if (filter.getAgeType() == 3)  // 3 is for all age
                        {
                            errorInfo = null;
                            List<String> keywords = checkers.get(j).calculate(text.replace("\r", "").replace("\n", ""), reportPattern, true);
                            if (keywords != null)  // is the target
                            {
                                if (isOCR)
                                {
                                    keywords.add(0, "<span style='color: blue;'>该文件为图片，文字识别可能有误</span>");
                                }
                                result = new SingleResult(file.getPath(), file.getName(), age, keywords);
                                break;
                            }
                        }
                    }
                    if (result != null)
                    {
                        break;
                    }
                }
            }

        }

        return new AbstractMap.SimpleEntry<>(result, errorInfo);
    }
}
