package classes;

import java.util.ArrayList;
import java.util.List;

public class ProductionLine {
    public int id;
    public String name;
    public String state; // active - inactive - maintenance
    public String notes;
    public List<Task> tasks = new ArrayList<>();

    public ProductionLine(){}

    public ProductionLine (int id, String name, String state, String notes) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.notes = notes;
        this.tasks = new ArrayList<>();
    }

    public void addTask (Task task) {
        tasks.add(task);
        state = "active";
    }

    public static class Task {
        public int id;
        public String productName;
        public int quantity;
        public String customer;
        public String startingDate;
        public String finishingDate;
        public String state; // ongoing - completed - canceled
        public double progress;

        public  Task(){}

        public Task (String productName, int quantity, String customer, String startingDate, String finishingDate, boolean canProduce) {
            this.id = 0;
            this.productName = productName;
            this.quantity = quantity;
            this.customer = customer;
            this.startingDate = startingDate;
            this.finishingDate = finishingDate;
            if (canProduce) {
                this.state = "ongoing";
            } else {
                this.state = "not enough items to start task";
            }
        }
    }

}
