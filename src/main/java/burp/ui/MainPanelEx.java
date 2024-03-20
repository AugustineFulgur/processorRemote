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
        this.setLayout(new FlowLayout()); //˭����JAVA�þ���˭��дswing������Ϊһ��ʺ
        this.add(new JLabel("RPC��ַ��������˿ڣ�"));
        JTextField rpcAddress=new JTextField();
        rpcAddress.setPreferredSize(new Dimension(200, 30));
        this.add(rpcAddress);
        this.add(new JLabel("processor����"));
        JTextField rpcName=new JTextField();
        rpcName.setPreferredSize(new Dimension(100, 30));
        this.add(rpcName);
        JCheckBox isSocketChk=new JCheckBox("�Ƿ�Ϊ���������");
        this.add(isSocketChk);
        JButton addBtn=new JButton("���Intruder processor");
        Object hn[]={"processor��","�����ַ������˿ڣ�","�Ƿ�Ϊ���������"};
        DefaultTableModel tableModel=new DefaultTableModel(hn,0);
        // processor�� �����ַ �Ƿ�Ϊ���������
        JTable history=new JTable(tableModel);
        JScrollPane scroll=new JScrollPane(history); //JAVA�����㴫ͳ֮һ��Ҫ����
        scroll.setPreferredSize(new Dimension(800,400));
        addBtn.addActionListener(l->{
            try {
                BurpExtender.call.registerIntruderPayloadProcessor(new IExtendIntruderPayloadProcessor(isSocketChk.isSelected(),rpcAddress.getText(),rpcName.getText()));
                Object ob[]={rpcName.getText(),rpcAddress.getText(),isSocketChk.isSelected()}; //�в� ����ʲô�������Ҳ��
                ((DefaultTableModel)history.getModel()).addRow(ob);
                ((DefaultTableModel)history.getModel()).fireTableDataChanged(); //֪ͨˢ��
            } catch (Exception e) {
                BurpExtender.so.println(e.getMessage());
                JOptionPane.showMessageDialog(null,e.getMessage(),"���processor����",JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        });
        this.add(addBtn);
        this.add(scroll);
        JButton deleteProcessor=new JButton("ɾ��ѡ�е�processor");
        deleteProcessor.addActionListener(l->{
            Vector<Vector> vec=((DefaultTableModel) history.getModel()).getDataVector();
            boolean isSocket= (boolean)vec.get(history.getSelectedRow()).get(2); //���������һ������ ��
            BurpExtender.so.println(vec.get(history.getSelectedRow()).get(2));
            IIntruderPayloadProcessor processor=(IIntruderPayloadProcessor) hr.keySet().toArray()[history.getSelectedRow()];
            if(isSocket){
                //��������Ϊ�������������Ҫ��ɾ��ʱֹͣ����
                hr.get(processor).thread.deprecatedStop();
            }
            hr.remove(processor); //�Ƴ�
            BurpExtender.call.removeIntruderPayloadProcessor(processor);
            ((DefaultTableModel)history.getModel()).removeRow(history.getSelectedRow());
        });
        this.add(deleteProcessor);
    }

    private class IExtendIntruderPayloadProcessor implements IIntruderPayloadProcessor { //Ϊ�˻���������Ҫ���⽨������

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
                    // �����쳣���
                    BurpExtender.so.println("Errors!");
                    return null;
                }
            });
            return futureResult.join();
        }
    }

}
