package czc.lazyhelper.manager;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import czc.lazyhelper.model.DaoMaster;
import czc.lazyhelper.model.DaoSession;

/**
 * Created by czc on 2018/7/16.
 */

public class DBManager {

    private final static String dbName = "wx_helper_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    public static void init(Context context) {
        getInstance(context);
    }

    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public DaoSession getSession() {
        Database db = openHelper.getWritableDb();
        if (daoMaster == null) {
            daoMaster = new DaoMaster(db);
        }
        if (daoSession == null) {
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public void close() {
        if (openHelper != null) {
            openHelper.close();
            openHelper = null;
        }
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

}
