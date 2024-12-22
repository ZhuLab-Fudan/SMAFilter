package cn.edu.fudan.ISTBI.thread;

import cn.edu.fudan.ISTBI.checker.ChartChecker;
import cn.edu.fudan.ISTBI.checker.SingleFileChecker;
import cn.edu.fudan.ISTBI.entity.Center;
import cn.edu.fudan.ISTBI.result.ChartResult;
import cn.edu.fudan.ISTBI.result.Result;
import cn.edu.fudan.ISTBI.result.SingleResult;
import cn.edu.fudan.ISTBI.utils.IOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ProcessThread extends Thread
{
    final String path;
    final Center center;
    final boolean shouldProcessSubFolder;

    List<String> info = new ArrayList<>();
    double percentage;

    List<File> fileList;
    Map<Future, String> fileNames = new HashMap<>();
    List<Future<?>> futures = null;

    ExecutorService executorService;

    List<SingleResult> singleResults;
    List<ChartResult> chartResults;

    public ProcessThread(String path, Center center, boolean shouldProcessSubFolder)
    {
        this.path = path;
        this.center = center;
        this.shouldProcessSubFolder = shouldProcessSubFolder;
    }


    @Override
    public void run()
    {
        // process
//        System.out.println(path);
//        System.out.println(center.getName());
        percentage = 0;
        if (info.size() > 0)
        {
            info = new ArrayList<>();
        }
        fileNames = new HashMap<>();
        process();

    }

    public synchronized List<String> getInfo()
    {
        return info;
    }

    public synchronized double getPercentage()
    {
        return percentage;
    }

    public void stopAll()
    {
        if (futures != null)
        {
            for (Future future:futures)
            {
                future.cancel(true);
            }
        }
    }

    private void process()
    {
        singleResults = new ArrayList<>();
        chartResults = new ArrayList<>();
        fileList = new ArrayList<>();
        futures = new ArrayList<>();

        addFiles(new File(path));

        if (fileList.size() > 0)
        {
            processFiles();
        }
        else
        {
            info.add("<span style='color: orange'>[WARN]</span> No file to process");
            percentage = 1;
        }
    }

    private void addFiles(File folder)
    {
        File[] files = folder.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    if (shouldProcessSubFolder)
                    {
                        addFiles(file);
                    }
                }
                else
                {
                    fileList.add(file);
                }
            }
        }
    }

    private void processFiles()
    {
        int cores = Runtime.getRuntime().availableProcessors();
        int originCores = cores;
        if (cores > 2 && cores <= 4)
        {
            cores = cores - 1;
        }
        else if (cores > 4)
        {
            cores = cores - 2;
        }
        else if (cores > 10)
        {
            cores = cores - 3;
        }
        info.add("[INFO] " + originCores + " cores available. Use " + cores);
        executorService = Executors.newFixedThreadPool(cores);

        SingleFileChecker singleFileChecker = new SingleFileChecker(center.getReportPatternList(), center.getFilters());
        ChartChecker chartChecker = new ChartChecker(center.getChartFilters());

        for (File file : fileList)
        {
            String fileName = file.getName();
            ChildThread thread;
            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))
            {
                thread = new ChildThread(file, chartChecker);
            }
            else
            {
                thread = new ChildThread(file, singleFileChecker);
            }
            Future f = executorService.submit(thread, thread);
            fileNames.put(f, file.getName());
            futures.add(f);
        }

        executorService.shutdown();


        for (Future future : futures)
        {
            try
            {
                ChildThread thread = (ChildThread) future.get();
                Result result = thread.getResult();
                String errorInfo = thread.getErrorInfo();
                if (result != null)
                {
                    if (result instanceof SingleResult)
                    {
                        singleResults.add((SingleResult) result);
                    }
                    else if (result instanceof ChartResult)
                    {
                        chartResults.add((ChartResult) result);
                    }
                }
                if (errorInfo == null)
                {
                    info.add("[INFO] (" + singleResults.size() + "/" + chartResults.size() + "/" + info.size() + "/" + fileList.size() + ") " + thread.getFileName() + " processed.");
                }
                else
                {
                    info.add("<span style='color: orange'>[WARN]</span> (" + singleResults.size() + "/" + chartResults.size() +  "/" + info.size() + "/" + fileList.size() + ") " + thread.getFileName() + " failed. " + errorInfo);
                }
                percentage = (double)(info.size()-1) / fileList.size();
            }
            catch (Exception e)
            {
                if (!(e instanceof CancellationException))
                {
                    info.add("<span style='color: red'>[ERROR]</span> (" + singleResults.size() + "/" + chartResults.size() + "/" + info.size() + "/" + fileList.size() + ") " + fileNames.get(future) + " failed. " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        IOUtils.saveSingleResults(singleResults);
        IOUtils.saveChartResults(chartResults);
    }
}
