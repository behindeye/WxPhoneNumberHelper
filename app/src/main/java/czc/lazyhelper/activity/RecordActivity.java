package czc.lazyhelper.activity;

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
import czc.lazyhelper.MyApplication;
import czc.lazyhelper.R;
import czc.lazyhelper.base.BaseActivity;
import czc.lazyhelper.constant.Const;
import czc.lazyhelper.manager.DBManager;
import czc.lazyhelper.model.PhoneModel;
import czc.lazyhelper.model.PhoneModelDao;
import czc.lazyhelper.util.ContactUtil;
import czc.lazyhelper.util.PreferenceHelper;
import czc.lazyhelper.util.ScreenUtil;
import czc.lazyhelper.util.ToastUtil;
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
	private List<PhoneModel> mDatas;

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
		mDatas = DBManager.getInstance(this).getSession().getPhoneModelDao().loadAll();
		if (mDatas != null && mDatas.size() > 0) {
			for (PhoneModel record : mDatas) {
				mRecordNumList.add(record.getName());
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
						PhoneModelDao phoneRecordDao = DBManager.getInstance(RecordActivity.this).getSession().getPhoneModelDao();
						phoneRecordDao.delete(mDatas.get(position));
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
				PhoneModelDao phoneRecordDao = DBManager.getInstance(RecordActivity.this).getSession().getPhoneModelDao();
				List<PhoneModel> recordList = phoneRecordDao.loadAll();
				ContactUtil.batchDelContact(RecordActivity.this, recordList);
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
			if (mRecordNumList.isEmpty()) {
				mBtnDeleteAll.setVisibility(View.GONE);
			}
		} else {
			ToastUtil.showToast(this, "删除失败");
		}
		return isSuccess;
	}
}
