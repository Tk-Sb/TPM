package GUI;

import GUI.pages.AdminDashboardPage;
import GUI.pages.ManagerDashboardPage;
import classes.Inventory;
import mapper.JSONException;

public class GUI {
    // Single static Inventory instance accessible from everywhere
    public static Inventory inventory = new Inventory("data/items.json", "data/products.json", "data/production-lines.json");

    public static void main(String[] args) {

        try {
            inventory.loadAll();
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }


        RunTask runTask = new RunTask();
        runTask.start();

        new ManagerDashboardPage();
//        new AdminDashboardPage();
//        new LoginPage();
    }
}
