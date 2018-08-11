package czc.lazyhelper.model.task;

/**
 * Created by czc on 2018/8/10.
 */
public class TaskNode {

    public static final String CLICK = "click";
    public static final String SCRLLOR = "scrllor";

    private String id;
    private int index;
    private String className;
    private String text;
    private String action;
    private boolean isNewPage;
    private int step;
    private String key;

    public TaskNode(String key, String id, String className, String text) {
        this.key = key;
        this.id = id;
        this.className = className;
        this.text = text;
    }

    public TaskNode(String key, String id, String className, String text, String action) {
        this.key = key;
        this.id = id;
        this.className = className;
        this.text = text;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isNewPage() {
        return isNewPage;
    }

    public void setNewPage(boolean newPage) {
        isNewPage = newPage;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "TaskNode{" +
                "id='" + id + '\'' +
                ", index=" + index +
                ", className='" + className + '\'' +
                ", text='" + text + '\'' +
                ", action='" + action + '\'' +
                ", isNewPage=" + isNewPage +
                ", step=" + step +
                ", key='" + key + '\'' +
                '}';
    }
}
