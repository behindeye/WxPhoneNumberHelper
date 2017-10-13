package czc.wxphonenumberhelper.util;

import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by czc on 2017/7/3.
 */

public class NodeUtil {
    private static final String TAG = "Node";
    private static final int millis = 1000;

    public static boolean performClick(AccessibilityNodeInfo clickNode) {
        while (clickNode != null && !clickNode.isClickable()) {
            clickNode = clickNode.getParent();
        }
        if (clickNode != null) {
            boolean result = false;
            try {
                //wait some times
                result = clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Thread.sleep(millis);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        Log.e(TAG, "clickNode is null");
        return false;
    }

    public static boolean performScroller(AccessibilityNodeInfo scrollerNode) {
        while (scrollerNode != null && !scrollerNode.isScrollable()) {
            scrollerNode = scrollerNode.getParent();
        }
        if (scrollerNode != null) {
            boolean result = false;
            try {
                result = scrollerNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                //wait some times
                Thread.sleep(millis);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        Log.e(TAG, "scrollerNode is null");
        return false;
    }

    public static boolean findNodeByTextAndPerformClick(AccessibilityNodeInfo root, String text) {
        if (root == null)
            return false;
        List<AccessibilityNodeInfo> txtNodeInfoList = root.findAccessibilityNodeInfosByText(text);

        if (txtNodeInfoList == null || txtNodeInfoList.isEmpty()) {
            Log.e(TAG, "没有找到" + text + "按钮");
            return false;
        }
        AccessibilityNodeInfo clickNode = null;
        for (AccessibilityNodeInfo nodeInfo : txtNodeInfoList) {
            if (nodeInfo.getText() != null && text.equals(nodeInfo.getText().toString())) {
                clickNode = nodeInfo;
                Log.i(TAG, "text= " + nodeInfo.getText());
            }
        }

        while (clickNode != null && !clickNode.isClickable()) {
            clickNode = clickNode.getParent();
        }
        if (clickNode != null) {
            boolean result = false;
            try {
                //wait some times
                result = clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Thread.sleep(millis);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        Log.e(TAG, "clickNode is null");
        return false;
    }

    public static boolean findNodeByIdAndPerformClick(AccessibilityNodeInfo root, String id) {
        List<AccessibilityNodeInfo> idNodeInfoList = root.findAccessibilityNodeInfosByViewId(id);
        if (idNodeInfoList == null || idNodeInfoList.isEmpty()) {
            return false;
        }
        //根据id来找的话，基本是找到list第一个就是目标node
        AccessibilityNodeInfo clickNode = null;

        for (AccessibilityNodeInfo info : idNodeInfoList) {
            if (info.getText() != null) {
                Log.i(TAG, "该id = " + info.getText());
            }
            if (info.getClassName() != null) {
                if (info.getClassName().toString().equals("android.widget.TextView")) {
                    clickNode = info;
                }
            }
        }
        if (clickNode == null) {
            clickNode = idNodeInfoList.get(0);
        }
        while (clickNode != null && !clickNode.isClickable()) {
            clickNode = clickNode.getParent();
        }
        if (clickNode != null) {
            boolean result = false;
            try {
                //wait some times
                result = clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Thread.sleep(millis);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        Log.e(TAG, "clickNode is null");
        return false;
    }

    public static AccessibilityNodeInfo findNodeByClass(AccessibilityNodeInfo root, String className) {
        if (TextUtils.isEmpty(className) || root == null) {
            return null;
        }
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo rootChild = root.getChild(i);
            if (rootChild != null) {
                if (className.equals(rootChild.getClassName().toString().trim())) {
                    return rootChild;
                }
            }
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findNodeByClassList(AccessibilityNodeInfo root, String className) {
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        if (TextUtils.isEmpty(className) || root == null) {
            return Collections.EMPTY_LIST;
        }
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo rootChild = root.getChild(i);
            if (rootChild != null) {
                if (className.equals(rootChild.getClassName().toString().trim())) {
                    list.add(rootChild);
                }
            }
        }
        return list;
    }

    public static List<AccessibilityNodeInfo> traverseNodeByClassList(AccessibilityNodeInfo root, String className) {
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        if (TextUtils.isEmpty(className) || root == null) {
            return Collections.EMPTY_LIST;
        }
        List<AccessibilityNodeInfo> list2 = new ArrayList<>();
        traverseNodeClassToList(root, list2);
        for (AccessibilityNodeInfo nodeInfo : list2) {
            if (nodeInfo.getClassName() != null && className.equals(nodeInfo.getClassName().toString())) {
                list.add(nodeInfo);
            }
        }
        return list;
    }

    public static void traverseNodeClassToList(AccessibilityNodeInfo node, List<AccessibilityNodeInfo> list) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                list.add(child);
                if (child.getChildCount() > 0) {
                    traverseNodeClassToList(child, list);
                }
            }
        }
    }

    public static AccessibilityNodeInfo findNodeByClassNameAndTime(AccessibilityNodeInfo node, String className, int findClassTimes) {
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        traverseNodeClassToList(node, list);
        for (AccessibilityNodeInfo nodeInfo : list) {
            if (nodeInfo.getClassName() != null && className.equals(nodeInfo.getClassName().toString())) {
                findClassTimes--;
                if (findClassTimes < 0) {
                    findClassTimes = 0;
                }
                if (findClassTimes == 0)
                    return nodeInfo;
            }
        }
        return null;
    }

}
