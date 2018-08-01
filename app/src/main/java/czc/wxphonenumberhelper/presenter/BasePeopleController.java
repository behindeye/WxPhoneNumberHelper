package czc.wxphonenumberhelper.presenter;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.service.AutoAddPeopleService;
import czc.wxphonenumberhelper.util.PreferenceHelper;

/**
 * Created by czc on 2017/7/29.
 */

public class BasePeopleController implements AddPeopleInterface{

    protected static final String TAG = "czc";

    protected static final String SayHiWithSnsPermissionUI = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
    protected static final String ContactInfoUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    protected static final String WxViewPager = "com.tencent.mm.ui.mogic.WxViewPager";
    protected static final String FMessageConversationUI = "com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI";
    protected static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";
    protected static final String DialogUI = "com.tencent.mm.ui.base.h";
    protected static final String NearbyFriendsUI = "com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI";
    protected static final String SayHiEditUI = "com.tencent.mm.ui.contact.SayHiEditUI";

    protected Object mLock = new Object();
    protected AutoAddPeopleService mService;
    protected ArrayList<String> mSentenceList = new ArrayList<>();
    protected Map<String, Boolean> mRecordMap = Collections.synchronizedMap(new HashMap());

    public BasePeopleController(AutoAddPeopleService service) {
        this.mService = service;
    }

    @Override
    public void initSentenceAndRecord() {
        initSentence();
        initRecord();
    }

    @Override
    public void doTask(AccessibilityEvent event) {

    }

    @Override
    public void saveRecord() {

    }

    @Override
    public void clearRecord() {

    }

    protected void initSentence() {
        String numberJson = PreferenceHelper.getString(Const.PREF_KEY_SENTENCE, "");
        ArrayList<String> list = new Gson().fromJson(numberJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (list != null && list.size() > 0) {
            mSentenceList.addAll(list);
        } else {
            mSentenceList.addAll(Arrays.asList(mService.getResources().getStringArray(R.array.sentence)));
        }
    }

    protected void initRecord() {
        String peopleListJson = PreferenceHelper.getString(Const.PREF_KEY_ADD_PEOPLES_LIST, "");
        Map<String, Boolean> map = new Gson().fromJson(peopleListJson, new TypeToken<Map<String, Boolean>>() {
        }.getType());
        if (map != null && !map.isEmpty()) {
            mRecordMap.putAll(map);
        }
    }

    protected AccessibilityNodeInfo getRoot() {
        return mService.getRoot();
    }
}
