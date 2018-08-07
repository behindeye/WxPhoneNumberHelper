package czc.lazyhelper.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import czc.lazyhelper.R;
import czc.lazyhelper.adapter.GroupPagerAdapter;
import czc.lazyhelper.base.BaseFragment;
import czc.lazyhelper.model.GroupInfo;
import czc.lazyhelper.util.ToolsUtil;

/**
 * Created by czc on 2017/8/4.
 */

public class GroupPageFragment extends BaseFragment {

    @BindView(R.id.vp_group_pager)
    ViewPager mVpGroupPager;

    @BindView(R.id.btn_save_photo)
    Button mBtnSavePhont;

    private List<GroupInfo> mGroupList = new ArrayList<>();
    private GroupPagerAdapter mAdapter;
    private ProgressDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_group_page, null);
        ButterKnife.bind(this, view);
        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("正在保存···");
        mDialog.setCanceledOnTouchOutside(false);
        mAdapter = new GroupPagerAdapter(getContext(), mGroupList);
        mVpGroupPager.setAdapter(mAdapter);
        return view;
    }

    public void showViewPager(List<GroupInfo> data, int index) {
        if (data != null && data.size() > 0) {
            mGroupList.addAll(data);
            mAdapter.notifyDataSetChanged();
            mVpGroupPager.setCurrentItem(index);
        }
    }

    @OnClick(R.id.btn_save_photo)
    void onClickSavePhoto() {
        int currentItem = mVpGroupPager.getCurrentItem();
        GroupInfo groupInfo = mGroupList.get(currentItem);
        if (groupInfo.photo != null) {
            File dir = new File(ToolsUtil.getAvailableStoragePath());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            final File target = new File(ToolsUtil.getAvailableStoragePath(), groupInfo.photo.getFilename());
            groupInfo.photo.download(target, new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    if (e == null) {
                        ToolsUtil.saveImageToAlbum(target.getAbsolutePath(), getContext());
                        toast("保存成功");
                    } else {
                        toast("保存失败");
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {
                    mDialog.show();
                }
            });
        }
    }


}
