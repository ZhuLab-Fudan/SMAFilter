package cn.edu.fudan.ISTBI.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportPattern
{
    int index;
    String scopeStartText;
    String scopeEndText;
    String agePattern;
}
