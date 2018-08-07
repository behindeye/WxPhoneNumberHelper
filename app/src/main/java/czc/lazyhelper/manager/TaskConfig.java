package czc.lazyhelper.manager;

/**
 * 任务配置
 * Created by czc on 2018/8/2.
 */

class TaskConfig {
    private static final TaskConfig ourInstance = new TaskConfig();
    private TaskMode mMode = TaskMode.NEAR_PEOPLE;
    private TaskStatus mStatus = TaskStatus.IDLE;

    static TaskConfig getInstance() {
        return ourInstance;
    }

    private TaskConfig() {
    }

    public void setCurrentTaskMode(TaskMode mode) {
        mMode = mode;
    }

    public TaskMode getCurrentTaskMode() {
        return mMode;
    }

    public void setTaskStatus(TaskStatus status) {
        mStatus = status;
    }

    public TaskStatus getTaskStatus() {
        return mStatus;
    }
}
