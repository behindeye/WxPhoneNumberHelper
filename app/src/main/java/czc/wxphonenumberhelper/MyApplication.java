package czc.wxphonenumberhelper;

import android.app.Application;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.util.AbnormalHandler;

/**
 * Created by czc on 2017/6/18.
 */

public class MyApplication extends Application {

    /**
     * SDK初始化也可以放到Application中
     */
    public static String APPID = "a28a536d06cc4c9c4ad4fa3fa14c9907";
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Bmob.initialize(this,APPID,"Bmob");
        BmobInstallation installation = BmobInstallation.getCurrentInstallation();
        installation.setDeviceType(android.os.Build.MODEL);
        installation.save();

        if (Const.DEBUG){
            AbnormalHandler.getInstance().init(this);
        }
    }

    public static  Application getApplication(){
        return application;
    }
}
