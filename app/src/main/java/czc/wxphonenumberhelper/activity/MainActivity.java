package czc.wxphonenumberhelper.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import czc.wxphonenumberhelper.Common.FuncItemFactory;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.adapter.FuncAdapter;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.controller.MyWindowManager;
import czc.wxphonenumberhelper.model.UserManager;
import czc.wxphonenumberhelper.ui.DialogView;
import czc.wxphonenumberhelper.ui.OnDialogClickListener;
import czc.wxphonenumberhelper.util.AppUtil;
import czc.wxphonenumberhelper.util.PreferenceHelper;

/**
 * app main entry
 */
public class MainActivity extends BaseActivity {

    private final static int MINI_VERSION_CODE = 16;
    private final static String SNACKBAR_TXT = "当前您使用的系统版本过低，请更新高版本系统再使用";

    @BindView(R.id.ll)
    FrameLayout mLlRootView;

    @BindView(R.id.bannerContainer)
    FrameLayout mFlBannerContainer;

    @BindView(R.id.ryv)
    RecyclerView mRecyclerView;

    private boolean mIsFirstJump = false;
    private FuncAdapter mAdapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FuncAdapter(this, FuncItemFactory.getInstance().createFuncItems());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        // only for build version larger equal than api 18
        // systemVersionValicate();
        checkUpdate();
        saveSomeMsg();
    }

    @Override
    protected void setListener() {
        mAdapter.setOnItemClickListener(new FuncAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String funcName = FuncItemFactory.getInstance().createFuncItems().get(position).funcName;
                switch (funcName) {
                    case FuncItemFactory.CREAT_ENUMBER:
                        redirect(NumberActivity.class);
                        break;
                    case FuncItemFactory.SHARE_GROUP:
                        redirect(GroupActivity.class);
                        break;
                    case FuncItemFactory.QUICK_CONTACTPEOPLE:
                        startAddPeople(Const.KEY_QUICK_ADD_NEAR_PEOPLE);
                        break;
                    case FuncItemFactory.ADD_CONTACTPEOPLE:
                        startAddPeople(Const.KEY_ADD_FRIEND);
                        break;
                    case FuncItemFactory.ADD_NEARPEOPLE:
                        startAddPeople(Const.KEY_ADD_NEAR_PEOPLE);
                        break;
                    case FuncItemFactory.STOP_ADD_PEOPLE:
                        PreferenceHelper.putBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, true);
                        EventBus.getDefault().post(new Boolean(true));
                        toast("设置成功");
                        break;
                    case FuncItemFactory.FLOW_WINDOW:
                        showFlowWindow();
                        break;
                    case FuncItemFactory.VALIDATE:
                        redirect(SentenceActivity.class);
                        break;
                    case FuncItemFactory.FEEDBACK:
                        redirect(FeedbackActivity.class);
                        break;
                    case FuncItemFactory.SOFT_VALIDATE:
                        redirect(ValidateActivity.class);
                        break;
                    case FuncItemFactory.HELP:
                        redirect(HelpActivity.class);
                        break;
                }
            }
        });
    }


    private void startAddPeople(final int addType) {
        if (PreferenceHelper.getBoolean(Const.PREF_IS_FIRST_USE_ADD_FUNCTION, true)) {
            DialogView dv = new DialogView(this);
            dv.setTitle("测试功能");
            dv.setMessage(getString(R.string.auto_add_people_tips));
            dv.setCancel(null);
            dv.AddDialogClickListener(new OnDialogClickListener() {
                @Override
                public void OnOKClickListener(View v) {
                    PreferenceHelper.putBoolean(Const.PREF_IS_FIRST_USE_ADD_FUNCTION, false);
                    prepareAddPeople(addType);
                }

                @Override
                public void OnCancelClickListener(View v) {
                }

                @Override
                public void OnCustomClickListener(View v) {
                }
            });
            dv.show();
        } else {
            prepareAddPeople(addType);
        }
    }

    private void prepareAddPeople(int addType) {
        PreferenceHelper.putBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, false);
        PreferenceHelper.putInt(Const.PREF_KEY_ADD_PEOPLE_TYPE, addType);
        EventBus.getDefault().post(new Integer(addType));
        if (!AppUtil.checkPermission()) {
            jumpSetting();
            mIsFirstJump = true;
        } else {
            AppUtil.jumpToWx(this);
        }
    }


    private void systemVersionValicate() {
        if (Build.VERSION.SDK_INT < MINI_VERSION_CODE) {

            Snackbar.make(mLlRootView, SNACKBAR_TXT, Snackbar.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.finish();
                }
            }, 3500);
        }
    }


    private void jumpSetting() {
        // jump to setting permission
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        if (mIsFirstJump && AppUtil.checkPermission()) {
            AppUtil.jumpToWx(this);
            mIsFirstJump = false;
        }
        super.onResume();
    }

    private void checkUpdate() {
        BmobUpdateAgent.update(this);
        if (Const.DEBUG) {
            BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                @Override
                public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                    log(updateStatus + "");
                    if (updateStatus == UpdateStatus.Yes) {
                        toast("版本更新");
                    } else if (updateStatus == UpdateStatus.No) {
                        toast("版本无更新");
                    } else if (updateStatus == UpdateStatus.EmptyField) {
                        toast("1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。");
                    } else if (updateStatus == UpdateStatus.IGNORED) {
                        toast("该版本已被忽略更新");
                    } else if (updateStatus == UpdateStatus.ErrorSizeFormat) {
                        toast("请使用file.length()方法获取apk大小。");
                    } else if (updateStatus == UpdateStatus.TimeOut) {
                        toast("超时");
                    }
                }
            });
        }
    }

    private void showFlowWindow() {
        if (!MyWindowManager.isWindowShowing()) {
            MyWindowManager.createSmallWindow(getApplicationContext());
        } else {
            toast("已显示悬浮窗了");
        }
    }

    private void saveSomeMsg() {
        if (AppUtil.isNewUser() && AppUtil.isValiditySoftWare()) {
            String manufacturer = android.os.Build.MANUFACTURER == null ? "" : android.os.Build.MANUFACTURER;
            String model = android.os.Build.MODEL == null ? "" : android.os.Build.MODEL;
            UserManager userManager = new UserManager();
            userManager.userName = PreferenceHelper.getString(Const.PREF_KEY_ACCOUNT, "");
            userManager.imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            userManager.manufacturer = manufacturer + model;
            userManager.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        PreferenceHelper.putBoolean(Const.PREF_IS_NEW_USER, false);
                    }
                }
            });
        }
    }


//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}

