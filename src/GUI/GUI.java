package GUI;

import GUI.pages.AdminDashboardPage;
import classes.Inventory;
import mapper.JSONException;

public class GUI {
    public static Inventory inventory = new Inventory("data/items.json", "data/products.json", "data/production-lines.json");
    public static void main(String[] args) {

        try {
            inventory.loadAll();
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        new AdminDashboardPage();
//        new LoginPage();
    }
}
