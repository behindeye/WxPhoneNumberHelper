package czc.lazyhelper.view;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by czc on 2018/8/6.
 */

public interface TaskView {

    void performBack();

    AccessibilityNodeInfo getActiveRoot();

}
