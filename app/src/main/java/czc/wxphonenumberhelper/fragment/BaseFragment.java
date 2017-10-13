package czc.wxphonenumberhelper.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.List;

import czc.wxphonenumberhelper.model.GroupInfo;
import czc.wxphonenumberhelper.util.ToastUtil;

/**
 * Created by czc on 2017/8/3.
 */

public class BaseFragment extends Fragment {

    protected Host mHost;

    public interface Host{
        void changeFragment(int fragmentType);

        void jump2PagerFragment(List<GroupInfo> data,int curIndex);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Host){
            mHost = (Host) context;
        }
    }

    protected void toast(String msg){
        ToastUtil.showToast(getContext(),msg);
    }
}
