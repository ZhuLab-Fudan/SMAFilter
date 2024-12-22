package cn.edu.fudan.ISTBI.checker;

import cn.edu.fudan.ISTBI.result.Result;

import java.io.File;
import java.util.Map;

public interface FileChecker
{
    Map.Entry<Result, String> evaluate(File file);
}
