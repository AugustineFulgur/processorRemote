package burp.indi.augusttheodor.helper;
import burp.BurpExtender;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    private static boolean isNumeric(String str) {
        //判断是否为数字
        return str.matches("\\d*");
    }

    public static byte[] httpProcess(String rpcAddress,byte[] original) throws URISyntaxException, IOException {
        CloseableHttpClient client= HttpClients.createDefault();
        HttpPost post=new HttpPost(new URI(rpcAddress));
        StringEntity params=new StringEntity(new String(original, StandardCharsets.UTF_8),StandardCharsets.UTF_8);
        post.setEntity(params);
        return EntityUtils.toByteArray(client.execute(post).getEntity());
    }

    public static byte[] socketProcess(HttpReverse cli,byte[] original) throws DeploymentException, URISyntaxException, IOException, InterruptedException {
        String oriStr=new String(original, StandardCharsets.UTF_8);
        cli.iHateWebsocket.put(oriStr,null);
        BurpExtender.so.println("get i");
        int count=0;
        while(true){
            //简单点
            if(cli.iHateWebsocket.get(oriStr)!=null){
                return cli.iHateWebsocket.get(oriStr).getBytes();
            }
            Thread.sleep(10);
            count++;
            if(count>300){
                return original;
            }
        }
    }

    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    params.put(key, value);
                }
            }
        }
        return params;
    }

}
