//package cn.edu.fudan.ISTBI.socket;
//
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class SimpleWebSocketHandler extends TextWebSocketHandler
//{
//    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        executorService.scheduleAtFixedRate(() -> {
//            try {
//                // Simulate processing and send progress to the frontend
//                int progress = (int) (Math.random() * 100);
//                session.sendMessage(new TextMessage("Progress: " + progress + "%"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, 0, 1, TimeUnit.SECONDS); // Adjust the interval as needed
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        executorService.shutdown();
//    }
//}
