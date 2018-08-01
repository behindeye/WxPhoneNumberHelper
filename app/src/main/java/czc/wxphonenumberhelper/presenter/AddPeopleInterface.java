package czc.wxphonenumberhelper.presenter;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by czc on 2017/7/3.
 */

public interface AddPeopleInterface {
    /**
     * 初始化加人短语 和 已加记录
     */
    void initSentenceAndRecord();

    /**
     * 执行自动化事件
     * @param event
     */
    void doTask(AccessibilityEvent event);

    /**
     * 保存历史加人数据
     */
    void saveRecord();


    void clearRecord();
}
