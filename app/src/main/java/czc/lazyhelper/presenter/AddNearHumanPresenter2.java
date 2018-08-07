package czc.lazyhelper.presenter;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.Random;

import czc.lazyhelper.MyApplication;
import czc.lazyhelper.manager.DBManager;
import czc.lazyhelper.model.SentenceModel;
import czc.lazyhelper.model.WechatFriendModel;
import czc.lazyhelper.model.WechatFriendModelDao;
import czc.lazyhelper.service.TaskService;
import czc.lazyhelper.util.NodeUtil;

import static czc.lazyhelper.util.NodeUtil.checkNodeDes;
import static czc.lazyhelper.util.NodeUtil.checkNodeText;
import static czc.lazyhelper.util.NodeUtil.findNodeByIdAndClassName;
import static czc.lazyhelper.util.NodeUtil.findNodeByIdAndClassNameList;
import static czc.lazyhelper.util.NodeUtil.findNodeByIdAndClick;
import static czc.lazyhelper.util.NodeUtil.findNodeByIdAndClick2;
import static czc.lazyhelper.util.NodeUtil.performClick;
import static czc.lazyhelper.util.NodeUtil.performScroll;
import static czc.lazyhelper.util.NodeUtil.traverseNodeByClassList;

/**
 * Created by czc on 2017/7/4.
 * 自动加附近的人
 */

public class AddNearHumanPresenter2 extends BaseTaskPresenter {

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

    public AddNearHumanPresenter2(TaskService service) {
        super(service);
    }

    @Override
    public void doTask(AccessibilityEvent event, AccessibilityNodeInfo root) {
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
        } else if ((mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
                && mClassName.equals(NearbyFriendsUI)
                || mType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && mClassName.equals(LIST_VIEW)
                || mType == AccessibilityEvent.TYPE_VIEW_SCROLLED && mClassName.equals(LIST_VIEW)) {
            peopleListView(root);
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(ContactInfoUI)) {
            clickSayHiBtn(root);
        } else if (mType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && mClassName.equals(SayHiEditUI)) {
//            inputSayHiSentence();
            sayHiToPeople(root);
        }
    }

    private void clickFindBtn() {
        Log.e(TAG, "clickFindBtn");
        findNodeByIdAndClick(getRoot(), findTvId, "发现", true);
    }

    private void clickNearPeopleLayout() {
        Log.i(TAG, "clickNearPeopleLayout");
        findNodeByIdAndClick(getRoot(), nearTvId, "附近的人", true);
    }

    private void clickDialogOkBtn() {
        Log.i(TAG, "clickDialogOkBtn");
        NodeUtil.findNodeByTextAndClick(getRoot(), "确定", true);
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
                    if (!hasAddThisHuman(name)) {
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


    /**
     * 是否添加过这个人
     *
     * @param name
     * @return true 说明添加过了
     */
    private boolean hasAddThisHuman(String name) {
        QueryBuilder<WechatFriendModel> queryBuilder = DBManager.getInstance(MyApplication.getAppContext())
                .getSession()
                .getWechatFriendModelDao()
                .queryBuilder();
        long count = queryBuilder
                .where(WechatFriendModelDao.Properties.Name.eq(name))
                .count();
        return count != 0;
    }

//    private void clickSayHiBtn() {
//        AccessibilityNodeInfo root = getRoot();
//        if (root != null) {
//            synchronized (mLock) {
//                Log.i("czc", "clickSayHiBtn");
//                // 返回按钮、性别icon
//                AccessibilityNodeInfo sexNode = null;
//                List<AccessibilityNodeInfo> imageNodeList = traverseNodeByClassList(root, IMAGE_VIEW);
//                for (AccessibilityNodeInfo imageNode : imageNodeList) {
//                    if (imageNode.getContentDescription().equals("男")) {
//                        sexNode = imageNode;
//                        break;
//                    } else if (imageNode.getContentDescription().equals("女")) {
//                        sexNode = imageNode;
//                        break;
//                    }
//                }
//                //找到昵称
//                String userName = "";
//                if (sexNode != null && sexNode.getParent() != null) {
//                    AccessibilityNodeInfo userNameNode = NodeUtil.findNodeByClass(sexNode.getParent(), TEXT_VIEW);
//                    userName = userNameNode.getText().toString().trim();
//                }
//                if (mRecordMap.containsKey(userName) && mRecordMap.get(userName)) {
//                    Log.i("czc", "click back btn ·····");
//                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    boolean result = findNodeByTextAndClick(root, "打招呼");
//                    mRecordMap.put(userName, result);
//                }
//            }
//        }
//
//    }

    public void clickSayHiBtn(AccessibilityNodeInfo root) {
        //记录相关信息
        mTargetFriend = new WechatFriendModel();
        AccessibilityNodeInfo nameNode = findNodeByIdAndClassName(root, detailNameTvId, TEXT_VIEW);
        if (checkNodeText(nameNode)) {
            String name = nameNode.getText().toString().trim().replace(" ", "");
            Log.e(TAG, nameNode.getText().toString());
            mTargetFriend.setName(name);
        }
        AccessibilityNodeInfo distanceNode = findNodeByIdAndClassName(root, detailDistTvId, TEXT_VIEW);
        if (checkNodeText(distanceNode)) {
            mTargetFriend.setDistance(distanceNode.getText().toString());
        }
        AccessibilityNodeInfo sexNode = findNodeByIdAndClassName(root, detailSexIvId, IMAGE_VIEW);
        if (checkNodeDes(sexNode)) {
            mTargetFriend.setSex(sexNode.getContentDescription().toString().equals("男") ? 0 : 1);
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
                        String area = areaFullStr.split("=")[1].trim();
                        mTargetFriend.setArea(area);
                    }
                    String signatureFullStr = split[1];
                    if (signatureFullStr.contains("=")) {
                        String signature = signatureFullStr.split("=")[1].trim();
                        mTargetFriend.setSignature(signature);
                    }
                }
            }
        }

        Log.e(TAG, mTargetFriend.toString());

        if (!hasAddThisHuman(mTargetFriend.getName())) {
            findNodeByIdAndClick(root, detailSayHiId, "打招呼", true);
        } else {
            mView.performBack();
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

    private void sayHiToPeople(AccessibilityNodeInfo root) {
        AccessibilityNodeInfo editNode = findNodeByIdAndClassName(root, sayHiEtId, EDIT_TEXT);
        if (editNode == null) {
            editNode = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        }
        if (editNode != null) {
            List<SentenceModel> sentenceModels = DBManager.getInstance(MyApplication.getAppContext()).getSession().getSentenceModelDao().loadAll();
            if (sentenceModels != null && sentenceModels.size() > 0) {
                Random random = new Random();
                int nextInt = random.nextInt(sentenceModels.size());
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        sentenceModels.get(nextInt).getSentence());
                editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            } else {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        "你好");
                editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }
        }
        if (mTargetFriend != null) {
            mTargetFriend.setHasAdd(true);
            DBManager.getInstance(MyApplication.getAppContext()).getSession().getWechatFriendModelDao().insertOrReplace(mTargetFriend);
        }
        findNodeByIdAndClick2(root, sayHiSendBtnId, TEXT_VIEW, true);
    }
}
