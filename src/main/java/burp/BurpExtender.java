package burp;

import burp.indi.augusttheodor.helper.ConfigFileUtil;
import burp.ui.MainPanelEx;

import java.awt.*;
import java.io.PrintWriter;

//by AugustTheo 此插件作用：
// 调用远程Web形式的加密接口
public class BurpExtender implements IBurpExtender,ITab {

    public static IExtensionHelpers helpers;
    public static PrintWriter so;
    private final String extName="processorRemote";
    private static final String VERSION = "0.2.0";
    public static MainPanelEx panel;
    public static ConfigFileUtil.ConfigObject co=ConfigFileUtil.getInstance();
    public static IBurpExtenderCallbacks call;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        BurpExtender.call=callbacks;
        BurpExtender.helpers = callbacks.getHelpers(); //获取helpers
        BurpExtender.so = new PrintWriter(callbacks.getStdout(), true); //获取输出
        BurpExtender.panel=new MainPanelEx(); //十分不优雅
        callbacks.setExtensionName(extName);
        callbacks.addSuiteTab(this);
        BurpExtender.so.println("@name "+extName);
        BurpExtender.so.println("@author AugustTheodor");
        BurpExtender.so.println("@version "+VERSION);
    }

    @Override
    public String getTabCaption() {
        return "E processor";
    }

    @Override
    public Component getUiComponent() {
        return BurpExtender.panel;
    }
}
