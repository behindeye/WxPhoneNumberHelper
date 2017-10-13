package czc.wxphonenumberhelper.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by czc on 2017/7/9.
 */

public class FileManager extends BmobObject{
    public String fileType;
    public String fileLink;
    public BmobFile file;
}
