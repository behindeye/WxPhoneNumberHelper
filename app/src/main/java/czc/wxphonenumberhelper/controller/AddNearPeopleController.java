package czc.wxphonenumberhelper.controller;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;

import java.util.List;
import java.util.Random;

import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.service.AutoAddPeopleService;
import czc.wxphonenumberhelper.util.NodeUtil;
import czc.wxphonenumberhelper.util.PreferenceHelper;

import static czc.wxphonenumberhelper.constant.Const.IMAGE_VIEW;
import static czc.wxphonenumberhelper.constant.Const.LIST_VIEW;
import static czc.wxphonenumberhelper.constant.Const.TEXT_VIEW;
import static czc.wxphonenumberhelper.util.NodeUtil.findNodeByTextAndPerformClick;
import static czc.wxphonenumberhelper.util.NodeUtil.traverseNodeByClassList;

/**
 * Created by czc on 2017/7/4.
 * 自动加附近的人
 */

public class AddNearPeopleController extends BasePeopleController implements AddPeopleInterface {

    private int mType;
    private String mClassName;

    public AddNearPeopleController(AutoAddPeopleService service) {
        super(service);
    }

    @Override
    public void doTask(AccessibilityEvent event) {
        mType = event.getEventType();
        mClassName = (String) event.getClassName();
        if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(LauncherUI)) {
            clickFindBtn();
            try {
                Thread.sleep(1000);
                clickNearPeopleLayout();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(DialogUI)) {
            clickDialogOkBtn();
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(NearbyFriendsUI)
                || mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && mClassName.equals(LIST_VIEW)
                || mType == AccessibilityEvent.TYPE_VIEW_SCROLLED && mClassName.equals(LIST_VIEW)) {
            beginAddPeople();
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(ContactInfoUI)) {
            clickSayHiBtn();
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(SayHiEditUI)) {
            inputSayHiSentence();
        }
    }

    private void clickFindBtn() {
        Log.i("czc", "clickFindBtn");
        findNodeByTextAndPerformClick(getRoot(), "发现");
    }

    private void clickNearPeopleLayout() {
        Log.i("czc", "clickNearPeopleLayout");
        findNodeByTextAndPerformClick(getRoot(), "附近的人");
    }

    private void clickDialogOkBtn() {
        Log.i("czc", "clickDialogOkBtn");
        findNodeByTextAndPerformClick(getRoot(), "确定");
    }

    private void beginAddPeople() {
        AccessibilityNodeInfo scrllorNodeInfo;
        AccessibilityNodeInfo root = getRoot();
        if (root != null) {
            synchronized (mLock) {
                Log.i("czc", "beginAddPeople");
                scrllorNodeInfo = NodeUtil.findNodeByClassNameAndTime(root, LIST_VIEW, 1);
                if (scrllorNodeInfo == null) {
                    Log.i("czc", "scrllorNode is null ");
                }

                List<AccessibilityNodeInfo> nodeByClassList = NodeUtil.findNodeByClassList(scrllorNodeInfo, Const.LINEAR_LAYOUT);
                for (int i = 0; i < nodeByClassList.size(); i++) {
                    AccessibilityNodeInfo nodeInfo = nodeByClassList.get(i);

                    AccessibilityNodeInfo sexNode = null;
                    List<AccessibilityNodeInfo> imageNodeList = NodeUtil.traverseNodeByClassList(nodeInfo, IMAGE_VIEW);
                    for (AccessibilityNodeInfo imageNode : imageNodeList) {
                        if (imageNode.getContentDescription().equals("男")) {
                            sexNode = imageNode;
                            break;
                        } else if (imageNode.getContentDescription().equals("女")) {
                            sexNode = imageNode;
                            break;
                        }
                    }

                    //找到昵称
                    if (sexNode != null && sexNode.getParent() != null) {
                        AccessibilityNodeInfo userNameNodeInfo = NodeUtil.findNodeByClass(sexNode.getParent(), TEXT_VIEW);
                        if (userNameNodeInfo != null) {
                            if (userNameNodeInfo.getText() != null) {
                                Log.i(TAG, "userName = " + userNameNodeInfo.getText().toString());
                                if (!mRecordMap.containsKey(userNameNodeInfo.getText().toString().trim())) {
                                    AccessibilityNodeInfo clickNode = userNameNodeInfo;
                                    while (clickNode != null && !clickNode.isClickable()) {
                                        clickNode = clickNode.getParent();
                                    }
                                    if (clickNode != null) {
                                        NodeUtil.performClick(clickNode);
                                        break;
                                    }
                                }
                            }

                            if (i == nodeByClassList.size() - 1) {
                                if (scrllorNodeInfo != null) {
                                    Log.i(TAG, "scrllor");
                                    NodeUtil.performScroller(scrllorNodeInfo);
                                    break;
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private void clickSayHiBtn() {
        AccessibilityNodeInfo root = getRoot();
        if (root != null) {
            synchronized (mLock) {
                Log.i("czc", "clickSayHiBtn");
                // 返回按钮、性别icon
                AccessibilityNodeInfo sexNode = null;
                List<AccessibilityNodeInfo> imageNodeList = traverseNodeByClassList(root, Const.IMAGE_VIEW);
                for (AccessibilityNodeInfo imageNode : imageNodeList) {
                    if (imageNode.getContentDescription().equals("男")) {
                        sexNode = imageNode;
                        break;
                    } else if (imageNode.getContentDescription().equals("女")) {
                        sexNode = imageNode;
                        break;
                    }
                }
                //找到昵称
                String userName = "";
                if (sexNode != null && sexNode.getParent() != null) {
                    AccessibilityNodeInfo userNameNode = NodeUtil.findNodeByClass(sexNode.getParent(), TEXT_VIEW);
                    userName = userNameNode.getText().toString().trim();
                }
                if (mRecordMap.containsKey(userName) && mRecordMap.get(userName)) {
                    Log.i("czc", "click back btn ·····");
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    boolean result = NodeUtil.findNodeByTextAndPerformClick(root, "打招呼");
                    mRecordMap.put(userName, result);
                }
            }
        }

    }

    private void inputSayHiSentence() {
        Log.i("czc", "inputSayHiSentence");
        //找到当前获取焦点的view
        AccessibilityNodeInfo root = getRoot();
        if (root != null) {
            AccessibilityNodeInfo target = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
            if (target != null && Build.VERSION.SDK_INT >= 21) {
                if (mSentenceList != null && mSentenceList.size() > 0) {
                    Random random = new Random();
                    int nextInt = random.nextInt(mSentenceList.size());
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            mSentenceList.get(nextInt));
                    target.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                }
            }
            NodeUtil.findNodeByTextAndPerformClick(root, "发送");
        }
    }

    @Override
    public void saveRecord() {
        Log.i(TAG, mRecordMap.toString());
        PreferenceHelper.putString(Const.PREF_KEY_ADD_NEAR_PEOPLES_LIST, new Gson().toJson(mRecordMap));
    }

    @Override
    public void clearRecord() {
//        mRecordMap.clear();
    }
}
