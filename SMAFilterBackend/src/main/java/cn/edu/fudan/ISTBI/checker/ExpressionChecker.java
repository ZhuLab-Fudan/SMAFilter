package cn.edu.fudan.ISTBI.checker;

import cn.edu.fudan.ISTBI.entity.ReportPattern;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;

//import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExpressionChecker
{
    Map<String, String> tokenToVariableMap;
    JexlExpression expression;

    public ExpressionChecker(String regulation)
    {
        tokenToVariableMap = new HashMap<>();
        String tmp = regulation.replaceAll("(?i)AND", " ").replaceAll("(?i)OR", " ").replaceAll("(?i)NOT", " ").replace("(", " ").replace(")", " ");
        List<String> tokens = Arrays.stream(tmp.split(" ")).filter(t -> !t.equals("")).collect(Collectors.toList());
        Collections.sort(tokens, Comparator.comparing(String::length).reversed());
        for (int i = 0;i < tokens.size();i++)
        {
            tokenToVariableMap.put(tokens.get(i), "___" + i);
        }

        regulation = regulation.replaceAll("(?i)AND", " && ").replaceAll("(?i)OR", " || ").replaceAll("(?i)NOT", " !");

        for (String token : tokens)
        {
            regulation = regulation.replace(token, tokenToVariableMap.get(token));
        }

        JexlEngine jexl = new Engine();
        try
        {
            expression = jexl.createExpression(regulation);
        }
        catch (Exception e)
        {
            expression = null;
            e.printStackTrace();
        }
    }

    public boolean isValidExpression()
    {
        if (expression == null)
        {
            return false;
        }
        else
        {
            JexlContext context = new MapContext();
            for (Map.Entry<String, String> entry : tokenToVariableMap.entrySet())
            {
                context.set(entry.getValue(), true);
            }
            try
            {
                boolean res = (boolean)expression.evaluate(context);
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

    }

    public List<String> calculate(String test, ReportPattern reportPattern, boolean shouldCutFromText)
    {
        List<String> res = null;
        JexlContext context = new MapContext();
        String scopeStartText = null;
        String scopeEndText = null;
        if (reportPattern != null)
        {
            scopeStartText = reportPattern.getScopeStartText();
            scopeEndText = reportPattern.getScopeEndText();
        }
        List<Map.Entry<Integer, Integer>> highlightRegions = new ArrayList<>();

        for (Map.Entry<String, String> entry : tokenToVariableMap.entrySet())
        {
//            int idx = test.lastIndexOf(entry.getKey());
            // for huashan only


            int scopeStart = getFirstIndex(test, scopeStartText);
            int scopeEnd = getLastIndex(test, scopeEndText);


            Map.Entry<Integer, Integer> matchRegion = getMatchText(test, entry.getKey(), scopeStart, scopeEnd);

            if (matchRegion == null)
            {
                context.set(entry.getValue(), false);
            }
            else
            {
                context.set(entry.getValue(), true);
                insertIntoHighlightRegions(highlightRegions, matchRegion);
            }
        }
        boolean isWanted = (boolean)expression.evaluate(context);
        if (isWanted)
        {
            res = new ArrayList<>();
            if (shouldCutFromText)
            {
                for (Map.Entry<Integer, Integer> matchRegion : highlightRegions)
                {
                    int begin = (matchRegion.getKey() < 10)? 0 : matchRegion.getKey() - 10;
                    int end = (matchRegion.getValue() < test.length() - 10)? matchRegion.getValue() + 10 : test.length();
                    String tmp = "... " +
                            test.substring(begin, matchRegion.getKey()) +
                            "<span style='color: orangered'> " +
                            test.substring(matchRegion.getKey(), matchRegion.getValue()) +
                            " </span>" +
                            test.substring(matchRegion.getValue(), end) +
                            " ...";
                    res.add(tmp.replace("\n", "&nbsp;&nbsp;").replace("\r", "&nbsp;&nbsp;"));
                }
            }
            else
            {
                Collections.sort(highlightRegions, (o1, o2) -> o2.getKey() - o1.getKey());   // large first
                String resText = test;
                for (Map.Entry<Integer, Integer> matchRegion : highlightRegions)
                {
                    resText = resText.substring(0, matchRegion.getKey()) +
                            "<span style='color: orangered'> " +
                            resText.substring(matchRegion.getKey(), matchRegion.getValue()) +
                            " </span>" +
                            resText.substring(matchRegion.getValue());
                }
                res.add(resText);
            }
        }
        return res;
    }

    private void insertIntoHighlightRegions(List<Map.Entry<Integer, Integer>> highlightRegions, Map.Entry<Integer, Integer> insertRegion)
    {
        // start is included in text, end is not included
        boolean containNewRegionStart = false;
        boolean containNewRegionEnd = false;
        boolean isContainedInNewRegion = false;
        Map.Entry<Integer, Integer> originRegion = null;
        for (Map.Entry<Integer, Integer> highlightRegion : highlightRegions)
        {
            containNewRegionStart = highlightRegion.getKey() <= insertRegion.getKey()
                    && highlightRegion.getValue() >= insertRegion.getKey();  // when h.end = i.start, they are directly connected. They can be merged into one
            containNewRegionEnd = highlightRegion.getKey() <= insertRegion.getValue()
                    && highlightRegion.getValue() >= insertRegion.getValue();  // when h.start = i.end, they are directly connected
            isContainedInNewRegion = highlightRegion.getKey() >= insertRegion.getKey()
                    && highlightRegion.getValue() <= insertRegion.getValue();

            // if yes, then use recursive to add new region
            // note that remove originRegion while iteration is dangerous
            if (containNewRegionStart || containNewRegionEnd || isContainedInNewRegion)
            {
                originRegion = highlightRegion;
                break;
            }
            // else: this h has no intersection with i, just pass
        }
        if (containNewRegionStart && containNewRegionEnd)  // h contains i, hhhiiihhh
        {
            // do nothing. No need to insert anymore.
        }
        else if (containNewRegionStart)  // hhhihihiiii
        {
            highlightRegions.remove(originRegion);
            insertIntoHighlightRegions(highlightRegions, new AbstractMap.SimpleEntry<>(originRegion.getKey(), insertRegion.getValue()));
        }
        else if (containNewRegionEnd)   // iiiihihihhhh
        {
            highlightRegions.remove(originRegion);
            insertIntoHighlightRegions(highlightRegions, new AbstractMap.SimpleEntry<>(insertRegion.getKey(), originRegion.getValue()));
        }
        else if (isContainedInNewRegion)    // iiihhhiii
        {
            highlightRegions.remove(originRegion);
            insertIntoHighlightRegions(highlightRegions, insertRegion);
        }
        else
        {
            highlightRegions.add(insertRegion);
        }
    }



    // get last start index of pattern(regex) in text
    private int getLastIndex(String text, String pattern)
    {
        int res = -1;
        if (pattern != null && !pattern.equals(""))
        {
            Pattern ptn = Pattern.compile(pattern);
            Matcher matcher = ptn.matcher(text);
            while (matcher.find())
            {
                res = matcher.start();
            }
        }
        return res;
    }

    // get first start index of pattern(regex) in text
    private int getFirstIndex(String text, String pattern)
    {
        int res = -1;
        if (pattern != null && !pattern.equals(""))
        {
            Pattern ptn = Pattern.compile(pattern);
            Matcher matcher = ptn.matcher(text);
            if (matcher.find())
            {
                res = matcher.start();
            }
        }
        return res;
    }

    private Map.Entry<Integer, Integer> getMatchText(String text, String pattern, int scopeStart, int scopeEnd)
    {
        Map.Entry<Integer, Integer> result = null;
        if (scopeEnd == -1)
        {
            scopeEnd = Integer.MAX_VALUE;
        }
        if (pattern != null && !pattern.equals(""))
        {
            Pattern ptn = Pattern.compile(pattern);
            Matcher matcher = ptn.matcher(text);
            while (matcher.find())
            {
                if (matcher.start() >= scopeStart && matcher.end() <= scopeEnd)
                {
                    result = new AbstractMap.SimpleEntry<>(matcher.start(), matcher.end());
//                    String matched = "<span style='color: orangered'>" + text.substring(matcher.start(), matcher.end()) + "</span>";
//                    if (shouldCutFromText)
//                    {
//                        int begin = (matcher.start() > 10)? matcher.start() - 10 : 0;
//                        int end = (matcher.end() < text.length() - 10)? matcher.end() + 10 : text.length();
//                        result = text.substring(begin, matcher.start()) + matched + text.substring(matcher.end(), end);
//                    }
//                    else
//                    {
//                        result = text.substring(0, matcher.start()) + matched + text.substring(matcher.end());
//                    }
                    break;
                }
            }
        }



        return result;
    }

    public List<String> getExampleTexts()
    {

        List<String> result = new ArrayList<>();
        List<Map.Entry<String, String>> params = new ArrayList<>(tokenToVariableMap.entrySet());
        int paramCount = tokenToVariableMap.size();
        for (int i = 0;i < (1 << paramCount); i++)
        {
            StringBuilder text = new StringBuilder("... ");
            JexlContext context = new MapContext();
//            for (Map.Entry<String, String> param : params)
            for (int j = 0;j < paramCount; j++)
            {
                Map.Entry<String, String> param = params.get(j);
                if (((i >> j) & 1) == 1)
                {
                   context.set(param.getValue(), true);
                   text.append(param.getKey()).append(" ... ");
                }
                else
                {
                   context.set(param.getValue(), false);
                }
            }
            if ((boolean)expression.evaluate(context))
            {
                result.add(text.toString());
            }
        }

        return result;
    }

//    public static void main(String[] args)
//    {
//        ExpressionChecker checker = new ExpressionChecker("a and b or c");
//        List<String> res = checker.getExampleTexts();
//        System.out.println(checker.isValidExpression());
////        System.out.println(checker.isWanted("a"));
////        System.out.println(checker.isWanted("b"));
////        System.out.println(checker.isWanted("a b"));
//    }

    //    public static void main(String[] args)
//    {
//        ExpressionChecker.setRegulation("(神经源性 aNd 前角 and (上下肢 or (上肢 and 下肢)))");
//        System.out.println(ExpressionChecker.calculate("神经源性上肢前角上下肢"));
//        System.out.println(ExpressionChecker.calculate("神经源性前角上下肢"));
//        System.out.println(ExpressionChecker.calculate("神经源性上肢前角下肢"));
//        System.out.println(ExpressionChecker.calculate("神经源性上前角下肢"));
//    }

//    public static void main(String[] args)
//    {
//        ExpressionChecker checker = new ExpressionChecker("(神经源性 aNd 前角 and (上下肢 or (上肢 and 下肢)))");
//        List<String> res1 = checker.calculate("神经源性上肢前角上下肢");
//        List<String> res2 = checker.calculate("神经源性上肢前角下肢");
//        List<String> res3 = checker.calculate("神经源性上下肢前角");
//        List<String> res4 = checker.calculate("神经源上下肢前角");
//        System.out.println("?");
////    }
//
//    public static void main(String[] args)
//    {
//        String t1 = "abcde";
//        String t2 = "b.*d";
//        Pattern pattern = Pattern.compile(t2);
//        Matcher matcher = pattern.matcher(t1);
//        System.out.println(matcher.find());
//        System.out.println(matcher.start());
//        System.out.println(matcher.end());
//    }

//    public static void main(String[] args)
//    {
//        // test region combination
//        List<Map.Entry<Integer, Integer>> originSet = new ArrayList<>();
//        originSet.add(new AbstractMap.SimpleEntry<>(2, 9));
//        originSet.add(new AbstractMap.SimpleEntry<>(10, 15));
//        originSet.add(new AbstractMap.SimpleEntry<>(20, 25));
//        originSet.add(new AbstractMap.SimpleEntry<>(30, 35));
//        originSet.add(new AbstractMap.SimpleEntry<>(1, 8));
//        originSet.add(new AbstractMap.SimpleEntry<>(3, 5));
//        originSet.add(new AbstractMap.SimpleEntry<>(9, 16));
//        originSet.add(new AbstractMap.SimpleEntry<>(19, 26));
//        originSet.add(new AbstractMap.SimpleEntry<>(29, 36));
//        Set<Map.Entry<Integer, Integer>> regions = new HashSet<>();
//
//        for (Map.Entry<Integer, Integer> region : originSet)
//        {
//            insertIntoHighlightRegions(regions, region);
//            System.out.print("Insert (" + region.getKey() + " -> " + region.getValue() + "). Total size = " + regions.size() + ", ");
//            for (Map.Entry<Integer, Integer> r : regions)
//            {
//                System.out.print("(" + r.getKey() + " -> " + r.getValue() + ") ");
//            }
//            System.out.println("");
//        }
//    }

//    public static void main(String[] args)
//    {
//        List<Integer> l = new ArrayList<>();
//        l.add(1);
//        l.add(2);
//        l.add(3);
//        l.add(4);
//        Collections.sort(l, (o1, o2) -> o2 - o1);
//        System.out.println(l);
//    }
}
