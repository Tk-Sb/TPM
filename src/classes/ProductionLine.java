package classes;

import java.util.ArrayList;
import java.util.List;

public class ProductionLine {
    public int id;
    public String name;
    public String state; // active - inactive - maintenance
    public List<Task> tasks = new ArrayList<>();

    public ProductionLine (String name) {
        this.id = 0;
        this.name = name;
        this.state = "inactive";
        this.tasks = new ArrayList<>();
    }

    public void addTask (Task task) {
        tasks.add(task);
        state = "active";
    }
}
