package czc.wxphonenumberhelper.activity;

import android.Manifest;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.PermissionUtils;

import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.base.BaseActivity;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {

    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void setContentView() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_splash);
        try {
            TextView tvVersion = (TextView) findViewById(R.id.tv_version);
            tvVersion.setText("v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
//        checkIsValidateAagin();
//        getValidateFuncItemList();
        if (PermissionUtils.isGranted(permissions)) {
            delayJumpActivity(1500);
        } else {
            PermissionUtils.permission(permissions)
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            delayJumpActivity(1000);
                        }

                        @Override
                        public void onDenied() {
                            finish();
                        }
                    }).request();
        }
    }

    private void delayJumpActivity(long time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                redirect(MainActivity.class);
                finish();
            }
        }, time);
    }

//    private void getValidateFuncItemList() {
//        BmobQuery<ValidateFuncItem> query = new BmobQuery<>();
//        query.findObjects(new FindListener<ValidateFuncItem>() {
//            @Override
//            public void done(List<ValidateFuncItem> list, BmobException e) {
//                if (e == null) {
//                    ValidateFuncItem validateFuncItem = list.get(0);
//                    List<String> validateFuncItemList = validateFuncItem.validateItemList;
//                    Log.i("czc", validateFuncItemList.toString());
//                    PreferenceHelper.putString(Const.PREF_KEY_VALIDATE_FUNC_ITEM_LIST, new Gson().toJson(validateFuncItemList));
//                }
//            }
//        });
//
//    }
//
//
//    private void checkIsValidateAagin() {
//        BmobQuery<BmobUser> query = new BmobQuery<>();
//        query.addWhereEqualTo("username", PreferenceHelper.getString(Const.PREF_KEY_ACCOUNT, ""));
//        query.findObjects(new FindListener<BmobUser>() {
//            @Override
//            public void done(List<BmobUser> list, BmobException e) {
//                if (e == null && list != null && list.size() > 0) {
//                    for (BmobUser user : list) {
//                        if (user.getUsername().equals(PreferenceHelper.getString(Const.PREF_KEY_ACCOUNT, ""))) {
//                            PreferenceHelper.putBoolean(Const.PREF_KEY_ACTIVATE, true);
//                        }
//                    }
//                } else {
//                    PreferenceHelper.putBoolean(Const.PREF_KEY_ACTIVATE, false);
//                }
//            }
//        });
//    }

    //防止用户返回键退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
