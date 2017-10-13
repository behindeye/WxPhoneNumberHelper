package czc.wxphonenumberhelper.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by czc on 2017/8/3.
 * 讨论组信息
 */

public class GroupInfo extends BmobObject {
    public String title;
    public String wxNumber;
    public String groupIntroduce;
    public BmobFile photo;
}
