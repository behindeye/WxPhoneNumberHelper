package czc.wxphonenumberhelper.view;

import java.util.List;

/**
 * Created by czc on 2018/8/1.
 */

public interface ICenterNumView {
    void begin();
    void success(List<String> data);
    void end();
    void error();
}
