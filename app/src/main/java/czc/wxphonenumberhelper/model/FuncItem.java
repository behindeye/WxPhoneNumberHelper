package czc.wxphonenumberhelper.model;

/**
 * 功能item
 */

public class FuncItem {
    public String funcName;
    public String subFuncName;
    public int resId;
    public boolean isNeedValidate;//是否需要验证

    public FuncItem(String funcName,String subFuncName, int resId,boolean isNeedValidate) {
        this.funcName = funcName;
        this.subFuncName = subFuncName;
        this.resId = resId;
        this.isNeedValidate = isNeedValidate;
    }
}
