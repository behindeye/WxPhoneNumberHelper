package czc.lazyhelper.manager;

import com.blankj.utilcode.util.FileUtils;

import czc.lazyhelper.MyApplication;

/**
 * 文件路径管理者
 *
 * @author zhicheng.chen
 * @date 2018/7/25
 */
public class PathManager {

    private static PathManager instance;
    private String rootDir;
    private String confDir;
    private String dbDir;

    private PathManager() {
        initDir();
    }

    private void initDir() {
        rootDir = MyApplication.getAppContext().getExternalFilesDir(null).getAbsolutePath();
        confDir = rootDir + "/conf/";
        dbDir = rootDir + "/db/";
        FileUtils.createOrExistsDir(confDir);
        FileUtils.createOrExistsDir(dbDir);
    }

    public String getConfigDir() {
        return confDir;
    }
    public String getConfigZipPath(){
        return getConfigDir()+"task.zip";
    }

    public String getDbDir() {
        return dbDir;
    }

    public String getPhoneDbPath(){
        return getDbDir()+"phone.db";
    }

    public static void init() {
        getInstance();
    }

    public static PathManager getInstance() {
        if (instance == null) {
            synchronized (PathManager.class) {
                if (instance == null) {
                    instance = new PathManager();
                }
            }
        }
        return instance;
    }
}
