package cn.edu.fudan.ISTBI.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rule
{
    int columnSelectType;  // 0 for index, 1 for name
    int columnIndex;
    String columnName;
    int columnType;     // 0 for number, 1 for string
    NumberPattern numberPattern;
    String stringPattern;
}
