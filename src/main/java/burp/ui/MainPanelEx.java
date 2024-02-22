package burp.ui;

import burp.BurpExtender;
import burp.IIntruderPayloadProcessor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainPanelEx extends JPanel {

    public MainPanelEx(){
        this.setLayout(new FlowLayout());
        this.add(new JLabel("RPC地址："));
        JTextField rpcAddress=new JTextField();
        rpcAddress.setPreferredSize(new Dimension(200, 30));
        this.add(rpcAddress);
        this.add(new JLabel("processor名："));
        JTextField rpcName=new JTextField();
        rpcName.setPreferredSize(new Dimension(100, 30));
        this.add(rpcName);
        JButton addBtn=new JButton("添加Intruder processor");
        CloseableHttpClient client= HttpClients.createDefault();
        addBtn.addActionListener(l->{
            BurpExtender.call.registerIntruderPayloadProcessor(new IIntruderPayloadProcessor() {
                @Override
                public String getProcessorName() {
                    return rpcName.getText();
                }

                @Override
                public byte[] processPayload(byte[] currentPayload, byte[] originalPayload, byte[] baseValue) {
                    try {
                        HttpPost post=new HttpPost(new URI(rpcAddress.getText()));
                        StringEntity params=new StringEntity(new String(currentPayload, StandardCharsets.UTF_8),StandardCharsets.UTF_8);
                        post.setEntity(params);
                        return EntityUtils.toByteArray(client.execute(post).getEntity());
                    } catch (URISyntaxException e) { //谁爱处理 谁处理
                        throw new RuntimeException(e);
                    } catch (ClientProtocolException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        this.add(addBtn);
    }

}
