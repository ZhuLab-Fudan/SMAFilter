package cn.edu.fudan.ISTBI;

import cn.edu.fudan.ISTBI.thread.IPCThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

@SpringBootApplication
public class SMAFilter
{
    public static void main(String[] args)
    {
        SpringApplication.run(SMAFilter.class, args);
//        try
//        {
//            PipedOutputStream pout = new PipedOutputStream();
//            PipedInputStream pin = new PipedInputStream(pout);
////            new IPCThread(pin, pout).run();
////            new IPCThread().run();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }
}
