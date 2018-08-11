package czc.lazyhelper.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import czc.lazyhelper.MyApplication;
import czc.lazyhelper.activity.MainActivity;
import czc.lazyhelper.constant.Const;
import czc.lazyhelper.manager.DBManager;
import czc.lazyhelper.manager.ParseManager;
import czc.lazyhelper.model.SentenceModelDao;
import czc.lazyhelper.model.WechatFriendModelDao;
import czc.lazyhelper.service.TaskService;
import czc.lazyhelper.util.PreferenceHelper;
import czc.lazyhelper.util.ToastUtil;
import czc.lazyhelper.view.TaskView;

/**
 * Created by czc on 2017/7/29.
 */

public abstract class BaseTaskStrategy implements TaskStrategy {

    protected static final String TAG = "@@@";
    protected int mType;
    protected String mClassName;

    //
    public static final String LIST_VIEW = "android.widget.ListView";
    public static final String TEXT_VIEW = "android.widget.TextView";
    public static final String IMAGE_VIEW = "android.widget.ImageView";
    public static final String EDIT_TEXT = "android.widget.EditText";
    public static final String RELATIVE_LAYOUT = "android.widget.RelativeLayout";
    public static final String LINEAR_LAYOUT = "android.widget.LinearLayout";

    //page
    protected static final String SayHiWithSnsPermissionUI = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
    protected static final String ContactInfoUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    protected static final String WxViewPager = "com.tencent.mm.ui.mogic.WxViewPager";
    protected static final String FMessageConversationUI = "com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI";
    protected static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";
    protected static final String DialogUI = "com.tencent.mm.ui.base.h";
    protected static final String NearbyFriendsUI = "com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI";
    protected static final String SayHiEditUI = "com.tencent.mm.ui.contact.SayHiEditUI";

    protected Object mLock = new Object();

    protected ArrayList<String> mSentenceList = new ArrayList<>();
    protected Map<String, Boolean> mRecordMap = Collections.synchronizedMap(new HashMap());

    private int times = 0;
    private boolean mIsThreadExc;
    private TaskView mView;
    protected boolean mIsStateChanged;
    protected boolean mIsContentChanged;

    public BaseTaskStrategy() {

    }

    @Override
    public final void bindTask(TaskView view) {
        mView = view;
    }

    @Override
    public final void doTask(AccessibilityEvent event, AccessibilityNodeInfo root) {
        mType = event.getEventType();
        mClassName = (String) event.getClassName();
        mIsStateChanged = mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        mIsContentChanged = mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        monitorTaskIsRunning(root);
        performTask(event, root);
    }

    /**
     * 执行任务操作
     * @param event
     * @param root
     */
    protected abstract void performTask(AccessibilityEvent event, AccessibilityNodeInfo root);


    protected String event(int eventType) {
        return AccessibilityEvent.eventTypeToString(eventType);
    }

    public void performBack() {
        if (mView != null) {
            mView.performBack();
        }
    }

    public void performAction(int action) {
        if (mView != null) {
            mView.performAction(action);
        }
    }

    public Context getContext() {
        return mView.getContext();
    }

    public AccessibilityNodeInfo getRoot() {
        if (mView == null) return null;
        return mView.getActiveRoot();
    }

    public WechatFriendModelDao getWxFrendDao() {
        return DBManager.getInstance(MyApplication.getAppContext()).getSession().getWechatFriendModelDao();
    }

    public SentenceModelDao getSentenceDao() {
        return DBManager.getInstance(MyApplication.getAppContext()).getSession().getSentenceModelDao();
    }

    public void toast(String msg) {
        if (getContext() == null) {
            ToastUtils.showShort(msg);
        } else {
            ToastUtil.showToast(getContext(), msg);
        }
    }

    public boolean page(String key) {
        if (mClassName != null && !StringUtils.isEmpty(key)) {
            return mClassName.equals(getPageRoute(key));
        }
        return false;

    }

    public String getPageRoute(String key) {
        return ParseManager.getInstance().getPagesMap().get(key).getName();
    }

    public String getNodeId(String key) {
        return ParseManager.getInstance().getNodesMap().get(key).getId();
    }

    public String getNodeText(String key) {
        return ParseManager.getInstance().getNodesMap().get(key).getText();
    }

    public String getNodeClass(String key) {
        return ParseManager.getInstance().getNodesMap().get(key).getClassName();
    }

    public void monitorTaskIsRunning(final AccessibilityNodeInfo root) {
        times = 0;
        if (!mIsThreadExc) {
            mIsThreadExc = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean canAutoAddPeople = PreferenceHelper.getBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, false);
                    boolean connect = TaskService.SERVICE_STATUS == TaskService.STATUS_CONNECT;
                    boolean shouldStop = SPUtils.getInstance().getBoolean(Const.PREF_IS_STOP_MONITOR_THREAD);
                    while (times < 40
                            && connect
                            && !canAutoAddPeople
                            && !shouldStop) {
                        try {
                            Thread.sleep(1000);
                            ++times;
                            Log.e(TAG, "running : " + times + "s");
                            shouldStop = SPUtils.getInstance().getBoolean(Const.PREF_IS_STOP_MONITOR_THREAD);
                            canAutoAddPeople = PreferenceHelper.getBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, false);
                            connect = TaskService.SERVICE_STATUS == TaskService.STATUS_CONNECT;
                            if (root == null) {
                                performBack();
                            }
                            if (times == 10) {
                                if (root != null) {
                                    toast("刷新界面，重试×1");
                                    root.refresh();
                                }
                            }
                            if (times == 20) {
                                if (root != null) {
                                    toast("刷新界面，重试×2");
                                    root.refresh();
                                }
                            }
                            if (times == 30) {
                                if (root != null) {
                                    toast("出错~回退界面");
                                    performBack();
                                }
                            }
                            if (times == 40) {
                                toast("超时");
                                if (root != null) {
                                    if (getContext() != null) {
                                        getContext().startActivity(new Intent(getContext(), MainActivity.class));
                                    }
                                } else {
                                    if (root != null) {
                                        performBack();
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mIsThreadExc = false;
                }
            }).start();
        }
    }

}
