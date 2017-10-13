package czc.wxphonenumberhelper.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.model.GroupInfo;

/**
 * Created by czc on 2017/8/3.
 */

public class GroupAdapter extends BaseAdapter {
    private List<GroupInfo> mGroupList;
    private Context mContext;

    public GroupAdapter(Context mContext, List<GroupInfo> mGroupList) {
        this.mGroupList = mGroupList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mGroupList.size();
    }

    @Override
    public GroupInfo getItem(int position) {
        return mGroupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_group, null);
            hodler = new ViewHodler();
            hodler.title = (TextView) convertView.findViewById(R.id.tv_title);
            hodler.wxnumber = (TextView) convertView.findViewById(R.id.tv_wx_number);
            hodler.introduce = (TextView) convertView.findViewById(R.id.tv_introduce);
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        GroupInfo item = getItem(position);
        hodler.title.setText(item.title);
        hodler.wxnumber.setText("微信号：" + item.wxNumber);
        hodler.introduce.setText(item.groupIntroduce);
        return convertView;
    }

    class ViewHodler {
        public TextView title;
        public TextView wxnumber;
        public TextView introduce;

    }
}
