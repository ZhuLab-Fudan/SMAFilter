package cn.edu.fudan.ISTBI.thread;

import cn.edu.fudan.ISTBI.checker.FileChecker;
import cn.edu.fudan.ISTBI.result.Result;

import java.io.File;
import java.util.Map;

public class ChildThread extends Thread
{

    final File file;
    final FileChecker fileChecker;
//    final List<ReportPattern> reportPatterns;
//    final List<ExpressionChecker> checkers;
//    final List<Filter> filters;
    Result result = null;
    String errorInfo = null;


    public ChildThread(File file, FileChecker checker)
    {
        this.file = file;
        this.fileChecker = checker;
    }

    @Override
    public void run()
    {
        System.out.println("[INFO] start to process " + file.getName());
        System.out.println("Thread " + currentThread() + " handles " + file.getName());

        Map.Entry<Result, String> res = fileChecker.evaluate(file);
        result = res.getKey();
        errorInfo = res.getValue();

        System.out.println("[INFO] end of " + file.getName());
    }

    public Result getResult()
    {
        return result;
    }

    public String getFileName()
    {
        return file.getName();
    }

    public String getErrorInfo()
    {
        return errorInfo;
    }
}
