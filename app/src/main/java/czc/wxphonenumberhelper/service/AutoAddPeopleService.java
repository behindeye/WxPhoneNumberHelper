package czc.wxphonenumberhelper.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.controller.AddContactController;
import czc.wxphonenumberhelper.controller.AddNearPeopleController;
import czc.wxphonenumberhelper.controller.AddPeopleInterface;
import czc.wxphonenumberhelper.controller.QuickAddContactController;
import czc.wxphonenumberhelper.util.PreferenceHelper;

/**
 * Created by czc on 2017/6/13.
 */

public class AutoAddPeopleService extends AccessibilityService {

    private static final String TAG = "czc";
    private AddPeopleInterface mController;
    private Timer mTimer;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Const.ACTION_CLEAR_RECORD)) {
                if (mController != null) {
                    mController.clearRecord();
                }
            }
        }
    };

    @Override
    protected void onServiceConnected() {
        Log.i(TAG, "onServiceConnected");
        EventBus.getDefault().register(this);
        int type = PreferenceHelper.getInt(Const.PREF_KEY_ADD_PEOPLE_TYPE, Const.KEY_ADD_FRIEND);
        if (type == Const.KEY_ADD_FRIEND) {
            mController = new AddContactController(this);
        } else if (type == Const.KEY_ADD_NEAR_PEOPLE) {
            mController = new AddNearPeopleController(this);
        } else if (type == Const.KEY_QUICK_ADD_NEAR_PEOPLE) {
            mController = new AddNearPeopleController(this);
        }

        mController.initSentenceAndRecord();

        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new SaveTask(), 0, 10000);
        }
        registerReceiver(mReceiver, new IntentFilter(Const.ACTION_CLEAR_RECORD));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "event=" + AccessibilityEvent.eventTypeToString(event.getEventType()));
        Log.i(TAG, "className=" + event.getClassName());

        boolean canAutoAddPeople = PreferenceHelper.getBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, false);
        if (!canAutoAddPeople) {
            if (mController != null) {
                mController.doTask(event);
            }
        }
    }

    public AccessibilityNodeInfo getRoot() {
        return getRootInActiveWindow();
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt");
        if (mController != null) {
            mController.saveRecord();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (mController != null) {
            mController.saveRecord();
        }
        EventBus.getDefault().unregister(this);
        getRootInActiveWindow().recycle();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveEvent(Boolean isSave) {
        if (isSave.booleanValue()) {
            if (mController != null) {
                mController.saveRecord();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeController(Integer type) {
        Log.i(TAG, "changeController");
        if (mController != null) {
            mController.saveRecord();
            mController = null;
        }
        if (type.intValue() == Const.KEY_ADD_FRIEND) {
            mController = new AddContactController(this);
        } else if (type.intValue() == Const.KEY_ADD_NEAR_PEOPLE) {
            mController = new AddNearPeopleController(this);
        } else if (type.intValue() == Const.KEY_QUICK_ADD_NEAR_PEOPLE) {
            mController = new QuickAddContactController(this);
        }
        if (mController != null) {
            mController.initSentenceAndRecord();
        }
    }

    class SaveTask extends TimerTask {

        @Override
        public void run() {
            if (mController != null) {
                new Thread() {
                    @Override
                    public void run() {
                        Log.i(TAG, "saveToLocal");
                        mController.saveRecord();
                    }
                }.start();
            }
        }
    }

}
