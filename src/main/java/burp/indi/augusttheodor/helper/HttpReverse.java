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

public class HttpReverse { //��ת��HTTP
    // �������������������� �÷� ʲôʱ��˭д����͸����ר�������
    // ˭����дһ��- - ��ô����ɽկchrome��ô�𾢸�����д������ô�Ѿ�

    public HashMap<String,String> iHateWebsocket=new HashMap<>();
    public PrivateThread thread;

    public HttpReverse(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/hb", l->{ //������
            l.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            String resText=null;
            for (String i:iHateWebsocket.keySet()) {
                if(iHateWebsocket.get(i)==null){ //���͵�һ����ȡ���Ŀ�ֵ
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
        // �����������ķ���
        PrivateThread serverThread = new PrivateThread(server,port);
        serverThread.start();
        this.thread=serverThread;
    }

    public class PrivateThread extends Thread{
        //д��Java��˵����ɧ��һ���һ����^ ^ ��stop�����ò���ƨ��
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
            BurpExtender.so.println("�˿�"+this.port+"�ϵķ���HTTP��ֹͣ");
            target.stop(0);
        }
    }

}
