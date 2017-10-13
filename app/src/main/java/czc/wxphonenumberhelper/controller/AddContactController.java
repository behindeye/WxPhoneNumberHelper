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

import static czc.wxphonenumberhelper.constant.Const.LIST_VIEW;
import static czc.wxphonenumberhelper.constant.Const.TEXT_VIEW;
import static czc.wxphonenumberhelper.util.NodeUtil.findNodeByTextAndPerformClick;
import static czc.wxphonenumberhelper.util.NodeUtil.traverseNodeByClassList;

/**
 * Created by czc on 2017/7/3.
 * 自动加通讯录的人
 */

public class AddContactController extends BasePeopleController {

    private int mType;
    private String mClassName;

    public AddContactController(AutoAddPeopleService service) {
        super(service);
    }

    @Override
    public void doTask(AccessibilityEvent event) {
        mType = event.getEventType();
        mClassName = (String) event.getClassName();
        if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(LauncherUI)) {
            clickTxlBtn();
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && mClassName.equals(LIST_VIEW)
                || mType == AccessibilityEvent.TYPE_VIEW_SCROLLED && mClassName.equals(WxViewPager)) {
            clickNewFriend();
        } else if (mType == AccessibilityEvent.TYPE_VIEW_SCROLLED && mClassName.equals(LIST_VIEW)
                || mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(FMessageConversationUI)) {
            beginAddPeople();
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(ContactInfoUI)
                || mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && mClassName.equals(TEXT_VIEW)) {
            peoPleInfoUI();
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(SayHiWithSnsPermissionUI)) {
            sayHiToPeoPle();
        }
    }

    private void clickTxlBtn() {
        findNodeByTextAndPerformClick(getRoot(), "通讯录");
    }

    private void clickNewFriend() {
        boolean result = findNodeByTextAndPerformClick(getRoot(), "新的朋友");
        if (!result) {
            findNodeByTextAndPerformClick(getRoot(), "朋友推荐");
        }
    }

    private void beginAddPeople() {
        AccessibilityNodeInfo scrllorNodeInfo = null;
        AccessibilityNodeInfo root = getRoot();
        if (root != null) {
            scrllorNodeInfo = NodeUtil.findNodeByClassNameAndTime(root, LIST_VIEW, 1);
            if (scrllorNodeInfo == null) {
                Log.i("czc", "scrllorNode is null ");
            }

            List<AccessibilityNodeInfo> nodeByClassList = NodeUtil.findNodeByClassList(scrllorNodeInfo, Const.RELATIVE_LAYOUT);
            for (int i = 0; i < nodeByClassList.size(); i++) {
                AccessibilityNodeInfo nodeInfo = nodeByClassList.get(i);
                if (nodeInfo != null && nodeInfo.getChildCount() >= 3) {
                    AccessibilityNodeInfo userNameNode = nodeInfo.getChild(0);
                    AccessibilityNodeInfo waitingValNode = nodeInfo.getChild(2);

                    Log.i("czc", userNameNode.getText().toString());
                    Log.i("czc", waitingValNode.getText().toString());

                    if (!mRecordMap.containsKey(userNameNode.getText().toString()) && !waitingValNode.getText().toString().equals("等待验证")) {
                        AccessibilityNodeInfo clickNode = userNameNode;
                        while (clickNode != null && !clickNode.isClickable()) {
                            clickNode = clickNode.getParent();
                        }
                        if (clickNode != null) {
                            Log.i(TAG, "click");
                            NodeUtil.performClick(clickNode);
                            break;
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

    private void peoPleInfoUI() {
        AccessibilityNodeInfo root = getRoot();
        if (root != null) {
            //如果可以发消息，说明已经加为好友
            List<AccessibilityNodeInfo> sendBtnNodeList = root.findAccessibilityNodeInfosByText("发消息");
            if (sendBtnNodeList.size() > 0) {
                Log.e(TAG, "has add people,click back button");
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }

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
                boolean result = findNodeByTextAndPerformClick(root, "添加到通讯录");
                mRecordMap.put(userName, result);
            }
        }
    }

    private void sayHiToPeoPle() {
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
        PreferenceHelper.putString(Const.PREF_KEY_ADD_PEOPLES_LIST, new Gson().toJson(mRecordMap));
    }

    @Override
    public void clearRecord() {
        Log.i(TAG, "clearRecord");
        mRecordMap.clear();
    }
}
