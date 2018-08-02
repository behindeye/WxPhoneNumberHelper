package czc.wxphonenumberhelper.activity;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.base.BaseActivity;
import czc.wxphonenumberhelper.model.Feedback;

public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.et_qq)
    EditText etQq;
    @BindView(R.id.et_wx)
    EditText etWx;
    @BindView(R.id.et_feedback)
    EditText etFeedback;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_feedback);
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_submit)
    public void onSubmitBtn() {
       String feedback =  etFeedback.getText().toString().trim();
        if (TextUtils.isEmpty(feedback)) {
            toast("请填写反馈内容");
            return;
        }
        Feedback fb = new Feedback();
        fb.setUserId(UUID.randomUUID().toString());
        fb.setQq(etQq.getText().toString().trim());
        fb.setWechat(etWx.getText().toString().trim());
        fb.setFeedback(feedback);
        fb.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    toast("提交成功");
                    back();
                }else {
                    toast("提交失败");
                }
            }
        });
    }
}
