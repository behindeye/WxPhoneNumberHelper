package czc.lazyhelper;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import czc.lazyhelper.constant.Const;
import czc.lazyhelper.manager.DBManager;
import czc.lazyhelper.manager.PathManager;
import czc.lazyhelper.util.AbnormalHandler;
import tech.linjiang.pandora.Pandora;

/**
 * Created by czc on 2017/6/18.
 */

public class MyApplication extends Application {

    /**
     * SDK初始化也可以放到Application中
     */
    public static String APPID = "";
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
