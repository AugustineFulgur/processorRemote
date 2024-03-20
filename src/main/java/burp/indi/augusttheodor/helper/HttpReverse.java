package burp.indi.augusttheodor.helper;

import burp.BurpExtender;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HttpReverse { //翻转型HTTP
    // 升级降级升级降级升级 好烦 什么时候谁写个渗透测试专用浏览器
    // 谁雇我写一个- - 怎么锅铲山寨chrome这么起劲给猴子写工具这么费劲

    public HashMap<String,String> iHateWebsocket=new HashMap<>();
    public PrivateThread thread;

    public HttpReverse(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/hb", l->{ //心跳包
            l.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            String resText=null;
            for (String i:iHateWebsocket.keySet()) {
                if(iHateWebsocket.get(i)==null){ //发送第一个获取到的空值
                    BurpExtender.so.println(i);
                    resText=i;
                    break;
                }
            }
            if(resText!=null){
                BurpExtender.so.println("send resText");
                l.sendResponseHeaders(200, resText.length());
                l.getResponseBody().write(resText.getBytes());
            }else{
                l.sendResponseHeaders(200, 0);
            }
            l.getResponseBody().close();
        });
        server.createContext("/enc",l->{
            l.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            Map<String, String> params = Helper.parseQueryParams(l.getRequestURI().getQuery());
            String key = params.get("key");
            String value = params.get("value");
            iHateWebsocket.put(key,value);
            l.sendResponseHeaders(200, 0);
            l.getResponseBody().close();
        });
        server.setExecutor(null); // creates a default executor
        // 启动服务器的方法
        PrivateThread serverThread = new PrivateThread(server,port);
        serverThread.start();
        this.thread=serverThread;
    }

    public class PrivateThread extends Thread{
        //写起Java想说的牢骚就一句接一句啦^ ^ 给stop被弃用擦擦屁股
        private HttpServer target;
        private int port;

        public PrivateThread(HttpServer target,int port){
            this.target=target;
            this.port=port;
        }

        @Override
        public void run() {
            target.start();
        }

        public void deprecatedStop(){
            BurpExtender.so.println("端口"+this.port+"上的反向HTTP已停止");
            target.stop(0);
        }
    }

}
