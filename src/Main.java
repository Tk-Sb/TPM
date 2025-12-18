import classes.*;
import mapper.JSONException;

public class Main {
    // Single static Inventory instance - accessible from everywhere
    static Inventory inventory;

    public static void main(String[] args) throws JSONException {
        inventory = new Inventory("data/items.json", "data/products.json");

        inventory.loadAll();

        System.out.println("=== APPLICATION STARTED ===");
        inventory.printSummary();

        // Example 1: Print required items for product with ID 0
        System.out.println("--- Example 1: Product requirements ---");
        for (Product product : inventory.products) {
            if (product.id == 0) {
                System.out.println("Required items for product '" + product.name + "' (ID: " + product.id + "):");
                for (Product.RequiredItem requiredItem : product.requiredItems) {
                    System.out.println("  - " + requiredItem.item + " (Qty: " + requiredItem.quantity + ")");
                }
            }
        }

//        // Example 2: Add a new item (automatically saves to file)
//        System.out.println("\n--- Example 2: Adding new item ---");
//        Item newItem = new Item(100, "New Component", "Electronics", 49.99, 50, 10);
//        inventory.addItem(newItem);

//        // Example 3: Create and add a new product (automatically saves to file)
//        System.out.println("\n--- Example 3: Adding new product ---");
//        Product newProduct = new Product(5, "New Gadget");
//        newProduct.requiredItems.add(new Product.RequiredItem("Battery", 2));
//        newProduct.requiredItems.add(new Product.RequiredItem("Screen", 1));
//        inventory.addProduct(newProduct);
//
//        // Example 4: Find item by ID
//        System.out.println("\n--- Example 4: Finding item by ID ---");
//        Item foundItem = inventory.getItemById(1);
//        if (foundItem != null) {
//            System.out.println("Found item: " + foundItem.name + " (Price: $" + foundItem.price + ")");
//        }
//
//        // Example 5: Update an existing item
//        System.out.println("\n--- Example 5: Updating item ---");
//        Item updatedItem = new Item(100, "Updated Component", "Electronics", 59.99, 60, 15);
//        inventory.updateItem(updatedItem);
//
//        System.out.println("\n=== APPLICATION FINISHED ===");
//        inventory.printSummary();
    }
}