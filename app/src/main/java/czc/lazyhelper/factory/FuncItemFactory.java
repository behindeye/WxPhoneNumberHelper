package czc.lazyhelper.factory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import czc.lazyhelper.R;
import czc.lazyhelper.constant.Const;
import czc.lazyhelper.model.FuncItem;
import czc.lazyhelper.util.PreferenceHelper;

/**
 * Created by czc on 2017/7/7.
 */

public class FuncItemFactory {

    private static FuncItemFactory instance;
    public static final String CREAT_ENUMBER = "生成手机好友";
    @Deprecated
    public static final String SHARE_GROUP = "群组分享";
    public static final String QUICK_CONTACTPEOPLE = "加手机好友";
    public static final String ADD_CONTACTPEOPLE = "自动加手机好友";
    public static final String ADD_NEARPEOPLE = "向附近的人打招呼";
    public static final String STOP_ADD_PEOPLE = "停止加好友";
    public static final String FLOW_WINDOW = "显示悬浮窗";
    public static final String VALIDATE = "加人验证信息";
    public static final String FEEDBACK = "意见反馈";
	@Deprecated
    public static final String SOFT_VALIDATE = "软件激活";
    public static final String HELP = "帮助";

    private FuncItemFactory() {

    }

    public static FuncItemFactory getInstance() {
        synchronized (FuncItemFactory.class) {
            if (instance == null) {
                instance = new FuncItemFactory();
            }
        }
        return instance;
    }

    public List<FuncItem> createFuncItems() {
        List<FuncItem> list = new ArrayList<FuncItem>() {
            {
                add(new FuncItem(CREAT_ENUMBER, "手机推荐好友", R.drawable.icon_contact, false));
                add(new FuncItem(QUICK_CONTACTPEOPLE, "自动快速添加微信手机推荐好友", R.drawable.icon_auto, false));
                add(new FuncItem(ADD_CONTACTPEOPLE, "自动添加微信手机推荐好友", R.drawable.icon_auto, false));
                add(new FuncItem(ADD_NEARPEOPLE, "自动向微信附近的人打招呼", R.drawable.icon_near, false));
                add(new FuncItem(STOP_ADD_PEOPLE, "停止所有的加人操作", R.drawable.icon_stop, false));
                add(new FuncItem(FLOW_WINDOW, "显示悬浮窗", R.drawable.icon_flow_window, false));
                add(new FuncItem(VALIDATE, "添加好友时输入的验证信息", R.drawable.icon_validate, false));
//                add(new FuncItem(SOFT_VALIDATE, "激活软件使用", R.drawable.icon_validity, false));
//                add(new FuncItem(SHARE_GROUP, "发布群组信息，认识好友", R.drawable.icon_group, false));
//                add(new FuncItem(FEEDBACK, "有问题，及时反馈给我们", R.drawable.icon_feedback, false));
                add(new FuncItem(HELP, "常见问题", R.drawable.icon_help, false));
            }
        };

        //for server controll
        String funcItemString = PreferenceHelper.getString(Const.PREF_KEY_VALIDATE_FUNC_ITEM_LIST, "");
        List<String> valicateFuncItemList = new Gson().fromJson(funcItemString, new TypeToken<List<String>>() {
        }.getType());
        if (valicateFuncItemList != null) {
            for (String s : valicateFuncItemList) {
                for (FuncItem funcItem : list) {
                    if (funcItem.funcName.equals(s)) {
                        funcItem.isNeedValidate = true;
                    }
                }
            }
        }
        return list;
    }
}
