package cn.edu.fudan.ISTBI.checker;

import cn.edu.fudan.ISTBI.entity.NumberPattern;
import cn.edu.fudan.ISTBI.utils.NumberUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CellChecker
{
    ExpressionChecker expressionChecker;
    NumericChecker numericChecker;

    public CellChecker(String expression)
    {
        expressionChecker = new ExpressionChecker(expression);
        numericChecker = null;
    }

    public CellChecker(NumberPattern numberPattern)
    {
        numericChecker = new NumericChecker(numberPattern);
        expressionChecker = null;
    }

    public String evaluate(String input)
    {
        input = input.replaceAll("\t", "");
        String result = null;
        if (expressionChecker != null)
        {
            if (expressionChecker.isValidExpression())
            {
                List<String> keywords = expressionChecker.calculate(input, null, false);
                if (keywords != null)
                {
                    StringBuilder builder = new StringBuilder(keywords.get(0));
                    for (int i = 1;i < keywords.size();i++)
                    {
                        builder.append("\n");
                        builder.append(keywords.get(i));
                    }
                    result = builder.toString();
                }
            }
        }
        else  // number
        {
            List<Double> numbers = NumberUtils.extractNumbers(input);
            for (Double num : numbers)
            {
                if (numericChecker.evaluate(num))
                {
                    result = input;
                    break;
                }
            }
        }
        return result;
    }

//    public static void main(String[] args)
//    {
//        List<Result> resultList = new ArrayList<>();
//        resultList.add(new SingleResult("path", "name", 0, new ArrayList<>()));
//        resultList.add(new ChartEntryResult("path", "name", "sheet", List.of("Column1", "Column2"), List.of(List.of("1", "症状A"), List.of("2", "症状B"))));
//        try
//        {
//            Writer writer = new FileWriter("./test.json", StandardCharsets.UTF_8);
//            JSONWriter jsonWriter = new JSONWriter(writer);
//            jsonWriter.writeObject(resultList);
//            jsonWriter.flush();
//            jsonWriter.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        try
//        {
//            BufferedReader reader = new BufferedReader(new FileReader(new File("./test.json"), StandardCharsets.UTF_8));
//            JSONReader jsonReader = new JSONReader(reader);
//            List<Result> results = jsonReader.readObject(new TypeReference<>(){});
//            jsonReader.close();
//
//            for (Result result : results)
//            {
//                // TODO: 16/01/2024
////                Files.copy(Paths.get(result.getFilePath()), Paths.get(info.getPath() + "/" + result.getFileName()), StandardCopyOption.REPLACE_EXISTING);
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//    }
}
