package czc.wxphonenumberhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.activity.MainActivity;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.manager.FloatWindowManager;
import czc.wxphonenumberhelper.util.PreferenceHelper;

/**
 * Created by czc on 2017/6/30.
 */

public class FloatWindowBigView extends LinearLayout {

    public static int viewWidth;
    public static int viewHeight;

    public FloatWindowBigView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_window_flow_content, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        initView(context);
    }

    private void initView(final Context context) {
        Button home = (Button) findViewById(R.id.btn_home_page);
        Button begin = (Button) findViewById(R.id.btn_begin_add_people);
        Button stop = (Button) findViewById(R.id.btn_stop_add_people);
        Button close = (Button) findViewById(R.id.btn_close_flow_window);
        Button back = (Button) findViewById(R.id.btn_back);
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                FloatWindowManager.removeBigWindow(context);
                FloatWindowManager.createSmallWindow(context);
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        begin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceHelper.putBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, false);
                FloatWindowManager.removeBigWindow(context);
                FloatWindowManager.createSmallWindow(context);
            }
        });

        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceHelper.putBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, true);
                EventBus.getDefault().post(new Boolean(true));
                FloatWindowManager.removeBigWindow(context);
                FloatWindowManager.createSmallWindow(context);
            }
        });

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
                FloatWindowManager.removeBigWindow(context);
                FloatWindowManager.removeSmallWindow(context);
            }
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                FloatWindowManager.removeBigWindow(context);
                FloatWindowManager.createSmallWindow(context);
            }
        });
    }

}
