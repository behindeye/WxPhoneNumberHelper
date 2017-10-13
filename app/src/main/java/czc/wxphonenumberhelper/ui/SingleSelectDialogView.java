package czc.wxphonenumberhelper.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.util.ScreenUtil;

/**
 * @author hedewen
 * @time 2014年12月11日 下午12:54:56
 */
public class SingleSelectDialogView {

	private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private ListView listView;
	private SimpleAdapter adapter;
	private OnOKListener mOnOKListener;
	private OnButtonLister mOnButtonLister;
	private String selectText = null;
	private int selectIndex = -1;
	public DialogView dv;

	public void setOnOKListener(OnOKListener mOnOKListener) {
		this.mOnOKListener = mOnOKListener;
	}

	public void setOnButtonLister(OnButtonLister mOnButtonLister) {
		this.mOnButtonLister = mOnButtonLister;
	}

	public SingleSelectDialogView(Context context, String title, List<String> items, int selectedIndex) {
		String[] itemsnew = new String[items.size()];
		items.toArray(itemsnew);
		init(context, title, itemsnew, selectedIndex);

	}

	private void init(final Context context, String title, String[] items, int selectedIndex) {
		listView = new ListView(context);
		listView.setFadingEdgeLength(0);
		listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setDivider(new ColorDrawable(context.getResources().getColor(R.color.line_color)));
		listView.setDividerHeight(1);
		adapter = new SimpleAdapter(context, list, R.layout.listformat_singleselect, new String[] { "text", "state" },
				new int[] { R.id.text, R.id.iv_select });
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				for (int i = 0; i < list.size(); i++) {
					list.get(i).put("state",
							i == position ? R.drawable.icon_check : R.drawable.icon_uncheck);
				}
				selectText = list.get(position).get("text").toString();
				selectIndex = position;
				adapter.notifyDataSetChanged();
				dv.dismiss();
				if (mOnOKListener != null) {
					mOnOKListener.onOK(selectIndex, selectText);
				}
			}
		});
		listView.setAdapter(adapter);
		setList(items, selectedIndex);

		dv = new DialogView(context);
		dv.setTitle(TextUtils.isEmpty(title) ? "请选择" : title);
		dv.setcontent(listView, ScreenUtil.dip2px(120));
		dv.dismissdialog(false);
		dv.setOk(null);
		dv.setCancel(null);
		dv.AddDialogClickListener(new OnDialogClickListener() {
			public void OnOKClickListener(View v) {
				if (selectText == null) {
					Toast.makeText(context, "请选择", Toast.LENGTH_SHORT).show();
				} else {
					dv.dismiss();
					if (mOnOKListener != null) {
						mOnOKListener.onOK(selectIndex, selectText);
					} else if (mOnButtonLister != null) {
						mOnButtonLister.onOK(selectIndex, selectText);
					}
				}
			}

			public void OnCustomClickListener(View v) {

			}

			public void OnCancelClickListener(View v) {
				if (mOnButtonLister != null) {
					mOnButtonLister.onCancle();
				}
			}
		});

	}

	public SingleSelectDialogView(final Context context, String title, String[] items, int selectedIndex) {
		init(context, title, items, selectedIndex);
	}

	public void setDes(String des) {
		dv.setDes(des);
	}

	public void setDesColor(int color) {
		dv.setDesColor(color);
	}

	public void setTitle(String title) {
		dv.setTitle(title);
	}

	public void setList(String[] items, int selectedIndex) {
		list.clear();
		for (int i = 0; i < items.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("text", items[i]);
			map.put("state", i == selectedIndex ? R.drawable.icon_check : R.drawable.icon_uncheck);
			list.add(map);
			if (selectedIndex == i) {
				selectText = items[i];
				selectIndex = selectedIndex;
			}
		}
		adapter.notifyDataSetChanged();
	}

	public void show() {
		dv.show();
	}

	public interface OnOKListener {
		void onOK(int postion, String text);
	}

	public interface OnButtonLister {
		void onOK(int postion, String text);

		void onCancle();
	}
}
