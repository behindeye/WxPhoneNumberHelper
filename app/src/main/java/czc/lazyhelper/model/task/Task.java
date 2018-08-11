package czc.lazyhelper.model.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czc on 2018/8/10.
 */
public class Task {
    private String version;
    private String taskName;

    private List<TaskNode> nodes = new ArrayList<>();
    private List<TaskPage> pages = new ArrayList<>();

    public String getVersion() {
        return version;
    }

    public String getTaskName() {
        return taskName;
    }

    public List<TaskNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TaskNode> nodes) {
        this.nodes = nodes;
    }

    public List<TaskPage> getPages() {
        return pages;
    }

    public void setPages(List<TaskPage> pages) {
        this.pages = pages;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void add(TaskNode node) {
        if (node != null && !nodes.contains(node)) {
            nodes.add(node);
        }
    }

    public void add(TaskPage page) {
        if (page != null && !pages.contains(page)) {
            pages.add(page);
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "version='" + version + '\'' +
                ", taskName='" + taskName + '\'' +
                ", nodes=" + nodes +
                ", pages=" + pages +
                '}';
    }
}
