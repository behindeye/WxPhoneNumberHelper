package czc.wxphonenumberhelper.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemLongClick;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.ui.DialogView;
import czc.wxphonenumberhelper.ui.SimpleDialogClickListener;
import czc.wxphonenumberhelper.util.PreferenceHelper;
import czc.wxphonenumberhelper.util.ScreenUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static czc.wxphonenumberhelper.constant.Const.PREF_KEY_SENTENCE;

/**
 * 加人验证信息
 */
public class SentenceActivity extends BaseActivity {
    private ArrayList<String> mSentenceList = new ArrayList<>();
    private ArrayAdapter<String> mListAdapter;

    @BindView(R.id.lv_sentence)
    SwipeMenuListView mSentenceListView;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.et_sentence)
    EditText mSentenceEt;
    @BindView(R.id.btn_add_sentence)
    Button btnAddSentence;

    private DialogView mDv;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_sentence);
        if (PreferenceHelper.getBoolean(Const.PREF_KEY_SHOW_INPUT_SENTENCE_DIALOG,true)) {
            mDv = new DialogView(this);
            mDv.setMessage("不要输入违法涉黄的信息");
            mDv.setCancel("不再提示");
            mDv.setOk("确定");
            mDv.show();
            mDv.AddDialogClickListener(new SimpleDialogClickListener(){
                @Override
                public void OnCancelClickListener(View v) {
                    toast("设置成功");
                    PreferenceHelper.putBoolean(Const.PREF_KEY_SHOW_INPUT_SENTENCE_DIALOG,false);
                }
            });
        }
    }

    @Override
    protected void initView() {
        tvEmpty.setText("添加加人验证信息");
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
        mSentenceListView.setMenuCreator(creator);
        mSentenceListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        log(position+"");
                        // delete
                        mSentenceList.remove(position);
                        mListAdapter.notifyDataSetChanged();
                        save();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        String numberJson = PreferenceHelper.getString(PREF_KEY_SENTENCE, "");
        Gson gson = new Gson();
        ArrayList<String> list = gson.fromJson(numberJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (list != null && list.size() > 0) {
            mSentenceList.addAll(list);
        }
        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mSentenceList);
        mSentenceListView.setAdapter(mListAdapter);
        mSentenceListView.setEmptyView(tvEmpty);
    }


    @OnClick(R.id.btn_add_sentence)
    public void clickAddBtn() {
        String sentence = mSentenceEt.getText().toString().trim();
        if (TextUtils.isEmpty(sentence)) {
            toast("请先填写信息");
        } else {
            mSentenceList.add(sentence);
            mListAdapter.notifyDataSetChanged();
            mSentenceEt.setText("");
        }
    }

    @OnItemLongClick(R.id.lv_sentence)
    boolean onItemLongClick(int position) {
        mSentenceList.remove(position);
        mListAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    protected void onPause() {
        save();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        save();
        super.onDestroy();
    }

    private void save() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Gson gson = new Gson();
                PreferenceHelper.putString(Const.PREF_KEY_SENTENCE, gson.toJson(mSentenceList));
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        log("success");
                    }
                });
    }
}
