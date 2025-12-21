package classes;

import mapper.JSONException;
import mapper.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final ObjectMapper mapper = new ObjectMapper();
    public List<Item> items = new ArrayList<>();
    public List<Product> products = new ArrayList<>();
    private final String itemsFilePath;
    private final String productsFilePath;
    public int productMaxId;
    public int itemMaxId;

    // Constructor with file paths
    public Inventory(String itemsFilePath, String productsFilePath) {
        this.itemsFilePath = itemsFilePath;
        this.productsFilePath = productsFilePath;
    }

    // Initialize items from JSON file
    public void loadItems() throws JSONException {
        File file = new File(itemsFilePath);
        if (file.exists()) {
            items = mapper.readValueAsList(file, Item.class);
            System.out.println("Loaded " + items.size() + " items from: " + itemsFilePath);
            itemMaxId = findItemsMaxId();
        } else {
            System.out.println("Items file not found: " + itemsFilePath + ". Starting with empty inventory.");
            items = new ArrayList<>();
        }
    }

    // Initialize products from JSON file
    public void loadProducts() throws JSONException {
        File file = new File(productsFilePath);
        if (file.exists()) {
            products = mapper.readValueAsList(file, Product.class);
            System.out.println("Loaded " + products.size() + " products from: " + productsFilePath);
            productMaxId = findProductsMaxId();
        } else {
            System.out.println("Products file not found: " + productsFilePath + ". Starting with empty inventory.");
            products = new ArrayList<>();
        }
    }

    // Load both items and products
    public void loadAll() throws JSONException {
        loadItems();
        loadProducts();
    }

    // Add item and save to file
    public void addItem(Item item) throws JSONException {
        items.add(item);
        saveItems();
        System.out.println("Item added: " + item.name);
        printItems();
    }

    // Remove item and save to file
    public void removeItem(Item item) throws JSONException {
        if (items.remove(item)) {
            saveItems();
            System.out.println("Item removed: " + item.name);
            printItems();
        }
    }

    // Add product and save to file
    public void addProduct(Product product) throws JSONException {
        products.add(product);
        saveProducts();
        System.out.println("Product added: " + product.name);
        printProducts();
    }

    // Remove product and save to file
    public void removeProduct(Product product) throws JSONException {
        if (products.remove(product)) {
            saveProducts();
            System.out.println("Product removed: " + product.name);
            printProducts();
        }
    }

    // Save items to file
    public void saveItems() throws JSONException {
        mapper.writeValue(new File(itemsFilePath), items);
        System.out.println("Items saved to: " + itemsFilePath);
    }

    // Save products to file
    public void saveProducts() throws JSONException {
        mapper.writeValue(new File(productsFilePath), products);
        System.out.println("Products saved to: " + productsFilePath);
    }

    // Save everything
    public void saveAll() throws JSONException {
        saveItems();
        saveProducts();
    }

    // Find item by ID
    public Item findItemById(int id) {
        for (Item item : items) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    // Find item by name
    public Item findItemByName(String name) {
        for (Item item : items) {
            if (item.name != null && item.name.equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    // Find items by category
    public List<Item> findItemsByCategory(String category) {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.category != null && item.category.equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    // Find product by ID
    public Product findProductById(int id) {
        for (Product product : products) {
            if (product.id == id) {
                return product;
            }
        }
        return null;
    }

    // Find product by name
    public Product findProductByName(String name) {
        for (Product product : products) {
            if (product.name != null && product.name.equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    // Print all items
    public void printItems() {
        System.out.println("\n=== ITEMS INVENTORY (" + items.size() + " items) ===");
        for (Item item : items) {
            System.out.printf("ID: %d, Name: %s, Category: %s, Price: $%.2f, Stock: %d, Min Stock: %d%n",
                    item.id, item.name, item.category, item.price, item.stock, item.minimumStock);
        }
        System.out.println("===================================\n");
    }

    // Print all products
    public void printProducts() {
        System.out.println("\n=== PRODUCTS INVENTORY (" + products.size() + " products) ===");
        for (Product product : products) {
            System.out.printf("ID: %d, Name: %s, Required Items: %d%n",
                    product.id, product.name, product.requiredItems.size());
            for (Product.RequiredItem requiredItem : product.requiredItems) {
                System.out.printf("  - %s (Quantity: %d)%n", requiredItem.item, requiredItem.quantity);
            }
        }
        System.out.println("=======================================\n");
    }

    // Print inventory summary
    public void printSummary() {
        System.out.println("\n=== INVENTORY SUMMARY ===");
        System.out.println("Items: " + items.size());
        System.out.println("Products: " + products.size());
        System.out.println("Items File: " + itemsFilePath);
        System.out.println("Products File: " + productsFilePath);
        System.out.println("=========================\n");
    }

    // Check if an item ID already exists
    public boolean itemIdExists(int id) {
        return findItemById(id) != null;
    }

    // Check if a product ID already exists
    public boolean productIdExists(int id) {
        return findProductById(id) != null;
    }

    // Update an existing item
    public void updateItem(Item updatedItem) throws JSONException {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).id == updatedItem.id) {
                items.set(i, updatedItem);
                saveItems();
                System.out.println("Item updated: " + updatedItem.name);
                return;
            }
        }
        System.out.println("Item with ID " + updatedItem.id + " not found for update.");
    }

    // Update an existing product
    public void updateProduct(Product updatedProduct) throws JSONException {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).id == updatedProduct.id) {
                products.set(i, updatedProduct);
                saveProducts();
                System.out.println("Product updated: " + updatedProduct.name);
                return;
            }
        }
        System.out.println("Product with ID " + updatedProduct.id + " not found for update.");
    }

    // Find the max id value for auto generating new ids
    private int findProductsMaxId() {
        return products.stream()
                .mapToInt(Product::getId)
                .max()
                .orElse(0); // Default value if list is empty
    }

    // Find the max id value for auto generating new ids
    private int findItemsMaxId() {
        return items.stream()
                .mapToInt(Item::getId)
                .max()
                .orElse(0); // Default value if list is empty
    }

    // Check for required items to produce a product
    public boolean canProduce(Product product, int quantity) {
        for (Product.RequiredItem requiredItem : findProductByName(product.name).requiredItems) {
            if ((requiredItem.quantity * quantity) > findItemByName(requiredItem.item).stock) {
                System.out.println("not enough " + requiredItem.item);
                return false;
            }
        }
        return true;
    }
    public boolean canProduce(String productName, int quantity) {

        for (Product.RequiredItem requiredItem : findProductByName(productName).requiredItems) {
            System.out.println(requiredItem.item + ": " + requiredItem.quantity);
            if ((requiredItem.quantity * quantity) > findItemByName(requiredItem.item).stock) {
                System.out.println("not enough " + requiredItem.item);
                return false;
            }
        }
        return true;
    }
}