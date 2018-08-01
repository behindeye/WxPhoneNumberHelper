package czc.wxphonenumberhelper.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import czc.wxphonenumberhelper.MyApplication;
import czc.wxphonenumberhelper.activity.CreateNumberActivity;
import czc.wxphonenumberhelper.activity.MainActivity;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.manager.DBManager;
import czc.wxphonenumberhelper.model.PhoneRecord;
import czc.wxphonenumberhelper.util.ContactUtil;
import czc.wxphonenumberhelper.util.ToastUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by czc on 2017/6/25.
 */

public class CreateNumberControllerImpl implements PhonePresenter {
    private ProgressDialog mDialog;
    private CreateNumberActivity mActivity;
    private List<String> mCreNumList = new ArrayList<>();

    private String mQhNumber;
    private String mCenterNumber;
    private String mNumberFlag;
    private int mNumber;

    public CreateNumberControllerImpl(Activity act) {
        mActivity = (CreateNumberActivity) act;
        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage("正在生成中，请耐心等待···");
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void initData() {
        Bundle bundle = mActivity.getIntent().getExtras();
        if (bundle != null) {
            mQhNumber = bundle.getString(Const.KEY_HD);
            mNumberFlag = bundle.getString(Const.KEY_CREATE_PHONE_NUMBER_FLAG);
            mCenterNumber = bundle.getString(Const.KEY_CENTER_NUMBER);
            mNumber = bundle.getInt(Const.KEY_CREATE_PHONE_NUMBER);
        }
    }

    @Override
    public void createNumber() {
        mCreNumList.clear();
        createPhoneNumber(mNumber);
        notifyDataSetChange();
    }

    @Override
    public void save() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Log.i("czc", mCreNumList.size() + "个");

                List<PhoneRecord> recordList = new ArrayList<PhoneRecord>();
                for (String number : mCreNumList) {
                    String name = mNumberFlag + number;
                    PhoneRecord record = new PhoneRecord();
                    record.setName(name);
                    record.setNumber(number);
                    recordList.add(record);
                    ContactUtil.insert(mActivity, name, number);
                }
                DBManager.getInstance(MyApplication.getAppContext())
                        .getSession()
                        .getPhoneRecordDao()
                        .insertInTx(recordList);
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
                        ToastUtil.showToast(mActivity, "保存成功");
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(intent);
                    }
                });

    }

    @Override
    public void notifyDataSetChange() {
        mActivity.notifyDataSetChange(mCreNumList);
    }

    private void createPhoneNumber(int number) {
        for (int i = 0; i < number; i++) {
            String phoneNumber = mQhNumber + mCenterNumber + getEnd4Number();
//            Log.i("czc", phoneNumber);
            if (!mCreNumList.contains(phoneNumber)) {
                mCreNumList.add(phoneNumber);
            }
        }
        if (mCreNumList.size() < mNumber) {
            Log.i("czc", "agin");
            createPhoneNumber(mNumber - mCreNumList.size());
        }
    }

    private String getEnd4Number() {
        String endNumber = "";
        Random random = new Random();
        int nextInt = random.nextInt(9998) % (9998 - 1 + 1) + 1;
        if (nextInt < 10) {
            endNumber = "000" + nextInt;
        } else if (nextInt < 100) {
            endNumber = "00" + nextInt;
        } else if (nextInt < 1000) {
            endNumber = "0" + nextInt;
        } else {
            endNumber = "" + nextInt;
        }
        return endNumber;
    }
}
