package czc.lazyhelper.event;


import czc.lazyhelper.base.BaseEvent;

/**
 * @author zhicheng.chen
 * @date 2018/7/25
 */
public abstract class EasyEvent<T> extends BaseEvent {
    public T value;
}
