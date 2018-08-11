package czc.lazyhelper.factory;

import android.util.Log;

import com.google.gson.Gson;

import czc.lazyhelper.model.task.MultiTask;
import czc.lazyhelper.model.task.Task;
import czc.lazyhelper.model.task.TaskNode;
import czc.lazyhelper.model.task.TaskPage;

/**
 * Created by czc on 2018/8/9.
 */

public class TaskJsonFactory {
    //
    public static final String LIST_VIEW = "android.widget.ListView";
    public static final String TEXT_VIEW = "android.widget.TextView";
    public static final String IMAGE_VIEW = "android.widget.ImageView";
    public static final String EDIT_TEXT = "android.widget.EditText";
    public static final String RELATIVE_LAYOUT = "android.widget.RelativeLayout";
    public static final String LINEAR_LAYOUT = "android.widget.LinearLayout";

    //page
    protected static final String SayHiWithSnsPermissionUI = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
    protected static final String ContactInfoUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    protected static final String WxViewPager = "com.tencent.mm.ui.mogic.WxViewPager";
    protected static final String FMessageConversationUI = "com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI";
    protected static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";
    protected static final String DialogUI = "com.tencent.mm.ui.base.h";
    protected static final String NearbyFriendsUI = "com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI";
    protected static final String SayHiEditUI = "com.tencent.mm.ui.contact.SayHiEditUI";

    public TaskJsonFactory() {

    }

    public static TaskJsonFactory create() {
        return new TaskJsonFactory();
    }

    public void createMultiTask(){
        Task taskV667 = createTaskV667();
        MultiTask task = new MultiTask();
        task.addTask(taskV667);

        Log.e("czc",new Gson().toJson(task));
    }

    public Task createTaskV667() {
        TaskNode node1 = new TaskNode("find", "com.tencent.mm:id/cdj", TEXT_VIEW, "发现", TaskNode.CLICK);
        TaskNode node2 = new TaskNode("nearHuman", "android:id/title", TEXT_VIEW, "附近的人", TaskNode.CLICK);
        TaskNode node3 = new TaskNode("nearHumanList", "com.tencent.mm:id/cjc", LIST_VIEW, "附近的人列表", TaskNode.SCRLLOR);
        TaskNode node4 = new TaskNode("nearHumanListName", "com.tencent.mm:id/b0p", TEXT_VIEW, "列表名字");
        TaskNode node5 = new TaskNode("nearHumanListSex", "com.tencent.mm:id/cjh", IMAGE_VIEW, "列表性别");
        TaskNode node6 = new TaskNode("detailName", "com.tencent.mm:id/qj", TEXT_VIEW, "详情名字");
        TaskNode node7 = new TaskNode("detailSex", "com.tencent.mm:id/apl", IMAGE_VIEW, "详情性别");
        TaskNode node8 = new TaskNode("detailDist", "com.tencent.mm:id/ap7", TEXT_VIEW, "详情距离");
        TaskNode node9 = new TaskNode("detailAreaAndAignParentId", "com.tencent.mm:id/jz", LINEAR_LAYOUT, "详情地区与签名");
        TaskNode node10 = new TaskNode("detailSayHi", "com.tencent.mm:id/aox", TEXT_VIEW, "打招呼");
        TaskNode node11 = new TaskNode("detailSendMsg", "com.tencent.mm:id/ap1", TEXT_VIEW, "发消息");
        TaskNode node12 = new TaskNode("sayHiEt", "com.tencent.mm:id/d4h", EDIT_TEXT, "打招呼输入框");
        TaskNode node13 = new TaskNode("sayHiSend", "com.tencent.mm:id/hg", TEXT_VIEW, "发送");

        TaskPage page1 = new TaskPage("LauncherUI", LauncherUI);
        TaskPage page2 = new TaskPage("ContactInfoUI", ContactInfoUI);
        TaskPage page3 = new TaskPage("SayHiWithSnsPermissionUI", SayHiWithSnsPermissionUI);
        TaskPage page4 = new TaskPage("WxViewPager", WxViewPager);
        TaskPage page5 = new TaskPage("FMessageConversationUI", FMessageConversationUI);
        TaskPage page6 = new TaskPage("DialogUI", DialogUI);
        TaskPage page7 = new TaskPage("NearbyFriendsUI", NearbyFriendsUI);
        TaskPage page8 = new TaskPage("SayHiEditUI", SayHiEditUI);

        Task task = new Task();
        task.setVersion("6.6.7");
        task.setTaskName("添加微信附近的人");
        task.add(node1);
        task.add(node2);
        task.add(node3);
        task.add(node4);
        task.add(node5);
        task.add(node6);
        task.add(node7);
        task.add(node8);
        task.add(node9);
        task.add(node10);
        task.add(node11);
        task.add(node12);
        task.add(node13);

        task.add(page1);
        task.add(page2);
        task.add(page3);
        task.add(page4);
        task.add(page5);
        task.add(page6);
        task.add(page7);
        task.add(page8);

        return task;
    }
}
