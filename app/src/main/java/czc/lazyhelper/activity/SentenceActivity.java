package czc.lazyhelper.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemLongClick;
import czc.lazyhelper.R;
import czc.lazyhelper.base.BaseActivity;
import czc.lazyhelper.manager.DBManager;
import czc.lazyhelper.model.SentenceModel;
import czc.lazyhelper.ui.DialogView;
import czc.lazyhelper.util.ScreenUtil;

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
//        if (PreferenceHelper.getBoolean(Const.PREF_KEY_SHOW_INPUT_SENTENCE_DIALOG, true)) {
//            mDv = new DialogView(this);
//            mDv.setMessage("不要输入违法涉黄的信息");
//            mDv.setCancel("不再提示");
//            mDv.setOk("确定");
//            mDv.show();
//            mDv.AddDialogClickListener(new SimpleDialogClickListener() {
//                @Override
//                public void OnCancelClickListener(View v) {
//                    toast("设置成功");
//                    PreferenceHelper.putBoolean(Const.PREF_KEY_SHOW_INPUT_SENTENCE_DIALOG, false);
//                }
//            });
//        }
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
                        log(position + "");
                        // delete
                        String sentence = mSentenceList.remove(position);
                        SentenceModel record = new SentenceModel();
                        record.setSentence(sentence);
                        DBManager.getInstance(SentenceActivity.this).getSession().getSentenceModelDao().delete(record);
                        mListAdapter.notifyDataSetChanged();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        ArrayList<String> list = new ArrayList<>();
        List<SentenceModel> recordList = DBManager.getInstance(this).getSession().getSentenceModelDao().loadAll();
        if (recordList != null) {
            for (SentenceModel record : recordList) {
                list.add(record.getSentence());
            }
        }
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
            SentenceModel record = new SentenceModel();
            record.setSentence(sentence);
            DBManager.getInstance(this).getSession().getSentenceModelDao().insert(record);
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
}
