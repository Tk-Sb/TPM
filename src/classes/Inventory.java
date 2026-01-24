package classes;

import mapper.JSONException;
import mapper.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String itemsFilePath;
    private final String productsFilePath;
    private final String productLineFilePath;

    // ================ LISTS ================
    public List<Item> items = new ArrayList<>();
    public List<Product> products = new ArrayList<>();
    public List<ProductionLine> productionLines = new ArrayList<>();

    // ================ MAX IDs ================
    public int itemMaxId;
    public int productMaxId;
    public int productionLineMaxId;
    public int taskMaxId;

    // ================ CONSTRUCTOR ================
    public Inventory(String itemsFilePath, String productsFilePath, String productLineFilePath) {
        this.itemsFilePath = itemsFilePath;
        this.productsFilePath = productsFilePath;
        this.productLineFilePath = productLineFilePath;
    }

    // ================ LOAD/SAVE ALL ================
    public void loadAll() throws JSONException {
        loadItems();
        loadProducts();
        loadProductionLines();
    }

    public void saveAll() throws JSONException {
        saveItems();
        saveProducts();
        saveProductionLines();
    }

    // ==================== ITEMS MANAGEMENT ===========================

    // ------------ LOAD/SAVE ------------
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

    public void saveItems() throws JSONException {
        mapper.writeValue(new File(itemsFilePath), items);
        System.out.println("Items saved to: " + itemsFilePath);
    }

    // ------------ CRUD OPERATIONS ------------
    public void addItem(Item item) throws JSONException {
        items.add(item);
        itemMaxId = findItemsMaxId();
        saveItems();
        System.out.println("Item added: " + item.name);
        printItems();
    }

    public void removeItem(Item item) throws JSONException {
        if (items.remove(item)) {
            saveItems();
            System.out.println("Item removed: " + item.name);
            printItems();
        }
    }

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

    // ------------ SEARCH FUNCTIONS ------------
    public Item findItemById(int id) {
        for (Item item : items) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public Item findItemByName(String name) {
        for (Item item : items) {
            if (item.name != null && item.name.equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public List<Item> findItemsByCategory(String category) {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.category != null && item.category.equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    // ------------ VALIDATION & HELPER FUNCTIONS ------------
    public boolean itemIdExists(int id) {
        return findItemById(id) != null;
    }

    private int findItemsMaxId() {
        return items.stream()
                .mapToInt(Item::getId)
                .max()
                .orElse(0);
    }

    // ------------ DISPLAY FUNCTIONS ------------
    public void printItems() {
        System.out.println("\n=== ITEMS INVENTORY (" + items.size() + " items) ===");
        for (Item item : items) {
            System.out.printf("ID: %d, Name: %s, Category: %s, Price: $%.2f, Stock: %d, Min Stock: %d%n",
                    item.id, item.name, item.category, item.price, item.stock, item.minimumStock);
        }
        System.out.println("===================================\n");
    }

    // ==================== PRODUCTS MANAGEMENT ========================

    // ------------ LOAD/SAVE ------------
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

    public void saveProducts() throws JSONException {
        mapper.writeValue(new File(productsFilePath), products);
        System.out.println("Products saved to: " + productsFilePath);
    }

    // ------------ CRUD OPERATIONS ------------
    public void addProduct(Product product) throws JSONException {
        products.add(product);
        saveProducts();
        System.out.println("Product added: " + product.name);
        printProducts();
    }

    public void removeProduct(Product product) throws JSONException {
        if (products.remove(product)) {
            saveProducts();
            System.out.println("Product removed: " + product.name);
            printProducts();
        }
    }

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

    // ------------ SEARCH FUNCTIONS ------------
    public Product findProductById(int id) {
        for (Product product : products) {
            if (product.id == id) {
                return product;
            }
        }
        return null;
    }

    public Product findProductByName(String name) {
        for (Product product : products) {
            if (product.name != null && product.name.equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    // ------------ VALIDATION & HELPER FUNCTIONS ------------
    public boolean productIdExists(int id) {
        return findProductById(id) != null;
    }

    private int findProductsMaxId() {
        return products.stream()
                .mapToInt(Product::getId)
                .max()
                .orElse(0);
    }

    // ------------ PRODUCTION CHECKS ------------
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

    // ------------ DISPLAY FUNCTIONS ------------
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

    // ================= PRODUCTION LINES MANAGEMENT ===================

    // ------------ LOAD/SAVE ------------
    public void loadProductionLines() throws JSONException {
        File file = new File(productLineFilePath);
        if (file.exists()) {
            productionLines = mapper.readValueAsList(file, ProductionLine.class);
            System.out.println("Loaded " + productionLines.size() + " production lines from: " + productLineFilePath);
            productionLineMaxId = findProductionLinesMaxId();
            taskMaxId = findTaskMaxId();
        } else {
            System.out.println("Production lines file not found: " + productLineFilePath + ". Starting with empty inventory.");
            productionLines = new ArrayList<>();
        }
    }

    public void saveProductionLines() throws JSONException {
        mapper.writeValue(new File(productLineFilePath), productionLines);
        System.out.println("Production lines saved to: " + productLineFilePath);
    }

    // ------------ CRUD OPERATIONS ------------
    public void addProductionLine(ProductionLine productionLine) throws JSONException {
        productionLines.add(productionLine);
        saveProductionLines();
        System.out.println("Production line added: " + productionLine.name);
        printProductionLines();
    }

    public void removeProductionLine(ProductionLine productionLine) throws JSONException {
        if (productionLines.remove(productionLine)) {
            saveProductionLines();
            System.out.println("Production line removed: " + productionLine.name);
            printProductionLines();
        }
    }

    public void removeTask(ProductionLine.Task task) throws JSONException {
        for (ProductionLine pl : productionLines) {
            if (pl.tasks.remove(task)) {
                saveProductionLines();
                System.out.println("Production line removed: " + task.productName);
                printProductionLines();
            }
        }
    }

    public void updateProductionLine(ProductionLine updatedLine) throws JSONException {
        for (int i = 0; i < productionLines.size(); i++) {
            if (productionLines.get(i).id == updatedLine.id) {
                productionLines.set(i, updatedLine);
                saveProductionLines();
                System.out.println("Production line updated: " + updatedLine.name);
                return;
            }
        }
        System.out.println("Production line with ID " + updatedLine.id + " not found for update.");
    }

    // ------------ SEARCH FUNCTIONS ------------
    public ProductionLine findProductionLineById(int id) {
        for (ProductionLine line : productionLines) {
            if (line.id == id) {
                return line;
            }
        }
        return null;
    }

    public ProductionLine findProductionLineByName(String name) {
        for (ProductionLine line : productionLines) {
            if (line.name != null && line.name.equalsIgnoreCase(name)) {
                return line;
            }
        }
        return null;
    }

    public ProductionLine.Task findTaskByName(String name) {
        for (ProductionLine pl : productionLines) {
            for (ProductionLine.Task task : pl.tasks) {
                if (task.productName != null && task.productName.equalsIgnoreCase(name)) {
                    return task;
                }
            }
        }
        return null;
    }

    public ProductionLine.Task findTaskById(int id) {
        for (ProductionLine pl : productionLines) {
            for (ProductionLine.Task task : pl.tasks) {
                if (task.id == id) {
                    return task;
                }
            }
        }
        return null;
    }

    public List<ProductionLine> findProductionLinesByState(String state) {
        List<ProductionLine> result = new ArrayList<>();
        for (ProductionLine line : productionLines) {
            if (line.state != null && line.state.equalsIgnoreCase(state)) {
                result.add(line);
            }
        }
        return result;
    }

    public List<ProductionLine> findProductionLinesWithTaskState(String taskState) {
        List<ProductionLine> result = new ArrayList<>();
        for (ProductionLine line : productionLines) {
            for (ProductionLine.Task task : line.tasks) {
                if (task.state != null && task.state.equalsIgnoreCase(taskState)) {
                    result.add(line);
                    break;
                }
            }
        }
        return result;
    }

    // ------------ VALIDATION & HELPER FUNCTIONS ------------
    public boolean productionLineIdExists(int id) {
        return findProductionLineById(id) != null;
    }

    private int findProductionLinesMaxId() {
        return productionLines.stream()
                .mapToInt(line -> line.id)
                .max()
                .orElse(0);
    }

    private int findTaskMaxId() {
        int maxId = 0;
        for (ProductionLine pl : productionLines) {
            maxId = pl.tasks.stream()
                    .mapToInt(task -> task.id)
                    .max()
                    .orElse(0);
        }

        return maxId;
    }

    // ------------ TASK MANAGEMENT ------------
    public void addTaskToProductionLine(int productionLineId, ProductionLine.Task task) throws JSONException {
        ProductionLine line = findProductionLineById(productionLineId);
        if (line != null) {
            line.addTask(task);
            saveProductionLines();
            System.out.println("Task added to production line: " + line.name);
        } else {
            System.out.println("Production line with ID " + productionLineId + " not found.");
        }
    }

    public void removeTaskFromProductionLine(int productionLineId, int taskId) throws JSONException {
        ProductionLine line = findProductionLineById(productionLineId);
        if (line != null) {
            ProductionLine.Task taskToRemove = null;
            for (ProductionLine.Task task : line.tasks) {
                if (task.id == taskId) {
                    taskToRemove = task;
                    break;
                }
            }

            if (taskToRemove != null && line.tasks.remove(taskToRemove)) {
                // Update production line state if no tasks remain
                if (line.tasks.isEmpty()) {
                    line.state = "inactive";
                }
                saveProductionLines();
                System.out.println("Task removed from production line: " + line.name);
            } else {
                System.out.println("Task with ID " + taskId + " not found in production line.");
            }
        } else {
            System.out.println("Production line with ID " + productionLineId + " not found.");
        }
    }

    public void updateTaskState(int productionLineId, int taskId, String newState) throws JSONException {
        ProductionLine line = findProductionLineById(productionLineId);
        if (line != null) {
            for (ProductionLine.Task task : line.tasks) {
                if (task.id == taskId) {
                    task.state = newState;
                    saveProductionLines();
                    System.out.println("Task state updated to: " + newState);
                    return;
                }
            }
            System.out.println("Task with ID " + taskId + " not found in production line.");
        } else {
            System.out.println("Production line with ID " + productionLineId + " not found.");
        }
    }

    public void updateTaskProgress(int productionLineId, int taskId, double progress) throws JSONException {
        ProductionLine line = findProductionLineById(productionLineId);
        if (line != null) {
            for (ProductionLine.Task task : line.tasks) {
                if (task.id == taskId) {
                    task.progress = progress;
                    saveProductionLines();
                    System.out.println("Task progress updated to: " + progress + "%");
                    return;
                }
            }
            System.out.println("Task with ID " + taskId + " not found in production line.");
        } else {
            System.out.println("Production line with ID " + productionLineId + " not found.");
        }
    }

    public void updateProductionLineState(int productionLineId, String newState) throws JSONException {
        ProductionLine line = findProductionLineById(productionLineId);
        if (line != null) {
            line.state = newState;
            saveProductionLines();
            System.out.println("Production line state updated to: " + newState);
        } else {
            System.out.println("Production line with ID " + productionLineId + " not found.");
        }
    }

    // ------------ TASK QUERIES ------------
    public List<ProductionLine.Task> getAllTasks() {
        List<ProductionLine.Task> allTasks = new ArrayList<>();
        for (ProductionLine line : productionLines) {
            allTasks.addAll(line.tasks);
        }
        return allTasks;
    }

    public List<ProductionLine.Task> getTasksByState(String state) {
        List<ProductionLine.Task> result = new ArrayList<>();
        for (ProductionLine line : productionLines) {
            for (ProductionLine.Task task : line.tasks) {
                if (task.state != null && task.state.equalsIgnoreCase(state)) {
                    result.add(task);
                }
            }
        }
        return result;
    }

    public List<ProductionLine.Task> getTasksByProduct(String productName) {
        List<ProductionLine.Task> result = new ArrayList<>();
        for (ProductionLine line : productionLines) {
            for (ProductionLine.Task task : line.tasks) {
                if (task.productName != null && task.productName.equalsIgnoreCase(productName)) {
                    result.add(task);
                }
            }
        }
        return result;
    }

    public List<ProductionLine.Task> getTasksByCustomer(String customer) {
        List<ProductionLine.Task> result = new ArrayList<>();
        for (ProductionLine line : productionLines) {
            for (ProductionLine.Task task : line.tasks) {
                if (task.customer != null && task.customer.equalsIgnoreCase(customer)) {
                    result.add(task);
                }
            }
        }
        return result;
    }

    // ------------ DISPLAY FUNCTIONS ------------
    public void printProductionLines() {
        System.out.println("\n=== PRODUCTION LINES (" + productionLines.size() + " lines) ===");
        for (ProductionLine line : productionLines) {
            System.out.printf("ID: %d, Name: %s, State: %s, Tasks: %d%n",
                    line.id, line.name, line.state, line.tasks.size());

            // Print tasks for this production line
            if (!line.tasks.isEmpty()) {
                System.out.println("  Tasks:");
                for (ProductionLine.Task task : line.tasks) {
                    System.out.printf("    - Task ID: %d, Product: %s, Quantity: %d, State: %s, Progress: %.2f%%%n",
                            task.id, task.productName != null ? task.productName : "N/A",
                            task.quantity, task.state, task.progress);
                }
            }
        }
        System.out.println("=======================================\n");
    }

    public void printProductionLineDetails(int id) {
        ProductionLine line = findProductionLineById(id);
        if (line != null) {
            System.out.println("\n=== PRODUCTION LINE DETAILS ===");
            System.out.println("ID: " + line.id);
            System.out.println("Name: " + line.name);
            System.out.println("State: " + line.state);
            System.out.println("Number of Tasks: " + line.tasks.size());

            if (!line.tasks.isEmpty()) {
                System.out.println("\nTasks:");
                for (ProductionLine.Task task : line.tasks) {
                    System.out.println("  Task ID: " + task.id);
                    System.out.println("  Product: " + (task.productName != null ? task.productName : "N/A"));
                    System.out.println("  Quantity: " + task.quantity);
                    System.out.println("  Customer: " + task.customer);
                    System.out.println("  Start Date: " + task.startingDate);
                    System.out.println("  Finish Date: " + task.finishingDate);
                    System.out.println("  State: " + task.state);
                    System.out.println("  Progress: " + task.progress + "%");
                    System.out.println("  ---");
                }
            }
            System.out.println("===============================\n");
        } else {
            System.out.println("Production line with ID " + id + " not found.");
        }
    }

    // ====================== SUMMARY FUNCTIONS ========================
    public void printSummary() {
        System.out.println("\n=== INVENTORY SUMMARY ===");
        System.out.println("Items: " + items.size());
        System.out.println("Products: " + products.size());
        System.out.println("Production Lines: " + productionLines.size());
        System.out.println("Items File: " + itemsFilePath);
        System.out.println("Products File: " + productsFilePath);
        System.out.println("Production Lines File: " + productLineFilePath);
        System.out.println("=========================\n");
    }
}