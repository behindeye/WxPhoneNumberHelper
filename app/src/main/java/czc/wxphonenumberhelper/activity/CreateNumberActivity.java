package czc.wxphonenumberhelper.activity;


import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.controller.CreateNumberControllerImpl;
import czc.wxphonenumberhelper.controller.PhoneController;

/**
 * 生成号码
 */
public class CreateNumberActivity extends BaseActivity {
    private List<String> mCreNumList = new ArrayList<>();
    private ArrayAdapter<String> mListAdapter;
    private PhoneController mController;

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
        mBtnSave.setEnabled(false);
        mController.save();
    }

    public void notifyDataSetChange(List<String> mData) {
        mCreNumList.clear();
        mCreNumList.addAll(mData);
        mListAdapter.notifyDataSetChanged();
    }

}
