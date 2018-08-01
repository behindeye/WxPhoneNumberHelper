package czc.wxphonenumberhelper.activity;


import android.Manifest;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.blankj.utilcode.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.presenter.CreateNumberControllerImpl;
import czc.wxphonenumberhelper.presenter.PhonePresenter;

/**
 * 生成号码
 */
public class CreateNumberActivity extends BaseActivity {
    private List<String> mCreNumList = new ArrayList<>();
    private ArrayAdapter<String> mListAdapter;
    private PhonePresenter mController;

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
        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mCreNumList);
        mPhoneNumListView.setAdapter(mListAdapter);
    }

    @Override
    protected void initData() {
        mController = new CreateNumberControllerImpl(this);
        mController.initData();
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

    public void notifyDataSetChange(List<String> mData) {
        mCreNumList.clear();
        mCreNumList.addAll(mData);
        mListAdapter.notifyDataSetChanged();
    }

}
