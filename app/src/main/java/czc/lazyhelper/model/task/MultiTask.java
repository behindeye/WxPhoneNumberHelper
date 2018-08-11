package czc.lazyhelper.model.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czc on 2018/8/9.
 */

public class MultiTask {

    private List<Task> tasks = new ArrayList<>();

    public void addTask(Task task){
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public String toString() {
        return "MultiTask{" +
                "tasks=" + tasks +
                '}';
    }
}
