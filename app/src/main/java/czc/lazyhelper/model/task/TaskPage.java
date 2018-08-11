package czc.lazyhelper.model.task;

/**
 * Created by czc on 2018/8/10.
 */
public class TaskPage {
    private String key;
    private String name;

    public TaskPage(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TaskPage{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
