package czc.wxphonenumberhelper.activity;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.model.ValidateFuncItem;
import czc.wxphonenumberhelper.util.PreferenceHelper;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void setContentView() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_splash);
        try {
            TextView tvVersion = (TextView) findViewById(R.id.tv_version);
            tvVersion.setText("V" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        checkIsValidateAagin();
        getValidateFuncItemList();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                redirect(MainActivity.class);
                finish();
            }
        }, 1500);
    }

    private void getValidateFuncItemList() {
        BmobQuery<ValidateFuncItem> query = new BmobQuery<>();
        query.findObjects(new FindListener<ValidateFuncItem>() {
            @Override
            public void done(List<ValidateFuncItem> list, BmobException e) {
                if (e == null) {
                    ValidateFuncItem validateFuncItem = list.get(0);
                    List<String> validateFuncItemList = validateFuncItem.validateItemList;
                    Log.i("czc", validateFuncItemList.toString());
                    PreferenceHelper.putString(Const.PREF_KEY_VALIDATE_FUNC_ITEM_LIST, new Gson().toJson(validateFuncItemList));
                }
            }
        });

    }


    private void checkIsValidateAagin() {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", PreferenceHelper.getString(Const.PREF_KEY_ACCOUNT, ""));
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    for (BmobUser user : list) {
                        if (user.getUsername().equals(PreferenceHelper.getString(Const.PREF_KEY_ACCOUNT, ""))) {
                            PreferenceHelper.putBoolean(Const.PREF_KEY_ACTIVATE, true);
                        }
                    }
                } else {
                    PreferenceHelper.putBoolean(Const.PREF_KEY_ACTIVATE, false);
                }
            }
        });
    }

    //防止用户返回键退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
