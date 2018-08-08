package czc.lazyhelper.presenter;

import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Random;

import czc.lazyhelper.model.SentenceModel;
import czc.lazyhelper.model.WechatFriendModel;
import czc.lazyhelper.model.WechatFriendModelDao;

import static czc.lazyhelper.util.NodeUtil.checkNodeDes;
import static czc.lazyhelper.util.NodeUtil.checkNodeText;
import static czc.lazyhelper.util.NodeUtil.findNodeByIdAndClassName;
import static czc.lazyhelper.util.NodeUtil.findNodeByIdAndClassNameList;
import static czc.lazyhelper.util.NodeUtil.findNodeByIdTextAndClick;
import static czc.lazyhelper.util.NodeUtil.findNodeByIdClassAndClick;
import static czc.lazyhelper.util.NodeUtil.findNodeByTextAndClick;
import static czc.lazyhelper.util.NodeUtil.performClick;
import static czc.lazyhelper.util.NodeUtil.performScroll;
import static czc.lazyhelper.util.NodeUtil.sleep;
import static czc.lazyhelper.util.NodeUtil.traverseNodeByClassList;

/**
 * Created by czc on 2017/7/4.
 * 自动加附近的人
 */

public class NearHumanStrategy2 extends BaseTaskStrategy {

	//1、点击发现按钮
	public static final String findTvId = "com.tencent.mm:id/cdj";
	//2、点击附近的人按钮
	public static final String nearTvId = "android:id/title";
	//3、附近的人列表
	public static final String nearLvId = "com.tencent.mm:id/cjc";
	//4、附近的人的信息：昵称、性别
	public static final String nameTvId = "com.tencent.mm:id/b0p";
	public static final String sexIvId = "com.tencent.mm:id/cjh";
	//5、附近的人详细信息：昵称，距离，性别，地区，个性签名，到招呼按钮
	public static final String detailNameTvId = "com.tencent.mm:id/qj";
	public static final String detailSexIvId = "com.tencent.mm:id/apl";
	public static final String detailDistTvId = "com.tencent.mm:id/ap7";
	public static final String detailAreaAndAignParentId = "com.tencent.mm:id/jz";
	public static final String detailSayHiId = "com.tencent.mm:id/aox";
	//6、打招呼页面：输入框，发送按钮
	public static final String sayHiEtId = "com.tencent.mm:id/d4h";
	public static final String sayHiSendBtnId = "com.tencent.mm:id/hg";

	private WechatFriendModel mTargetFriend;


	@Override
	public void doTask(AccessibilityEvent event, AccessibilityNodeInfo root) {
		mType = event.getEventType();
		mClassName = (String) event.getClassName();
		Log.e(TAG, event(mType));
		Log.e(TAG, mClassName);
		boolean isStateChanged = mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
		boolean isContentChanged = mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
		if (isStateChanged && mClassName.equals(LauncherUI)) {
			clickFindBtn(root);
			sleep();
			clickNearPeopleLayout(root);
		} else if (isStateChanged && mClassName.equals(DialogUI)) {
			clickDialogOkBtn(root);
		} else if ((isStateChanged
				|| isContentChanged)
				&& mClassName.equals(NearbyFriendsUI)
				|| isContentChanged && mClassName.equals(LIST_VIEW)
				|| mType == AccessibilityEvent.TYPE_VIEW_SCROLLED && mClassName.equals(LIST_VIEW)) {
			peopleListView(root);
		} else if (isStateChanged && mClassName.equals(ContactInfoUI)) {
			clickSayHiBtn(root);
		} else if (isStateChanged && mClassName.equals(SayHiEditUI)) {
			sayHiToPeople(root);
		}
	}

	private void clickFindBtn(AccessibilityNodeInfo root) {
		Log.e(TAG, "clickFindBtn");
		findNodeByIdTextAndClick(root, findTvId, "发现", true);
	}

	private void clickNearPeopleLayout(AccessibilityNodeInfo root) {
		Log.i(TAG, "clickNearPeopleLayout");
		findNodeByIdTextAndClick(root, nearTvId, "附近的人", true);
	}

	private void clickDialogOkBtn(AccessibilityNodeInfo root) {
		Log.i(TAG, "clickDialogOkBtn");
		findNodeByTextAndClick(root, "确定", true);
	}

	private void peopleListView(AccessibilityNodeInfo nodeInfo) {
		AccessibilityNodeInfo scrollerNode = findNodeByIdAndClassName(nodeInfo, nearLvId, LIST_VIEW);
		if (scrollerNode == null) {
			Log.e(TAG, "scroller is null");
		}
		if (scrollerNode != null) {
			final int count = scrollerNode.getChildCount();
			for (int i = 0; i < count; i++) {
				AccessibilityNodeInfo child = scrollerNode.getChild(i);
				AccessibilityNodeInfo nameNode = findNodeByIdAndClassName(child, nameTvId, TEXT_VIEW);
				if (nameNode != null) {
					Log.e(TAG, nameNode.getText().toString() + "==");
					String name = nameNode.getText().toString().trim().replace(" ", "");
					if (!hasAdd(name)) {
						performClick(nameNode);
						break;
					}
				} else {
					Log.e(TAG, "name node is null");
				}
				AccessibilityNodeInfo sex = findNodeByIdAndClassName(child, sexIvId, IMAGE_VIEW);
				if (sex != null) {
					Log.e(TAG, sex.getContentDescription().toString());
				} else {
					Log.e(TAG, "sex node is null");
				}
				if (i == count - 1) {
					if (scrollerNode.isScrollable()) {
						performScroll(scrollerNode);
					}
				}
			}
		}
	}

	private void clickSayHiBtn(AccessibilityNodeInfo root) {
		String name = "", distance = "", area = "", signature = "";
		int sex = 0;
		AccessibilityNodeInfo nameNode = findNodeByIdAndClassName(root, detailNameTvId, TEXT_VIEW);
		if (checkNodeText(nameNode)) {
			name = nameNode.getText().toString().trim().replace(" ", "");
		}
		AccessibilityNodeInfo distanceNode = findNodeByIdAndClassName(root, detailDistTvId, TEXT_VIEW);
		if (checkNodeText(distanceNode)) {
			distance = distanceNode.getText().toString();
		}
		AccessibilityNodeInfo sexNode = findNodeByIdAndClassName(root, detailSexIvId, IMAGE_VIEW);
		if (checkNodeDes(sexNode)) {
			sex = sexNode.getContentDescription().toString().equals("男") ? 0 : 1;
		}

		List<AccessibilityNodeInfo> layoutNode = findNodeByIdAndClassNameList(root, detailAreaAndAignParentId, LINEAR_LAYOUT);
		if (layoutNode != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < layoutNode.size(); i++) {
				AccessibilityNodeInfo child = layoutNode.get(i);
				List<AccessibilityNodeInfo> textNode = traverseNodeByClassList(child, TEXT_VIEW);
				for (int j = 0; j < textNode.size(); j++) {
					AccessibilityNodeInfo tv = textNode.get(j);
					if (checkNodeText(tv)) {
						if (j != textNode.size() - 1) {
							sb.append(tv.getText().toString() + "=");
						} else {
							sb.append(tv.getText().toString() + ",");
						}
					}
				}
			}
			//地区和个性签名
			String areaAndSignature = sb.toString();
			if (areaAndSignature.contains(",")) {
				String[] split = areaAndSignature.split(",");
				if (split.length == 2) {
					String areaFullStr = split[0];
					if (areaFullStr.contains("=")) {
						area = areaFullStr.split("=")[1].trim();
					}
					String signatureFullStr = split[1];
					if (signatureFullStr.contains("=")) {
						signature = signatureFullStr.split("=")[1].trim();
					}
				}
			}
		}
		//记录相关信息
		mTargetFriend = new WechatFriendModel();
		mTargetFriend.setName(name);
		mTargetFriend.setDistance(distance);
		mTargetFriend.setSex(sex);
		mTargetFriend.setArea(area);
		mTargetFriend.setSignature(signature);
		Log.e(TAG, mTargetFriend.toString());

		if (!hasAdd(mTargetFriend.getName())) {
			findNodeByIdTextAndClick(root, detailSayHiId, "打招呼", true);
		} else {
			performBack();
		}
	}


	private void sayHiToPeople(AccessibilityNodeInfo root) {
		AccessibilityNodeInfo editNode = findNodeByIdAndClassName(root, sayHiEtId, EDIT_TEXT);
		if (editNode == null) {
			editNode = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
		}
		if (editNode != null) {
			List<SentenceModel> sentenceModels = getSentenceDao().loadAll();
			String sentence = "你好";
			if (sentenceModels != null && sentenceModels.size() > 0) {
				Random random = new Random();
				int nextInt = random.nextInt(sentenceModels.size());
				sentence = sentenceModels.get(nextInt).getSentence();
			}
			Bundle arguments = new Bundle();
			arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, sentence);
			editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
		}
		if (mTargetFriend != null) {
			mTargetFriend.setHasAdd(true);
			getWxFrendDao().insertOrReplace(mTargetFriend);
		}
		findNodeByIdClassAndClick(root, sayHiSendBtnId, TEXT_VIEW, true);
	}


	/**
	 * 是否添加过这个人
	 *
	 * @param name
	 * @return true 说明添加过了
	 */
	private boolean hasAdd(String name) {
		return getWxFrendDao()
				.queryBuilder()
				.where(WechatFriendModelDao.Properties.Name.eq(name))
				.count() != 0;
	}
}
