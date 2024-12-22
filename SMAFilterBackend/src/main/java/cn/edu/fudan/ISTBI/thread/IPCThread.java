package cn.edu.fudan.ISTBI.thread;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IPCThread extends Thread
{
    String requestPath = "\\\\.\\Pipe\\myPipe";

    @Override
    public void run()
    {


        super.run();
        try
        {

            RandomAccessFile pipe = new RandomAccessFile(requestPath, "rw");


            pipe.write("Hi".getBytes(StandardCharsets.UTF_8));


//            BufferedWriter writer = new BufferedWriter(new FileWriter(requestPath));
//            writer.write("Hi");
//            BufferedReader reader = new BufferedReader(new FileReader(requestPath));
//            while (true)
//            {
//                byte[] buf = new byte[1024];
//                try
//                {
//                    int bytes = pipe.read(buf);
//                    String message = "[RECV] " + new String(buf, 0, bytes);
//                    System.out.println(message);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
////
////                if (reader.ready())
////                {
////                    String request = reader.readLine();
////                    System.out.println("[RECV] " + request);
////                }
////                try
////                {
////                    Thread.sleep(100);
////                }
////                catch (Exception e)
////                {
////                    e.printStackTrace();
////                }
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    //
//
//    PipedInputStream pin;
//    PipedOutputStream pout;
//    public IPCThread(PipedInputStream pin, PipedOutputStream pout)
//    {
//        this.pin = pin;
//        this.pout = pout;
//    }
//
//    @Override
//    public void run()
//    {
//        super.run();
//        try
//        {
//            while (true)
//            {
//                byte[] buffer = new byte[1024];
//                int bytesRead = pin.read(buffer);
//                if (bytesRead > 0)
//                {
//                    String message = "[RECV] " + new String(buffer, 0, bytesRead);
//                    System.out.println(message);
//                    pout.write(message.getBytes(StandardCharsets.UTF_8));
//                    pout.flush();
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
}
