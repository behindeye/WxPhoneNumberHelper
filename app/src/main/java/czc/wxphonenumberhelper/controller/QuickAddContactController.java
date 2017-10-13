package czc.wxphonenumberhelper.controller;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import czc.wxphonenumberhelper.service.AutoAddPeopleService;
import czc.wxphonenumberhelper.util.NodeUtil;

import static czc.wxphonenumberhelper.constant.Const.LIST_VIEW;
import static czc.wxphonenumberhelper.util.NodeUtil.findNodeByTextAndPerformClick;

/**
 * Created by czc on 2017/7/29.
 * 快速加好友
 */

public class QuickAddContactController extends BasePeopleController {

    private int mType;
    private String mClassName;

    public QuickAddContactController(AutoAddPeopleService service) {
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
        AccessibilityNodeInfo root = getRoot();
        if (root != null) {
            AccessibilityNodeInfo scrllorNodeInfo = NodeUtil.findNodeByClassNameAndTime(root, LIST_VIEW, 1);
            if (scrllorNodeInfo == null) {
                Log.i("czc", "scrllorNode is null ");
            }

            List<AccessibilityNodeInfo> nodeByClassList = root.findAccessibilityNodeInfosByText("添加");
            if (nodeByClassList != null && !nodeByClassList.isEmpty()) {
                for (int i = 0; i < nodeByClassList.size(); i++) {
                    AccessibilityNodeInfo nodeInfo = nodeByClassList.get(i);
                    if (nodeInfo.getText() != null && nodeInfo.getText().toString().equals("添加")) {
                        NodeUtil.performClick(nodeInfo);
                    }
                }
            } else {
                if (scrllorNodeInfo != null) {
                    Log.i(TAG, "scrllor");
                    NodeUtil.performScroller(scrllorNodeInfo);
                }
            }
        }
    }

}
