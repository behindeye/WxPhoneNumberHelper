package czc.lazyhelper.view;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by czc on 2018/8/6.
 */

public interface TaskView {

    void performBack();

    void performAction(int action);

    AccessibilityNodeInfo getActiveRoot();

    Context getContext();

}
