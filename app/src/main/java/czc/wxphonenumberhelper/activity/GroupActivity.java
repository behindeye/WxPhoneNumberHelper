package czc.wxphonenumberhelper.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.base.BaseFragment;
import czc.wxphonenumberhelper.fragment.GroupListFragment;
import czc.wxphonenumberhelper.fragment.GroupPageFragment;
import czc.wxphonenumberhelper.fragment.PublishGroupFragment;
import czc.wxphonenumberhelper.model.GroupInfo;

public class GroupActivity extends FragmentActivity implements BaseFragment.Host {
    @BindView(R.id.fl_content)
    FrameLayout mFlContent;

    private PublishGroupFragment publishGroupFragment;
    private GroupListFragment groupListFragment;
    private GroupPageFragment groupPagerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        publishGroupFragment = new PublishGroupFragment();
        groupListFragment = new GroupListFragment();
        groupPagerFragment = new GroupPageFragment();

        ft.add(R.id.fl_content, groupListFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void changeFragment(int fragmentType) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentType == Const.PUBLISH_GROUP_FRAMENT) {
            if (publishGroupFragment.isAdded()) {
                ft.hide(groupListFragment).show(publishGroupFragment);
            } else {
                ft.hide(groupListFragment).add(R.id.fl_content, publishGroupFragment);
            }
            ft.addToBackStack(null);
        } else if (fragmentType == Const.GROUP_LIST_FRAMENT) {
            if (groupListFragment.isAdded()) {
                ft.hide(publishGroupFragment).show(groupListFragment);
            } else {
                ft.hide(publishGroupFragment).add(R.id.fl_content, groupListFragment);
            }
            groupListFragment.refresh();
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void jump2PagerFragment(List<GroupInfo> data, int curIndex) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (groupPagerFragment.isAdded()) {
            ft.hide(groupListFragment).show(groupPagerFragment);
        } else {
            ft.hide(groupListFragment).add(R.id.fl_content, groupPagerFragment);
        }
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
        groupPagerFragment.showViewPager(data, curIndex);
    }
}
