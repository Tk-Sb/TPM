package GUI;

import classes.Product;
import classes.ProductionLine;
import mapper.JSONException;

import static GUI.GUI.inventory;

public class RunTask extends Thread {
    @Override
    public void run() {
        while (true) {
            for (ProductionLine pl : inventory.productionLines) {
                for (ProductionLine.Task task : pl.tasks) {
                    if (task.progress > 100 && !task.state.equals("completed")) {
                        task.progress = 100;
                        task.state = "completed";


                        try {
                            for (Product.RequiredItem item : inventory.findProductByName(task.productName).requiredItems) {
                                inventory.findItemByName(item.item).stock -= item.quantity * task.quantity;
                            }

                            inventory.saveAll();
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }

                    } else if (task.state.equals("ongoing")) {
                        task.progress += 2;
                        System.out.println(task.productName + " : " + task.progress);

                        try {
                            inventory.saveProductionLines();
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                }
            }
        }
    }
}
