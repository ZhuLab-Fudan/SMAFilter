package cn.edu.fudan.ISTBI.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SingleResult extends Result
{
    String filePath;
    String fileName;
    int age;
    List<String> keywords;
}
