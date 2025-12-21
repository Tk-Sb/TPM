package classes;

import java.util.Date;

public class Task {
    public int id;
    public Product product;
    public int quantity;
    public String customer;
    public String startingDate;
    public String finishingDate;
    public String state; // ongoing - completed - canceled
    public ProductionLine productionLine;
    public double completedPercentage;

    public Task (Product product, int quantity, String customer, String startingDate, String finishingDate, ProductionLine productionLine, boolean canProduce) {
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
        this.productionLine = productionLine;
    }

    public void printState () {
        System.out.println(state);
    }
}
