package czc.lazyhelper.event;

/**
 * Created by czc on 2018/7/29.
 */

public class EasyEventBuilder<T>{
    private T value;
    private int mType;

    /**
     * 必须传事件类型
     * @param type
     */
    public EasyEventBuilder (int type){
        this.mType = type;
    }

    public EasyEventBuilder setValue(T value){
        this.value = value;
        return this;
    }

//    public EasyEventBuilder setType(int type){
//        this.mType = type;
//        return this;
//    }

    public EasyEvent build(){
        EasyEvent<T> event = new EasyEvent<T>() {
            @Override
            protected int getType() {
                return mType;
            }
        };
        event.value = this.value;
        return event;
    }
}
