package cn.edu.fudan.ISTBI.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Center
{
    int id;
    String name;
    List<ReportPattern> reportPatternList;
    List<Filter> filters;
    List<ChartFilter> chartFilters;
}
