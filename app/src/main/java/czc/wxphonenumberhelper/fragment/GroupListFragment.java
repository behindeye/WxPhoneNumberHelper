package czc.wxphonenumberhelper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.adapter.GroupAdapter;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.model.GroupInfo;

/**
 * Created by czc on 2017/8/2.
 */

public class GroupListFragment extends BaseFragment {

    private int pageSize = 20;
    private int pageIndex = 0;

    private GroupAdapter mAdapter;
    private List<GroupInfo> mGroupList = new ArrayList<>();

    @BindView(R.id.lv_group_list)
    ListView mLvGroupList;

    @BindView(R.id.btn_publish)
    Button mBtnPublish;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_group_list, null);
        ButterKnife.bind(this, view);
        mAdapter = new GroupAdapter(getContext(), mGroupList);
        mLvGroupList.setAdapter(mAdapter);
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadmore(true);
        mRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageIndex++;
                BmobQuery<GroupInfo> query = new BmobQuery<>();
                query.setLimit(pageSize).setSkip(pageIndex * pageSize).order("-createdAt")
                        .findObjects(new FindListener<GroupInfo>() {
                            @Override
                            public void done(List<GroupInfo> list, BmobException e) {
                                if (e == null) {
                                    if (list != null && list.size() > 0) {
                                        mGroupList.addAll(list);
                                        mAdapter.notifyDataSetChanged();
                                    }else{
                                        toast("没有更多数据");
                                    }
                                }else {
                                    toast("加载失败");
                                }
                                mRefreshLayout.finishLoadmore();
                            }
                        });
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageIndex = 0;
                BmobQuery<GroupInfo> query = new BmobQuery<>();
                query.setLimit(pageSize).setSkip(pageIndex).order("-createdAt")
                        .findObjects(new FindListener<GroupInfo>() {
                            @Override
                            public void done(List<GroupInfo> list, BmobException e) {
                                if (e == null) {
                                    if (list != null && list.size() > 0) {
                                        mGroupList.clear();
                                        mGroupList.addAll(list);
                                        mAdapter.notifyDataSetChanged();
                                    }else{
                                        toast("暂无数据");
                                    }
                                } else {
                                    toast("刷新失败");
                                }
                                mRefreshLayout.finishRefresh();
                            }
                        });
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefreshLayout.autoRefresh(100);
        BmobQuery<GroupInfo> query = new BmobQuery<>();
        query.setLimit(pageSize).setSkip(pageIndex).order("-createdAt")
                .findObjects(new FindListener<GroupInfo>() {
                    @Override
                    public void done(List<GroupInfo> list, BmobException e) {
                        if (e == null) {
                            if (list != null && list.size() > 0) {
                                mGroupList.clear();
                                mGroupList.addAll(list);
                                mAdapter.notifyDataSetChanged();
                            }else{
                                toast("暂无数据");
                            }
                        }
                        mRefreshLayout.finishRefresh();
                    }
                });
    }

    @OnClick(R.id.btn_publish)
    void onClickPublishBtn() {
        mHost.changeFragment(Const.PUBLISH_GROUP_FRAMENT);
    }

    @OnItemClick(R.id.lv_group_list)
    void onItemClick(int index) {
        mHost.jump2PagerFragment(mGroupList, index);
    }

    public void refresh() {
        mRefreshLayout.autoRefresh();
    }
}
