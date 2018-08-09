package czc.lazyhelper.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import czc.lazyhelper.R;
import czc.lazyhelper.base.BaseActivity;
import czc.lazyhelper.constant.Const;
import czc.lazyhelper.util.WxAppUtil;
import czc.lazyhelper.util.Md5;
import czc.lazyhelper.util.PreferenceHelper;

@Deprecated
public class ValidateActivity extends BaseActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.ll_login_view)
    LinearLayout llLoginView;
    @BindView(R.id.ll_success_view)
    LinearLayout llSuccessView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_validate);
    }

    @Override
    protected void initData() {
        if (!WxAppUtil.isValiditySoftWare()) {
            llLoginView.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            llSuccessView.setVisibility(View.GONE);
        } else {
            llLoginView.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            llSuccessView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_login)
    public void onLoginClicked() {
        final String account = etAccount.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            toast("请填写激活账号");
            return;
        }
        btnLogin.setText("激活中···");
        btnLogin.setEnabled(false);

        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereEqualTo("username", account);
        query.findObjects(new FindListener<BmobUser>() {

            @Override
            public void done(List<BmobUser> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    for (BmobUser user : list) {
                        if (user.getUsername().equals(account)){
                            toast("激活成功");
                            PreferenceHelper.putBoolean(Const.PREF_KEY_ACTIVATE, true);
                            PreferenceHelper.putString(Const.PREF_KEY_ACCOUNT, account);
                            PreferenceHelper.putString(Const.PREF_KEY_ACCOUNT_MD5, Md5.encode(account));
                            llLoginView.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.GONE);
                            llSuccessView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    toast("激活失败");
                    PreferenceHelper.putBoolean(Const.PREF_KEY_ACTIVATE, false);
                    PreferenceHelper.putString(Const.PREF_KEY_ACCOUNT, "");
                    PreferenceHelper.putString(Const.PREF_KEY_ACCOUNT_MD5, "");
                    btnLogin.setText("激活");
                    btnLogin.setEnabled(true);
                }
            }
        });

    }

    @OnClick(R.id.btn_success)
    void onClickSuccess() {
        back();
    }
}
