package czc.lazyhelper.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.blankj.utilcode.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import czc.lazyhelper.R;
import czc.lazyhelper.base.BaseActivity;
import czc.lazyhelper.presenter.CreateNumberPresenter;
import czc.lazyhelper.util.ToastUtil;
import czc.lazyhelper.view.CreatePhoneView;

/**
 * 生成号码
 */
public class CreateNumberActivity extends BaseActivity implements CreatePhoneView {
    private List<String> mCreNumList = new ArrayList<>();
    private ArrayAdapter<String> mListAdapter;
    private CreateNumberPresenter mController;
    private ProgressDialog mDialog;

    @BindView(R.id.lv_phone_number)
    ListView mPhoneNumListView;

    @BindView(R.id.btn_save)
    Button mBtnSave;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_create_number);
    }

    @Override
    protected void initView() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在生成中，请耐心等待···");
        mDialog.setCanceledOnTouchOutside(false);

        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mCreNumList);
        mPhoneNumListView.setAdapter(mListAdapter);
    }

    @Override
    protected void initData() {
        mController = new CreateNumberPresenter(this);
        mController.initData(getIntent().getExtras());
        mController.createNumber();
    }


    // 保存号码本地
    @OnClick(R.id.btn_save)
    void save() {
        String[] strings = {Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS};
        if (!PermissionUtils.isGranted(strings)) {
            PermissionUtils
                    .permission(strings)
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            mBtnSave.setEnabled(false);
                            mController.save();
                        }

                        @Override
                        public void onDenied() {
                            toast("请授予权限，才能存储号码");
                        }
                    }).request();
        } else {
            mBtnSave.setEnabled(false);
            mController.save();
        }
    }

    @Override
    public void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialog.show();
            }
        });
    }

    @Override
    public void hideProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog.isShowing()) {
                    mDialog.hide();
                }
            }
        });
    }

    @Override
    public void showResult(List<String> data) {
        toast("共生成" + data.size() + "条号码");
        mCreNumList.clear();
        mCreNumList.addAll(data);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void success() {
        hideProgressDialog();
        ToastUtil.showToast(this, "保存成功");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}
