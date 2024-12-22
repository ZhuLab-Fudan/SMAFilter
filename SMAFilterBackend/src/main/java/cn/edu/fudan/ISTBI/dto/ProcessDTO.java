package cn.edu.fudan.ISTBI.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessDTO
{
    String name;
    String path;
    Boolean processSubFolder;
}
