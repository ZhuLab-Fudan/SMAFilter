package cn.edu.fudan.ISTBI.controller;

import cn.edu.fudan.ISTBI.dto.ProcessDTO;
import cn.edu.fudan.ISTBI.result.ChartResult;
import cn.edu.fudan.ISTBI.result.SingleResult;
import cn.edu.fudan.ISTBI.utils.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ResultController
{
    @CrossOrigin
    @GetMapping("/getSingleResults")
    public ResponseEntity<List<SingleResult>> getResults()
    {
        List<SingleResult> results = IOUtils.getSingleResults();
        if (results != null)
        {
            return new ResponseEntity<>(results, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @GetMapping("/getChartResults")
    public ResponseEntity<List<ChartResult>> getChartResults()
    {
        List<ChartResult> results = IOUtils.getChartResults();
        if (results != null)
        {
            return new ResponseEntity<>(results, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PostMapping("/saveSingleCases")
    public ResponseEntity<String> saveCases(@RequestBody ProcessDTO info)
    {
        IOUtils.copySingleCases(info.getPath());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/saveChartCases")
    public ResponseEntity<String> saveChartCases(@RequestBody ProcessDTO info)
    {
        IOUtils.saveChartCases(info.getPath());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
