package czc.lazyhelper.base;

/**
 * @author zhicheng.chen
 * @date 2018/7/25
 */
public abstract class BaseEvent {
    public int type;

    public BaseEvent(){
        setType();
    }

    public void setType(){
        type = getType();
    }

    /**
     * 必须实现，根据type去区分event
     * @return
     */
    protected abstract int getType();
}
