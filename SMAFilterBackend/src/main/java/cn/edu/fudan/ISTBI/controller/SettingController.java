package cn.edu.fudan.ISTBI.controller;

import cn.edu.fudan.ISTBI.dto.ProcessDTO;
import cn.edu.fudan.ISTBI.entity.Center;
import cn.edu.fudan.ISTBI.checker.ExpressionChecker;
import cn.edu.fudan.ISTBI.utils.ConfigUtils;
import cn.edu.fudan.ISTBI.utils.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SettingController
{


    @CrossOrigin
    @GetMapping("/getFilterSettingByName/{name}")
    public ResponseEntity<Center> getCenter(@PathVariable("name") String name)
    {
        List<Center> centers = ConfigUtils.getConfig();
        Center center = centers.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
        return new ResponseEntity<>(center, HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/saveFilterSetting")
    public ResponseEntity<String> saveCenter(@RequestBody Center center)
    {
        List<Center> centers = ConfigUtils.getConfig();
        String name = center.getName();
        for (int i = 0;i < centers.size();i++)
        {
            if (centers.get(i).getName().equals(name))
            {
                centers.set(i, center);
                break;
            }
        }
        ConfigUtils.saveConfig(centers);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/checkExpressionAvailable")
    public ResponseEntity<List<String>> checkExpressionAvailable(@RequestBody ProcessDTO dto)
    {
        String expression = dto.getName();  // use name to pass the expression
        String errorMessage;
        ExpressionChecker checker = new ExpressionChecker(expression);
        if (checker.isValidExpression())
        {
            return new ResponseEntity<>(checker.getExampleTexts(), HttpStatus.OK);
        }
        else
        {
            long leftBrace = expression.chars().filter(ch -> ch == '(').count();
            long rightBrace = expression.chars().filter(ch -> ch == ')').count();
            if (leftBrace < rightBrace)
            {
                errorMessage = "缺少左括号 \"(\"";
            }
            else if (rightBrace < leftBrace)
            {
                errorMessage = "缺少右括号 \")\"";
            }
            else
            {
                errorMessage = "表达式有误";
            }
            return new ResponseEntity<>(List.of(errorMessage), HttpStatus.BAD_REQUEST);
        }
    }





}
