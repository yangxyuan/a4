package aaa;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocketServer {

    private static AtomicInteger onlineCount = new AtomicInteger();

    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    private Session session;

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        System.out.println("new added ！current count number = " + getOnlineCount());
    }

    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("one connection closed！current count number = " + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("from client message : " + message);

        for(WebSocketServer item : webSocketSet){
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("error occur");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount.getAndIncrement();
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount.getAndDecrement();
    }
}