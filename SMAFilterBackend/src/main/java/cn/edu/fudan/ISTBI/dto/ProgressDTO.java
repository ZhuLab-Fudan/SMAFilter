package cn.edu.fudan.ISTBI.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgressDTO
{
    List<String> info;
    String percentage;
    String remainedTime;
}
