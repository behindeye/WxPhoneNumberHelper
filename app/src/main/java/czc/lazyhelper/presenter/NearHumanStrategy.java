package czc.lazyhelper.presenter;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Random;

import czc.lazyhelper.util.NodeUtil;

import static czc.lazyhelper.util.NodeUtil.findNodeByTextAndClick;
import static czc.lazyhelper.util.NodeUtil.traverseNodeByClassList;

/**
 * Created by czc on 2017/7/4.
 * 自动加附近的人
 */

public class NearHumanStrategy extends BaseTaskStrategy {

    @Override
    public void doTask(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        mType = event.getEventType();
        mClassName = (String) event.getClassName();
        Log.e(TAG, event(mType));
        Log.e(TAG, mClassName);
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
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && mClassName.equals(NearbyFriendsUI)
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
        Log.e(TAG, "clickFindBtn");
        findNodeByTextAndClick(getRoot(), "发现",true);
    }

    private void clickNearPeopleLayout() {
        Log.i("czc", "clickNearPeopleLayout");
        findNodeByTextAndClick(getRoot(), "附近的人", true);
    }

    private void clickDialogOkBtn() {
        Log.i("czc", "clickDialogOkBtn");
        findNodeByTextAndClick(getRoot(), "确定");
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

                List<AccessibilityNodeInfo> nodeByClassList = NodeUtil.findNodeByClassList(scrllorNodeInfo, LINEAR_LAYOUT);
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
                                        root.recycle();
                                        NodeUtil.performClick(clickNode);
                                        break;
                                    }
                                }
                            }

                            if (i == nodeByClassList.size() - 1) {
                                if (scrllorNodeInfo != null) {
                                    Log.i(TAG, "scrllor");
                                    NodeUtil.performScroll(scrllorNodeInfo);
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
                List<AccessibilityNodeInfo> imageNodeList = traverseNodeByClassList(root, IMAGE_VIEW);
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
                    performBack();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    boolean result = findNodeByTextAndClick(root, "打招呼");
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
//            NodeUtil.findNodeByTextAndClick(root, "发送");
        }
    }

}
