package burp.ui;

import burp.BurpExtender;
import burp.IIntruderPayloadProcessor;
import burp.indi.augusttheodor.helper.Helper;
import burp.indi.augusttheodor.helper.HttpReverse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

public class MainPanelEx extends JPanel {

    private HashMap<IIntruderPayloadProcessor,HttpReverse> hr=new HashMap<>();

    public MainPanelEx(){
        this.setLayout(new FlowLayout()); //谁觉得JAVA好就拉谁来写swing，鉴定为一坨屎
        this.add(new JLabel("RPC地址或服务器端口："));
        JTextField rpcAddress=new JTextField();
        rpcAddress.setPreferredSize(new Dimension(200, 30));
        this.add(rpcAddress);
        this.add(new JLabel("processor名："));
        JTextField rpcName=new JTextField();
        rpcName.setPreferredSize(new Dimension(100, 30));
        this.add(rpcName);
        JCheckBox isSocketChk=new JCheckBox("是否为反向服务器");
        this.add(isSocketChk);
        JButton addBtn=new JButton("添加Intruder processor");
        Object hn[]={"processor名","调用字符串（端口）","是否为反向服务器"};
        DefaultTableModel tableModel=new DefaultTableModel(hn,0);
        // processor名 请求地址 是否为反向服务器
        JTable history=new JTable(tableModel);
        JScrollPane scroll=new JScrollPane(history); //JAVA的优秀传统之一定要套娃
        scroll.setPreferredSize(new Dimension(800,400));
        addBtn.addActionListener(l->{
            try {
                BurpExtender.call.registerIntruderPayloadProcessor(new IExtendIntruderPayloadProcessor(isSocketChk.isSelected(),rpcAddress.getText(),rpcName.getText()));
                Object ob[]={rpcName.getText(),rpcAddress.getText(),isSocketChk.isSelected()}; //有病 我用什么命名风格也管
                ((DefaultTableModel)history.getModel()).addRow(ob);
                ((DefaultTableModel)history.getModel()).fireTableDataChanged(); //通知刷新
            } catch (Exception e) {
                BurpExtender.so.println(e.getMessage());
                JOptionPane.showMessageDialog(null,e.getMessage(),"添加processor错误",JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        });
        this.add(addBtn);
        this.add(scroll);
        JButton deleteProcessor=new JButton("删除选中的processor");
        deleteProcessor.addActionListener(l->{
            Vector<Vector> vec=((DefaultTableModel) history.getModel()).getDataVector();
            boolean isSocket= (boolean)vec.get(history.getSelectedRow()).get(2); //酣畅淋漓的一场调用 惹
            BurpExtender.so.println(vec.get(history.getSelectedRow()).get(2));
            IIntruderPayloadProcessor processor=(IIntruderPayloadProcessor) hr.keySet().toArray()[history.getSelectedRow()];
            if(isSocket){
                //如果被标记为反向服务器，需要在删除时停止监听
                hr.get(processor).thread.deprecatedStop();
            }
            hr.remove(processor); //移除
            BurpExtender.call.removeIntruderPayloadProcessor(processor);
            ((DefaultTableModel)history.getModel()).removeRow(history.getSelectedRow());
        });
        this.add(deleteProcessor);
    }

    private class IExtendIntruderPayloadProcessor implements IIntruderPayloadProcessor { //为了基础处理需要额外建立个类

        public String rpcName;
        public String rpcAddress;
        public boolean isSocket;
        public HttpReverse reverse=null;

        public IExtendIntruderPayloadProcessor(boolean isSocket,String rpcAddress,String rpcName) throws IOException {
            this.isSocket=isSocket;
            this.rpcAddress=rpcAddress;
            this.rpcName=rpcName;
            if (isSocket) {
                HttpReverse reverse=new HttpReverse(Integer.parseInt(rpcAddress));
                this.reverse=reverse;
                hr.put(this,reverse);
            } else {
                hr.put(this,null);
            }
        }

        @Override
        public String getProcessorName() {
            return this.rpcName;
        }

        @Override
        public byte[] processPayload(byte[] currentPayload, byte[] originalPayload, byte[] baseValue) {
            CompletableFuture<byte[]> futureResult = CompletableFuture.supplyAsync(() -> {
                try {
                    if (this.isSocket) {
                        return Helper.socketProcess(this.reverse, originalPayload);
                    } else {
                        return Helper.httpProcess(this.rpcAddress, originalPayload);
                    }
                } catch (Exception e) {
                    // 处理异常情况
                    BurpExtender.so.println("Errors!");
                    return null;
                }
            });
            return futureResult.join();
        }
    }

}
