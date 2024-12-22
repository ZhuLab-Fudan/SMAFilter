package cn.edu.fudan.ISTBI.controller;

import cn.edu.fudan.ISTBI.dto.ProgressDTO;
import cn.edu.fudan.ISTBI.dto.ProcessDTO;
import cn.edu.fudan.ISTBI.entity.Center;
import cn.edu.fudan.ISTBI.thread.ProcessThread;
import cn.edu.fudan.ISTBI.utils.ConfigUtils;
import cn.edu.fudan.ISTBI.utils.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.List;

@RestController
public class ProcessController
{
    ProcessThread thread;

    static DecimalFormat df = new DecimalFormat("#.00");

    long startTime;

    @CrossOrigin
    @PostMapping("/startProcess")
    public ResponseEntity<String> startProcess(@RequestBody ProcessDTO info)
    {
        Center center = ConfigUtils.getConfig().stream().filter(c -> c.getName().equals(info.getName())).findFirst().orElse(null);
        String path = info.getPath();
        thread = new ProcessThread(path, center, info.getProcessSubFolder());
        startTime = System.currentTimeMillis();
        thread.start();
        // start process
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/getProgress")
    public ResponseEntity<ProgressDTO> getProgress()
    {
        if (thread != null)
        {
            ProgressDTO progress = new ProgressDTO();
            List<String> info = thread.getInfo();
            if (info.size() >= 100)
            {
                progress.setInfo(info.subList(info.size() - 100, info.size()));
            }
            else
            {
                progress.setInfo(info);
            }
            double percentage = thread.getPercentage();
            String percent = df.format(percentage * 100);
            if (percent.startsWith("."))
            {
                percent = "0" + percent;
            }
            progress.setPercentage(percent);
            long pass = System.currentTimeMillis() - startTime;
            long remain;
            if (percentage == 0)
            {
                remain = 0;
            }
            else
            {
                remain = (long) (pass / percentage * (1-percentage));
            }
            remain = remain / 1000;
            long remainedHours = remain / 3600;
            long remainedMinuts = remain % 3600 / 60;
            long remainedSeconds = remain % 60;
            String time = "";
            time += (remainedHours > 0)? remainedHours + " 小时 " : "";
            time += (remainedMinuts > 0)? remainedMinuts + " 分钟 " : "";
            time += remainedSeconds + " 秒";
            progress.setRemainedTime(time);
            return new ResponseEntity<>(progress, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PostMapping("/stopProcess")
    public ResponseEntity<String> stopProcess()
    {
        thread.stopAll();
        thread.interrupt();
        thread = null;
        System.out.println("Terminated");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
