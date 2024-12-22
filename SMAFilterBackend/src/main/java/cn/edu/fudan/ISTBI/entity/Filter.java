package cn.edu.fudan.ISTBI.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filter
{
    int maxAge;
    int minAge;
    int ageType;
    String text;
}
