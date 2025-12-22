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

    public static class Task {
        public int id;
        public Product product;
        public int quantity;
        public String customer;
        public String startingDate;
        public String finishingDate;
        public String state; // ongoing - completed - canceled
        public double progress;

        public Task (Product product, int quantity, String customer, String startingDate, String finishingDate, boolean canProduce) {
            this.id = 0;
            this.product = product;
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

        public void printState () {
            System.out.println(state);
        }
    }

}
