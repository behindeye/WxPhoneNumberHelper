package czc.lazyhelper.presenter;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Random;

import czc.lazyhelper.util.NodeUtil;

import static czc.lazyhelper.util.NodeUtil.sleep;
import static czc.lazyhelper.util.NodeUtil.traverseNodeByClassList;

/**
 * Created by czc on 2017/7/3.
 * 自动加通讯录的人
 */

public class ContactStrategy extends BaseTaskStrategy {

    @Override
    protected void performTask(AccessibilityEvent event, AccessibilityNodeInfo root) {
        if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(LauncherUI)) {
            clickTxlBtn(root);
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && mClassName.equals(LIST_VIEW)
                || mType == AccessibilityEvent.TYPE_VIEW_SCROLLED && mClassName.equals(WxViewPager)) {
            clickNewFriend(root);
        } else if (mType == AccessibilityEvent.TYPE_VIEW_SCROLLED && mClassName.equals(LIST_VIEW)
                || mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(FMessageConversationUI)) {
            beginAddPeople(root);
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(ContactInfoUI)
                || mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && mClassName.equals(TEXT_VIEW)) {
            peoPleInfoUI(root);
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(SayHiWithSnsPermissionUI)) {
            sayHiToPeoPle(root);
        }
    }

    private void clickTxlBtn(AccessibilityNodeInfo root) {
		NodeUtil.findNodeByTextAndClick(root, "通讯录");
	}

	private void clickNewFriend(AccessibilityNodeInfo root) {
		boolean result = NodeUtil.findNodeByTextAndClick(root, "新的朋友");
		if (!result) {
			NodeUtil.findNodeByTextAndClick(root, "朋友推荐");
		}
	}

	private void beginAddPeople(AccessibilityNodeInfo root) {
		AccessibilityNodeInfo scrllorNodeInfo = null;
		if (root != null) {
			scrllorNodeInfo = NodeUtil.findNodeByClassNameAndTime(root, LIST_VIEW, 1);
			if (scrllorNodeInfo == null) {
				Log.i("czc", "scrllorNode is null ");
			}

			List<AccessibilityNodeInfo> nodeByClassList = NodeUtil.findNodeByClassList(scrllorNodeInfo, RELATIVE_LAYOUT);
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
							NodeUtil.performScroll(scrllorNodeInfo);
							break;
						}
					}
				}

			}
		}
	}

	private void peoPleInfoUI(AccessibilityNodeInfo root) {
		if (root != null) {
			//如果可以发消息，说明已经加为好友
			List<AccessibilityNodeInfo> sendBtnNodeList = root.findAccessibilityNodeInfosByText("发消息");
			if (sendBtnNodeList.size() > 0) {
				Log.e(TAG, "has add people,click back button");
				performBack();
				sleep();
				return;
			}

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
				boolean result = NodeUtil.findNodeByTextAndClick(root, "添加到通讯录");
				mRecordMap.put(userName, result);
			}
		}
	}

	private void sayHiToPeoPle(AccessibilityNodeInfo root) {
		//找到当前获取焦点的view
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
			NodeUtil.findNodeByTextAndClick(root, "发送");
		}
	}
}
