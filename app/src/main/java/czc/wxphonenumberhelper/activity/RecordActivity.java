package czc.wxphonenumberhelper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import czc.wxphonenumberhelper.MyApplication;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.base.BaseActivity;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.manager.DBManager;
import czc.wxphonenumberhelper.model.PhoneRecord;
import czc.wxphonenumberhelper.model.PhoneRecordDao;
import czc.wxphonenumberhelper.util.ContactUtil;
import czc.wxphonenumberhelper.util.PreferenceHelper;
import czc.wxphonenumberhelper.util.ScreenUtil;
import czc.wxphonenumberhelper.util.ToastUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 生成记录
 */
public class RecordActivity extends BaseActivity {
    private ArrayList<String> mRecordNumList = new ArrayList<>();
    private ArrayAdapter<String> mListAdapter;

    @BindView(R.id.lv_phone_number)
    SwipeMenuListView mPhoneNumListView;

    @BindView(R.id.tv_empty)
    TextView mEmptyView;

    @BindView(R.id.btn_clearNum)
    Button mBtnDeleteAll;

    private ProgressDialog mDialog;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_record);
    }

    @Override
    protected void initView() {
        mEmptyView.setText("暂无生成记录");
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在删除，请耐心等待···");
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initData() {
        List<PhoneRecord> list = DBManager.getInstance(MyApplication.getAppContext()).getSession().getPhoneRecordDao().loadAll();
        if (list != null && list.size() > 0) {
            for (PhoneRecord record : list) {
                mRecordNumList.add(record.getNumber());
            }
        } else {
            mBtnDeleteAll.setVisibility(View.GONE);
        }
        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mRecordNumList);
        mPhoneNumListView.setAdapter(mListAdapter);
        mPhoneNumListView.setEmptyView(mEmptyView);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setIcon(getResources().getDrawable(R.drawable.icon_delete));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(12);
                deleteItem.setTitleColor(getResources().getColor(R.color.white));
                deleteItem.setWidth(ScreenUtil.dip2px(65));
                menu.addMenuItem(deleteItem);
            }
        };
        mPhoneNumListView.setMenuCreator(creator);
        mPhoneNumListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        log(position + "");
                        deleteNumber(mRecordNumList.get(position));
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    @OnClick(R.id.btn_clearNum)
    void deleteAllNumber() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                PhoneRecordDao phoneRecordDao = DBManager.getInstance(MyApplication.getAppContext()).getSession().getPhoneRecordDao();
                List<PhoneRecord> recordList = phoneRecordDao.loadAll();
                for (PhoneRecord record : recordList) {
                    ContactUtil.delete(RecordActivity.this, record.getName());
                }
                phoneRecordDao.deleteAll();
                mRecordNumList.clear();
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mDialog.show();
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        mDialog.dismiss();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        mListAdapter.notifyDataSetChanged();
                        save();
                        ToastUtil.showToast(RecordActivity.this, "删除成功");
                        sendBroadcast(new Intent(Const.ACTION_CLEAR_RECORD));
                        redirect(MainActivity.class);
                    }
                });
    }

    public boolean deleteNumber(String number) {
        boolean isSuccess = ContactUtil.delete(this, number);
        if (isSuccess) {
            mRecordNumList.remove(number);
            ToastUtil.showToast(this, "删除成功");
            mListAdapter.notifyDataSetChanged();
            save();
            if (mRecordNumList.isEmpty()) {
                mBtnDeleteAll.setVisibility(View.GONE);
            }
        } else {
            ToastUtil.showToast(this, "删除失败");
        }
        return isSuccess;
    }


    public void save() {
        Observable
                .just(mRecordNumList)
                .doOnNext(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> list) {
                        PreferenceHelper.putString(Const.PREF_KEY_NUMBERS, new Gson().toJson(list));
                        PreferenceHelper.putString(Const.PREF_KEY_ADD_PEOPLES_LIST, "");
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
