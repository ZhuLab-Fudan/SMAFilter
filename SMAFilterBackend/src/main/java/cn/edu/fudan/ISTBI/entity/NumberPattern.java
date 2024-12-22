package cn.edu.fudan.ISTBI.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NumberPattern
{
    double max;
    double min;
    int type;   // '小于', '介于', '大于'
}
