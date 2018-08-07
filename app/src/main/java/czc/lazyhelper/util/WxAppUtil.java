package czc.lazyhelper.util;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.lang.reflect.Method;

import czc.lazyhelper.constant.Const;
import czc.lazyhelper.service.TaskService;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by czc on 2017/7/3.
 */

public class WxAppUtil {

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
                    if (accessibilityService.contains(TaskService.class.getSimpleName())) {
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

    /**
     * 判断悬浮窗口权限是否打开
     */
    public static boolean hasFloatWindowAccessPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        }
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
