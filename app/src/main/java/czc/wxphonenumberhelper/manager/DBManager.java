package czc.wxphonenumberhelper.manager;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import czc.wxphonenumberhelper.model.DaoMaster;
import czc.wxphonenumberhelper.model.DaoSession;

/**
 * Created by czc on 2018/7/16.
 */

public class DBManager {

    private final static String dbName = "wx_helper_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    public static void init(Context context){
        getInstance(context.getApplicationContext());
    }

    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    public DaoSession getSession(){
        Database db = openHelper.getWritableDb();
        DaoMaster dm = new DaoMaster(db);
        return dm.newSession();
    }
}
