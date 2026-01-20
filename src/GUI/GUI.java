package GUI;

import GUI.pages.AdminDashboardPage;
import GUI.pages.LoginPage;
import classes.Inventory;
import classes.ProductionLine;
import mapper.JSONException;

public class GUI {
    public static void main(String[] args) {
        Inventory inventory = new Inventory("data/items.json", "data/products.json", "data/production-lines.json");

        try {
            inventory.loadAll();
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        new AdminDashboardPage(inventory);
//        new LoginPage();
    }
}
