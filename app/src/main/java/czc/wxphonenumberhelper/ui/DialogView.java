package czc.wxphonenumberhelper.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.util.ScreenUtil;
import czc.wxphonenumberhelper.util.ToolsUtil;

public class DialogView extends Dialog {

	private Context context;
	private Button btn_ok;
	private Button btn_cancel;
	private Button btn_custom;
	private TextView tv_title;
	private View view_line1, view_line2;
	private RelativeLayout rl_content;
	private LinearLayout ll_btn;
	private String titleStr;
	private String desStr;
	private View viewcontent;

	private TextView tv_des;
	private TextView tv_content;
	private String okStr = null;
	private String cancelStr = null;
	private String customStr = null;

	private float customHeigt = 0; // 自定义高度
	private int backgoundResId = -1;

	private String tag;

	public DialogView(Context context) {
		super(context, R.style.dialog);
		this.context = context;
		setCancelable(true);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		DisplayMetrics mDm = new DisplayMetrics();
		window.getWindowManager().getDefaultDisplay().getMetrics(mDm);
		int globalHeight = mDm.heightPixels;
		int stateBarHeight = ScreenUtil.dip2px(20);
		try {
			stateBarHeight = ToolsUtil.getStateBarHeight(context);
		} catch (Exception e) {
			e.printStackTrace();
		} // 状态栏高度

		okStr = context.getResources().getString(R.string.dialog_ok);
		cancelStr = context.getResources().getString(R.string.dialog_cancel);

		lp.height = globalHeight - stateBarHeight - ScreenUtil.dip2px(100);
		getWindow().setAttributes(lp);
	}

	private boolean IsdismissCustom = true;

	private boolean IsdismissOK = true;

	public void dismissdialog(boolean IsdismissOK) {
		this.IsdismissOK = IsdismissOK;
	}

	public void dismissdialogCustom(boolean IsdissmissCustom) {
		this.IsdismissCustom = IsdissmissCustom;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_dialogview);
		tv_title = (TextView) findViewById(R.id.tv_title);
		rl_content = (RelativeLayout) findViewById(R.id.rl_content);
		tv_des = (TextView) findViewById(R.id.tv_des);
		if (backgoundResId != -1) {
			rl_content.setBackgroundResource(backgoundResId);
		}
		view_line1 = (View) findViewById(R.id.view_line1);
		view_line2 = (View) findViewById(R.id.view_line2);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_custom = (Button) findViewById(R.id.btn_custom);
		ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
		btn_ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (IsdismissOK) {
					dismiss();
				}
				if (mOnDialogClickListener != null) {
					mOnDialogClickListener.OnOKClickListener(v);
				}
			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				if (mOnDialogClickListener != null) {
					mOnDialogClickListener.OnCancelClickListener(v);
				}
			}
		});

		btn_custom.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (IsdismissCustom) {
					dismiss();
				}
				if (mOnDialogClickListener != null) {
					mOnDialogClickListener.OnCustomClickListener(v);
				}
			}
		});

		if (titleStr != null) {
			tv_title.setText(titleStr);
		}

		if (desStr != null) {
			tv_des.setVisibility(View.VISIBLE);
			tv_des.setText(desStr);
		} else {
			tv_des.setVisibility(View.GONE);
		}

		if (okStr != null) {
			btn_ok.setText(okStr);
		} else {
			view_line1.setVisibility(View.GONE);
			btn_ok.setVisibility(View.GONE);
		}

		if (cancelStr != null) {
			btn_cancel.setText(cancelStr);
		} else {
			view_line2.setVisibility(View.GONE);
			btn_cancel.setVisibility(View.GONE);
		}

		if (customStr != null) {
			btn_custom.setText(customStr);
			view_line1.setVisibility(View.VISIBLE);
			btn_custom.setVisibility(View.VISIBLE);
		} else {
			view_line1.setVisibility(View.GONE);
			btn_custom.setVisibility(View.GONE);
		}

		if (okStr == null && cancelStr == null && customStr == null) {
			ll_btn.setVisibility(View.GONE);
		} else {
			ll_btn.setVisibility(View.VISIBLE);
		}

		if (viewcontent != null) {

			LayoutParams rlp = null;

			if (customHeigt != 0) {

				rlp = new LayoutParams(
						new LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(customHeigt)));
			} else {

				rlp = new LayoutParams(
						new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			rl_content.addView(viewcontent, rlp);
		} else {
			if (tv_content != null) {
				ScrollView scrollView = new ScrollView(context);
				scrollView.setFadingEdgeLength(0);
				scrollView.addView(tv_content);
				rl_content.addView(scrollView);
			}
		}
	}

	public void setTitle(String title) {
		titleStr = title;
	}

	public void setDes(String des) {
		desStr = des;
	}

	public void setDesColor(int color) {
		tv_des.setTextColor(color);
	}

	public void setBackgroundResource(int resId) {
		backgoundResId = resId;
	}

	public void setOk(String okStr) {
		this.okStr = okStr;
	}

	public void setCancel(String cancelStr) {
		this.cancelStr = cancelStr;
	}

	public void setCustomStr(String customStr) {
		this.customStr = customStr;
	}

	public void setcontent(View content, float customHeigt) {
		this.customHeigt = customHeigt;
		if (content != null) {
			viewcontent = content;
		}
	}

	public void setcontent(View content) {
		if (content != null) {
			viewcontent = content;
		}
	}

	public void setMessage(String msg, boolean isHtml) {
		tv_content = new TextView(context);
		if (isHtml) {
			try {
				tv_content.setText(Html.fromHtml(msg));
			} catch (Exception e) {
				tv_content.setText(msg);
			}
		} else {
			tv_content.setTextColor(getContext().getResources().getColor(R.color.normalcolor));
			tv_content.setText(msg);
		}
		tv_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
	}

	public void setMessage(String msg) {
		setMessage(msg, false);
	}

	public void setOnShowListener(OnShowListener listener) {
		super.setOnShowListener(listener);
	}

	private OnDialogClickListener mOnDialogClickListener = null;

	public void AddDialogClickListener(OnDialogClickListener mOnDialogClickListener) {
		this.mOnDialogClickListener = mOnDialogClickListener;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setSystemLevel() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}
}
