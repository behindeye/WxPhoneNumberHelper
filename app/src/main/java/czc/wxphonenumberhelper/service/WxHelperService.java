package czc.wxphonenumberhelper.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import czc.wxphonenumberhelper.manager.FloatWindowManager;
import rx.functions.Action1;

public class WxHelperService extends Service {
	public WxHelperService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!FloatWindowManager.isWindowShowing()) {
			try {
				FloatWindowManager.createSmallWindow(getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (Build.VERSION.SDK_INT < 18) {
			startForeground(1009, new Notification());
			startService(new Intent(this, WxHelperInnerService.class));
		} else {
			startForeground(1009, new Notification());
		}
		return START_STICKY;
	}

	public static class WxHelperInnerService extends Service {

		@Nullable
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			startForeground(1009, new Notification());
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
