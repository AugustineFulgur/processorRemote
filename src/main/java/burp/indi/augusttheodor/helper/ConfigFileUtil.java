package burp.indi.augusttheodor.helper;

import burp.BurpExtender;

import java.io.*;
import java.util.Vector;

public class ConfigFileUtil {
    //�����ļ�
    private static final String fileName="easyAuxiliary.config";
    private static File configFile;

    public static ConfigObject getInstance(){ //��ȡ�ļ���
        ConfigObject co=null;
        try{
            ConfigFileUtil.configFile=new File(ConfigFileUtil.fileName);
            if(ConfigFileUtil.configFile.exists()){
                InputStream st=new FileInputStream(ConfigFileUtil.configFile);
                ObjectInputStream os=new ObjectInputStream(st);
                co=(ConfigObject)os.readObject(); //��ȡ�ļ��������л���Object��Ȼ��װ��
                os.close();
            }else{
                ConfigFileUtil.configFile.createNewFile();
                ObjectOutputStream oo=new ObjectOutputStream(new FileOutputStream(ConfigFileUtil.configFile));
                co=new ConfigObject();
                oo.writeObject(co);
                oo.close();
            }
            return co;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig(ConfigObject co){
        ConfigFileUtil.configFile=new File(ConfigFileUtil.fileName);
        try {
            ConfigFileUtil.configFile.delete(); //ɾ���ļ�
            ConfigFileUtil.configFile.createNewFile();
            ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(ConfigFileUtil.configFile));
            os.writeObject(co);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ConfigObject implements Serializable {

        public int maxEnumerableNumber=2; //���ö������
        public int maxThread=5; //����߳�
        public boolean isEnumerableFirst=true; //ö����ǰ
        public boolean isEnumerableMiddle=true; //ö���м�
        public boolean isEnumerableLast=true; //ö�����
        public int maxThreshold=0; //��С������ֵ

    }

}
