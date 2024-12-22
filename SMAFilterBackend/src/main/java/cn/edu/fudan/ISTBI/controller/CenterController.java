package cn.edu.fudan.ISTBI.controller;

import cn.edu.fudan.ISTBI.dto.CenterDTO;
import cn.edu.fudan.ISTBI.dto.ProcessDTO;
import cn.edu.fudan.ISTBI.entity.Center;
import cn.edu.fudan.ISTBI.utils.ConfigUtils;
import cn.edu.fudan.ISTBI.utils.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class CenterController
{
    @CrossOrigin
    @GetMapping("/getCenterList")
    public ResponseEntity<List<CenterDTO>> getCenterList()
    {
        return new ResponseEntity<>(ConfigUtils.getConfig().stream().map(center -> new CenterDTO(center.getId(), center.getName())).collect(Collectors.toList()), HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/setCenterList")
    public ResponseEntity<String> setCenterList(@RequestBody List<CenterDTO> centers)
    {
        List<Center> originCenterList = ConfigUtils.getConfig();
        Map<Integer, Center> centerMap = new HashMap<>();
        Set<String> newNames = new HashSet<>(centers.stream().map(center -> center.getName()).collect(Collectors.toList()));
        if (newNames.size() != centers.size())
        {
            return new ResponseEntity<>("中心名称不允许重复", HttpStatus.BAD_REQUEST);
        }
        else
        {
            List<Integer> originIds = originCenterList.stream().map(center -> center.getId()).collect(Collectors.toList());
            for (Center center : originCenterList)
            {
                centerMap.put(center.getId(), center);
            }
            for (CenterDTO center : centers)
            {
                centerMap.get(center.getId()).setName(center.getName());
                originIds.remove(Integer.valueOf(center.getId()));  // to remove the element with this id, not with this as index
            }
            // to remove the deleted center
            List<Center> newList = originCenterList.stream().filter(center -> !originIds.contains(center.getId())).collect(Collectors.toList());
            ConfigUtils.saveConfig(newList);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @CrossOrigin
    @PostMapping("/importCenterList")
    // only use path in the DTO
    public ResponseEntity<String> importList(@RequestBody ProcessDTO processDTO)
    {
        String path = processDTO.getPath();
        List<Center> centersToAdd = ConfigUtils.getConfig(path);
        List<Center> originCenters = ConfigUtils.getConfig();
        List<String> originNames = originCenters.stream().map(center -> center.getName()).collect(Collectors.toList());

        int maxId;
        Optional<Integer> maxIdOp = originCenters.stream().map(center -> center.getId()).max(Integer::compareTo);
        if (maxIdOp.isPresent())
        {
            maxId = maxIdOp.get();
        }
        else
        {
            maxId = 0;
        }
        for (Center center : centersToAdd)
        {
            String newName = getCenterNewName(center.getName(), originNames);
            originNames.add(newName);
            center.setName(newName);
            maxId ++;
            center.setId(maxId);
            originCenters.add(center);
        }

        ConfigUtils.saveConfig(originCenters);

        return new ResponseEntity<>(HttpStatus.OK);
    }






    private static String getCenterNewName(String thisName, List<String> originNames)
    {
        int index = 1;
        Pattern pattern = Pattern.compile(thisName + "\\((\\d+)\\)");
        Matcher matcher;
        for (String name : originNames)
        {
            if (name.equals(thisName))
            {
                if (index == 1)
                {
                    index = 2;
                }
            }
            else
            {
                matcher = pattern.matcher(name);
                if (matcher.find())
                {
                    int existsIndex = Integer.parseInt(matcher.group(1));
                    if (existsIndex >= index)
                    {
                        index = existsIndex + 1;
                    }
                }
            }
        }
        if (index == 1)
        {
            return thisName;
        }
        else
        {
            return thisName +"(" + index + ")";
        }
    }

// for name test only
//    public static void main(String[] args)
//    {
//        List<String> list = new ArrayList<>();
//        list.add("name");
//        list.add("temp");
//
//        String name;
//        name = getCenterNewName("name", list);
//        list.add(name);
//        System.out.println("Add " + name);
//
//        name = getCenterNewName("name", list);
//        list.add(name);
//        System.out.println("Add " + name);
//
//        name = getCenterNewName("name", list);
//        list.add(name);
//        System.out.println("Add " + name);
//
//        name = getCenterNewName("name", list);
//        list.add(name);
//        System.out.println("Add " + name);
//
//        name = getCenterNewName("temp", list);
//        list.add(name);
//        System.out.println("Add " + name);
//
//        name = getCenterNewName("temp", list);
//        list.add(name);
//        System.out.println("Add " + name);
//
//
//        name = getCenterNewName("nametemp", list);
//        list.add(name);
//        System.out.println("Add " + name);
//
//    }
}


