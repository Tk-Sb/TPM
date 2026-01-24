package GUI;

import classes.ProductionLine;
import mapper.JSONException;

import static GUI.GUI.inventory;

public class RunTask extends Thread {
    @Override
    public void run() {
        while (true) {
            for (ProductionLine pl : inventory.productionLines) {
                for (ProductionLine.Task task : pl.tasks) {
                    if (task.progress >= 100) {
                        task.progress = 100;
                        task.state = "completed";
                    } else if (task.state.equals("ongoing")) {
                        task.progress += 10;
                        System.out.println(task.productName + " : " + task.progress);

                        try {
                            inventory.saveProductionLines();
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
