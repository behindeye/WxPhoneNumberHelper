package czc.lazyhelper.manager;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import czc.lazyhelper.model.task.MultiTask;
import czc.lazyhelper.model.task.Task;
import czc.lazyhelper.model.task.TaskNode;
import czc.lazyhelper.model.task.TaskPage;
import czc.lazyhelper.util.MyAppUtil;

/**
 * 解析配置文件
 * Created by czc on 2018/8/11.
 */
public class ParseManager {

    private static ParseManager Instance;

    private boolean mHasConfigWxSet;

    private Map<String, TaskNode> mNodesMap;
    private Map<String, TaskPage> mPagesMap;
    private Map<String, Task> mTasksMap;

    public static ParseManager getInstance() {
        if (Instance == null) {
            synchronized (ParseManager.class) {
                if (Instance == null) {
                    Instance = new ParseManager();
                }
            }
        }
        return Instance;
    }

    private ParseManager() {
        mTasksMap = new HashMap<>();
        mNodesMap = new HashMap<>();
        mPagesMap = new HashMap<>();
    }

    public void readTaskConfig(){
        String wechatVersion = MyAppUtil.getWechatVersion();
        LogUtils.d(wechatVersion);
        readConfigByWxVersion(wechatVersion);
    }

    public void readConfigByWxVersion(String version) {
        String configDir = PathManager.getInstance().getConfigDir();
        File config = null;
        List<File> files = FileUtils.listFilesInDir(configDir);
        if (files != null && files.size() > 0) {
            for (File file : files) {
                if (file.getName().contains(version)) {
                    config = file;
                    break;
                }
            }
        }
        if (config != null) {
            mHasConfigWxSet = true;
            String jsonConfig = FileIOUtils.readFile2String(config);
            MultiTask multiTask = new Gson().fromJson(jsonConfig, MultiTask.class);
            if (multiTask != null) {
                List<Task> tasks = multiTask.getTasks();
                for (Task task : tasks) {
                    mTasksMap.put(task.getVersion(), task);
                    List<TaskNode> nodes = task.getNodes();
                    for (TaskNode node : nodes) {
                        mNodesMap.put(node.getKey(), node);
                    }
                    List<TaskPage> pages = task.getPages();
                    for (TaskPage page : pages) {
                        mPagesMap.put(page.getKey(), page);
                    }
                }
            }
        }else {
            mHasConfigWxSet = false;
        }
    }

    public Map<String, Task> getTasksMap() {
        return mTasksMap;
    }

    public Map<String, TaskPage> getPagesMap() {
        return mPagesMap;
    }

    public Map<String, TaskNode> getNodesMap() {
        return mNodesMap;
    }

    public boolean hasConfigWx() {
        return mHasConfigWxSet;
    }
}
