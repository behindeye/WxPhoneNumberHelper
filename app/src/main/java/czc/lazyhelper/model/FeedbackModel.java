package czc.lazyhelper.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by czc on 2017/7/6.
 */

public class FeedbackModel extends BmobObject{
     String userId;
     String qq;
     String feedback;
     String wechat;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }
}
