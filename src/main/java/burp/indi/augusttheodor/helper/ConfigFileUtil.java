package burp.indi.augusttheodor.helper;

import burp.BurpExtender;

import java.io.*;
import java.util.Vector;

public class ConfigFileUtil {
    //配置文件
    private static final String fileName="easyAuxiliary.config";
    private static File configFile;

    public static ConfigObject getInstance(){ //读取文件到
        ConfigObject co=null;
        try{
            ConfigFileUtil.configFile=new File(ConfigFileUtil.fileName);
            if(ConfigFileUtil.configFile.exists()){
                InputStream st=new FileInputStream(ConfigFileUtil.configFile);
                ObjectInputStream os=new ObjectInputStream(st);
                co=(ConfigObject)os.readObject(); //读取文件并反序列化到Object，然后装填
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
            ConfigFileUtil.configFile.delete(); //删除文件
            ConfigFileUtil.configFile.createNewFile();
            ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(ConfigFileUtil.configFile));
            os.writeObject(co);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ConfigObject implements Serializable {

        public int maxEnumerableNumber=2; //最大枚举数量
        public int maxThread=5; //最大线程
        public boolean isEnumerableFirst=true; //枚举最前
        public boolean isEnumerableMiddle=true; //枚举中间
        public boolean isEnumerableLast=true; //枚举最后
        public int maxThreshold=0; //最小置信阈值

    }

}
