package czc.wxphonenumberhelper;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.manager.DBManager;
import czc.wxphonenumberhelper.util.AbnormalHandler;
import tech.linjiang.pandora.Pandora;

/**
 * Created by czc on 2017/6/18.
 */

public class MyApplication extends Application {

    /**
     * SDK初始化也可以放到Application中
     */
    public static String APPID = "a28a536d06cc4c9c4ad4fa3fa14c9907";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Bmob.initialize(this, APPID, "Bmob");
        BmobInstallation installation = BmobInstallation.getCurrentInstallation();
        installation.setDeviceType(android.os.Build.MODEL);
        installation.save();

        DBManager.init(this);

        Pandora.init(this).enableShakeOpen();
        Pandora.init(this).open();
        if (Const.DEBUG) {
            AbnormalHandler.getInstance().init(this);
        }
    }

    public static Context getAppContext() {
        return context;
    }
}
