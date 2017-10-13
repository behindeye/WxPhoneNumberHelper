package czc.wxphonenumberhelper.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.service.AutoAddPeopleService;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by czc on 2017/7/3.
 */

public class AppUtil {

    /**
     * 判断软件是否激活
     * @return
     */
    public static boolean isValiditySoftWare(){
        String account = PreferenceHelper.getString(Const.PREF_KEY_ACCOUNT,"");
        String accountMd5 = PreferenceHelper.getString(Const.PREF_KEY_ACCOUNT_MD5,"");
        boolean validity = PreferenceHelper.getBoolean(Const.PREF_KEY_ACTIVATE,false);
        if (validity && TextUtils.equals(accountMd5,Md5.encode(account))){
            return true;
        }else {
            return false;
        }
    }

    /**
     * check has permission
     * @return
     */
    public static  boolean checkPermission() {
        int ok = 0;
        try {
            ok = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }

        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1) {
            String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.contains(AutoAddPeopleService.class.getSimpleName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static  void jumpToWx(Context context) {
        // jump to wechat
        ComponentName name = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
        try {
            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(name);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast(context,"亲，你安装了微信了吗？");
            e.printStackTrace();
        }
    }

    public static boolean isNewUser(){
        return PreferenceHelper.getBoolean(Const.PREF_IS_NEW_USER, true);
    }
}
