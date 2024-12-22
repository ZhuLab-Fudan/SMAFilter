package cn.edu.fudan.ISTBI.controller;

import cn.edu.fudan.ISTBI.dto.ProcessDTO;
import cn.edu.fudan.ISTBI.utils.ConfigUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController
{
    @CrossOrigin
    @PostMapping("/setBackendPath")
    public ResponseEntity<String> setBackendPath(@RequestBody ProcessDTO dto)
    {
        String path = dto.getPath();
        ConfigUtils.setBackendPath(path);
//        System.out.println(path);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
