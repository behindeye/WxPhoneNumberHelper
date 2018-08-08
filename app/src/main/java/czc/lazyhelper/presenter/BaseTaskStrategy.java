package czc.lazyhelper.presenter;

import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import czc.lazyhelper.manager.DBManager;
import czc.lazyhelper.model.SentenceModelDao;
import czc.lazyhelper.model.WechatFriendModelDao;
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

	private TaskView mView;

	public BaseTaskStrategy() {

	}

	@Override
	public void bindTask(TaskView view) {
		mView = view;
	}

	@Override
	public void doTask(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {

	}


	protected String event(int eventType) {
		return AccessibilityEvent.eventTypeToString(eventType);
	}

	public void performBack() {
		mView.performBack();
	}

	public void performAction(int action) {
		mView.performAction(action);
	}

	public Context getContext() {
		return mView.getContext();
	}

	public AccessibilityNodeInfo getRoot() {
		return mView.getActiveRoot();
	}

	public WechatFriendModelDao getWxFrendDao() {
		return DBManager.getInstance(getContext()).getSession().getWechatFriendModelDao();
	}

	public SentenceModelDao getSentenceDao() {
		return DBManager.getInstance(getContext()).getSession().getSentenceModelDao();
	}


}
