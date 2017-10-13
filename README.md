# WxPhoneNumberHelper
> 基于 **AccessibilityService** ，写的**微信自动添加好友**、**自动添加附近的人**；代码集成网上优质的开源库，实现敏捷开发。

# 一、MVP模式开发 #
1.通过 MVP 开发模式，解耦，不过代码量不是特别多，自己学习用。

# 二、Butterknife #
1.极大的方便了开发，减少了大量的 findViewById 的代码，同时在 Android Studio 里面集成了 
Android ButterKnife Zelezny 插件，谁用谁知道，将方便进行到极致。

# 三、Eventbus #
1.对事件的处理，用 Eventbus 来发布与订阅，减少广播的使用。

# 四、RxJava #
1.使用 Rxjava 处理异步事件。

# 五、集成 Bmob 后端云 #
1.集成登录、数据存储、web、版本更新等功能。

# 六、开源控件 与 网络请求库 #
    dependencies {

    compile('com.zhy:okhttputils:2.6.2') {
        exclude group: 'com.squareup.okhttp3'
        exclude group: 'com.squareup.okio'
    }
    compile 'com.baoyz.swipemenulistview:library:1.3.0'
    compile 'com.zhihu.android:matisse:0.4.3'
    compile 'com.squareup.picasso:picasso:2.5.2'
    compile 'com.scwang.smartrefresh:SmartRefreshLayout:1.0.3-alpha-6'
}

# 七、软件截图 #
![](https://raw.githubusercontent.com/behindeye/WxPhoneNumberHelper/master/pic/main.jpg)




