import classes.*;
import mapper.JSONException;

public class Main {
    // Single static Inventory instance - accessible from everywhere
    static Inventory inventory;

    public static void main(String[] args) throws JSONException {
        inventory = new Inventory("data/items.json", "data/products.json");

        try {
            inventory.loadAll();
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("=== APPLICATION STARTED ===");

        ProductionLine productionLine1 = new ProductionLine("pans production line");

        productionLine1.addTask(new Task(inventory.findProductByName("pan"), 2, "pep", "21/12/2026", "21/1/2027", productionLine1, inventory.canProduce(inventory.findProductByName("pan"), 2)));

        for (Task task : productionLine1.tasks) {
            task.printState();
        }

    }
}