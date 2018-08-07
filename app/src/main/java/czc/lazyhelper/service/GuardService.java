package czc.lazyhelper.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import czc.lazyhelper.activity.MainActivity;
import czc.lazyhelper.constant.Const;
import czc.lazyhelper.event.EasyEvent;
import czc.lazyhelper.event.EventType;
import czc.lazyhelper.manager.FloatWindowManager;
import czc.lazyhelper.util.WxAppUtil;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 守护服务
 */
public class GuardService extends Service {

    private Subscription mSubscribe;
    private static final int SERVICE_ID = 10010;

    public GuardService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void jumpToWxAfterServiceConnect(EasyEvent event) {
        startActivity(new Intent(this, MainActivity.class));
        if (event.type == EventType.TYPE_SERVICE_HAS_CONNECTED) {
            mSubscribe = Observable.interval(1, TimeUnit.SECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long time) {
                            if (time > 3) {
                                mSubscribe.unsubscribe();
                                WxAppUtil.jumpToWx(GuardService.this);
                            } else {
                                ToastUtils.showShort("倒计时" + (3 - time) + "s,即将跳转到微信");
                            }
                        }
                    });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(Const.EXTRA_OPEN_FLOWWINDOW, false)) {
                if (!FloatWindowManager.isWindowShowing()) {
                    try {
                        FloatWindowManager.createSmallWindow(getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(SERVICE_ID, new Notification());
            startService(new Intent(this, GuardInnerService.class));
        } else {
            startForeground(SERVICE_ID, new Notification());
        }
        return START_STICKY;
    }

    public static class GuardInnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(SERVICE_ID, new Notification());
            rx.Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            stopForeground(true);
                            stopSelf();
                        }
                    });
            return START_STICKY;
        }
    }
}
