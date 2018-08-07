package czc.lazyhelper.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import czc.lazyhelper.R;
import czc.lazyhelper.model.GroupInfo;

/**
 * Created by czc on 2017/8/3.
 */

public class GroupPagerAdapter extends PagerAdapter {
    private List<GroupInfo> mGroupList;
    private Context mContext;
    LinkedList<View> mCaches = new LinkedList<View>();

    public GroupPagerAdapter(Context mContext, List<GroupInfo> mGroupList) {
        this.mGroupList = mGroupList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mGroupList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mCaches.add((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = null;
        ViewHodler holder = null;
        if (mCaches.size() == 0) {
            convertView = View.inflate(mContext, R.layout.item_group_pager, null);
            holder = new ViewHodler();
            holder.title = (TextView) convertView.findViewById(R.id.tv_pager_title);
            holder.wxnumber = (TextView) convertView.findViewById(R.id.tv_pager_wx_number);
            holder.introduce = (TextView) convertView.findViewById(R.id.tv_pager_introduce);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_pager_photo);
            convertView.setTag(holder);
        } else {
            convertView = (View) mCaches.removeFirst();
            holder = (ViewHodler) convertView.getTag();
        }
        container.addView(convertView);
        GroupInfo groupInfo = mGroupList.get(position);
        holder.title.setText(groupInfo.title);
        holder.wxnumber.setText(groupInfo.wxNumber);
        holder.introduce.setText(groupInfo.groupIntroduce);
        Picasso.with(mContext)
                .load(groupInfo.photo.getUrl())
                .config(Bitmap.Config.RGB_565)
                .into(holder.imageView);
        return convertView;
    }

    class ViewHodler {
        public TextView title;
        public TextView wxnumber;
        public TextView introduce;
        public ImageView imageView;

    }
}
