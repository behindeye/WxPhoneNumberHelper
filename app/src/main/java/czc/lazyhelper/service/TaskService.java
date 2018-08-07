package czc.lazyhelper.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.greenrobot.eventbus.EventBus;

import czc.lazyhelper.constant.Const;
import czc.lazyhelper.event.EasyEvent;
import czc.lazyhelper.event.EasyEventBuilder;
import czc.lazyhelper.event.EventType;
import czc.lazyhelper.presenter.AddContactController;
import czc.lazyhelper.presenter.AddNearHumanPresenter;
import czc.lazyhelper.presenter.AddNearHumanPresenter2;
import czc.lazyhelper.presenter.BaseTaskPresenter;
import czc.lazyhelper.util.PreferenceHelper;
import czc.lazyhelper.view.TaskView;

/**
 * 核心服务：执行自动化任务
 * Created by czc on 2017/6/13.
 */
public class TaskService extends AccessibilityService implements TaskView {

    private static final String TAG = "TaskService";
    private BaseTaskPresenter mController;

    /**
     * 服务没有连接
     */
    public static final int STATUS_UNCONNECT = 0;
    /**
     * 服务连接
     */
    public static final int STATUS_CONNECT = 1;
    /**
     * 服务状态
     */
    public static int SERVICE_STATUS = STATUS_UNCONNECT;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onServiceConnected() {
        SERVICE_STATUS = STATUS_CONNECT;
        Log.i(TAG, "onServiceConnected");
        int type = PreferenceHelper.getInt(Const.PREF_KEY_ADD_PEOPLE_TYPE, Const.KEY_ADD_FRIEND);
        if (type == Const.KEY_ADD_FRIEND) {
            mController = new AddContactController(this);
        } else if (type == Const.KEY_ADD_NEAR_PEOPLE) {
            mController = new AddNearHumanPresenter2(this);
        } else if (type == Const.KEY_QUICK_ADD_NEAR_PEOPLE) {
            mController = new AddNearHumanPresenter(this);
        }
        mController.bindService(this);

        EasyEvent event = new EasyEventBuilder<Boolean>(EventType.TYPE_SERVICE_HAS_CONNECTED)
                .setValue(true)
                .build();
        EventBus.getDefault().post(event);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        SERVICE_STATUS = STATUS_CONNECT;
        boolean canAutoAddPeople = PreferenceHelper.getBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, false);
        if (!canAutoAddPeople) {
            if (mController != null) {
                mController.doTask(event, getRoot());
            }
        }
    }

    public AccessibilityNodeInfo getRoot() {
        return getRootInActiveWindow();
    }

    @Override
    public void onInterrupt() {
        SERVICE_STATUS = STATUS_UNCONNECT;
        Log.e(TAG, "onInterrupt");
    }

    @Override
    public void onDestroy() {
        SERVICE_STATUS = STATUS_UNCONNECT;
        Log.e(TAG, "onDestroy");
        getRootInActiveWindow().recycle();
    }

    @Override
    public void performBack() {
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public AccessibilityNodeInfo getActiveRoot() {
        return getRootInActiveWindow();
    }
}
