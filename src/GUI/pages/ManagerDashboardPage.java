package GUI.pages;

import GUI.RunTask;
import GUI.components.*;
import GUI.components.Dialog;
import GUI.lib.RequiredValidator;
import classes.Item;
import classes.Product;
import classes.ProductionLine;
import mapper.JSONException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.stream.Collectors;

import static GUI.GUI.inventory;

public class ManagerDashboardPage extends JFrame {
    // Navigation tap
    String activeTap = "production lines";

    // Panels
    JPanel mainPanel = new JPanel();
    JPanel subPanel = new JPanel();
    JPanel actionButtonsPanel = new JPanel();
    JPanel navigationPanel = new JPanel();

    // Production lines table columns
    String[] productionLinesTableColumnNames = {"Id", "Name", "State", "Notes"};

    // Tasks table columns
    String[] tasksTableColumnNames = {"Id", "Product Name", "Quantity", "Customer", "Time span", "State", "Progress"};

    // Items table columns
    String[] itemsTableColumnNames = {"Id", "Name", "Category", "Price", "Stock", "minimum stock"};

    // Product table columns
    String[] productsTableColumnNames = {"Id", "Name", "Required items"};

    // Table
    DataTable table = new DataTable(productionLinesTableColumnNames);
    HashMap<String, String> filters = new HashMap<>();

    // Navigation buttons
    SecondaryButton items = new SecondaryButton("Items",
    e -> {
            resetFilters();
            fillItemsTable();
        }
    );
    SecondaryButton products = new SecondaryButton("Products",
    e -> {
        resetFilters();
        fillProductsTable();
    }
    );
    SecondaryButton productionLines = new SecondaryButton("Production lines",
    e -> {
        resetFilters();
        fillProductionLinesTable();
    }
    );
    SecondaryButton tasks = new SecondaryButton("Tasks",
    e -> {
        resetFilters();
        fillTasksTable();
    }
    );
    PrimaryButton refresh = new PrimaryButton("Refresh",
    e -> {
        try {
            inventory.loadAll();
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }

        resetFilters();
    }
    );

    // Buttons
    SecondaryButton add = new SecondaryButton("Add new",
    e -> {
        switch (activeTap) {
            case "items", "tasks" -> openAddDialog();
        }
    }
    );
    SecondaryButton delete = new SecondaryButton("Delete",
    e -> openDeleteDialog()
    );
    SecondaryButton edit = new SecondaryButton("Edit",
    e -> openEditDialog()
    );
    SecondaryButton checkInfo = new SecondaryButton("Check info",
    e -> {
        switch (activeTap) {
            case "production lines" -> openInfoDialog();
        }
    }
    );
    PrimaryButton filter = new PrimaryButton("Filter",
    e -> openFiltersDialog()
    );
    SecondaryButton removeFilters = new SecondaryButton("Remove filters",
    e -> resetFilters()
    );


    public ManagerDashboardPage() {
        resetFilters();
        fillProductionLinesTable();
        initializeFrame();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("TPM Admin Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Border layout manager
        setSize(1200, 900);
        getContentPane().setBackground(new Color(9, 9, 11)); // change background color

        // Table panel
        mainPanel.setLayout(new BorderLayout(16, 16)); // Border layout manager
        mainPanel.setBackground(new Color(9, 9, 11)); // change background color
        mainPanel.setBorder(new EmptyBorder(100, 48, 100, 48)); // Padding
        mainPanel.add(subPanel, BorderLayout.NORTH);
        mainPanel.add(table, BorderLayout.CENTER);

        // Navigation and action buttons panel
        subPanel.setLayout(new BorderLayout(16, 16)); // Border layout manager
        subPanel.setBackground(new Color(9, 9, 11)); // change background color
        subPanel.add(actionButtonsPanel, BorderLayout.SOUTH);
        subPanel.add(navigationPanel, BorderLayout.NORTH);

        // Action Buttons panel
        actionButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Flow layout manager
        actionButtonsPanel.setBackground(new Color(9, 9, 11)); // change background color
        actionButtonsPanel.add(filter);
        actionButtonsPanel.add(removeFilters);
        actionButtonsPanel.add(checkInfo);
        actionButtonsPanel.add(edit);
        actionButtonsPanel.add(delete);
        actionButtonsPanel.add(add);

        // Navigation panel
        navigationPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Flow layout manager
        navigationPanel.setBackground(new Color(9, 9, 11)); // change background color
        navigationPanel.add(refresh);
        navigationPanel.add(productionLines);
        navigationPanel.add(items);
        navigationPanel.add(tasks);
        navigationPanel.add(products);

        add(mainPanel, BorderLayout.CENTER);
    }

    // fill production lines table with data from inventory
    private void fillProductionLinesTable() {
        table.clearData(); // clear table state
        table.setData(productionLinesTableColumnNames); // set columns for production lines table
        activeTap = "production lines";

        for (ProductionLine pl: inventory.productionLines) {
            Object[] plData = {pl.id, pl.name, pl.state, pl.notes};

            if (filters.get("product name").isEmpty()) {
                table.addRow(plData);
            } else {
                for (ProductionLine.Task task : pl.tasks) {
                    if (task.productName.equals(filters.get("product name"))){
                        table.addRow(plData);
                        break;
                    }
                }
            }
        }
    }

    // fill tasks table with data from all production lines
    private void fillTasksTable() {
        table.clearData(); // clear table state
        table.setData(tasksTableColumnNames); // set columns for tasks table
        activeTap = "tasks";

        for (ProductionLine pl : inventory.productionLines) {
            for (ProductionLine.Task task : pl.tasks) {
                Object[] taskData = {
                        task.id, task.productName,
                        task.quantity,
                        task.customer,
                        task.startingDate + " to " + task.finishingDate,
                        task.state,
                        task.progress
                };

                boolean productionLineNameFilter = filters.get("production line name").isEmpty() ||
                        pl.name.equals(filters.get("production line name"));

                boolean productNameFilter = filters.get("product name").isEmpty() ||
                        task.productName.equals(filters.get("product name"));

                boolean taskStateFilter = filters.get("task state").equals("all") ||
                        task.state.equals(filters.get("task state"));

                if (productionLineNameFilter && productNameFilter && taskStateFilter) {
                    table.addRow(taskData);
                }
            }
        }
    }

    // fill items table with data from inventory
    private void fillItemsTable() {
        table.clearData(); // clear table state
        table.setData(itemsTableColumnNames); // set columns for items table
        activeTap = "items";

        for (Item item : inventory.items) {
            Object[] itemData = {item.id, item.name, item.category, item.price, item.stock, item.minimumStock};

            boolean itemNameFilter = filters.get("item name").isEmpty() ||
                    item.name.equals(filters.get("item name"));

            boolean itemCategoryFilter = filters.get("item category").isEmpty() ||
                    item.category.equals(filters.get("item category"));

            boolean itemAvailabilityFilter = filters.get("item availability").equals("all") ||
                    item.getAvailability().equals(filters.get("item availability"));

            if (itemNameFilter && itemCategoryFilter && itemAvailabilityFilter) {
                table.addRow(itemData);
            }
        }
    }

    // fill items table with data from inventory
    private void fillProductsTable() {
        table.clearData(); // clear table state
        table.setData(productsTableColumnNames); // set columns for items table
        activeTap = "products";

        for (Product product : inventory.products) {
            Object[] productData = {
                product.id,
                product.name,
                product.requiredItems
                    .stream()
                    .map(requiredItem -> requiredItem.item)
                    .collect(Collectors.joining(", "))
            };

            if (filters.get("production line name").isEmpty()) {
                table.addRow(productData);
            } else {
                ProductionLine pl = inventory.findProductionLineByName(filters.get("production line name"));
                if (pl != null) {
                    for (ProductionLine.Task task : pl.tasks) {
                        if (task.productName.equals(product.name)) {
                            table.addRow(productData);
                            break;
                        }
                    }
                }
            }
        }


    }

    private void openFiltersDialog() {
        Dialog dialog = new Dialog();

        SecondaryButton dialogCloseButton = new SecondaryButton("Cancel", e -> {
            dialog.hide(); // close dialog
        });

        switch (activeTap) {
            case "production lines" -> {
                // product name to filter production lines by
                TextInputField productName = new TextInputField("Product name", "product name");
                productName.setText(filters.get("product name"));

                PrimaryButton applyFilter = new PrimaryButton("Apply filter", e -> {
                    dialog.hide(); // close dialog

                    filters.put("product name", productName.getText());

                    fillProductionLinesTable();
                });

                Card card = new Card("Filter production lines", "Filter production lines by product name");
                card.addContent(productName);

                // Show dialog with form to add new filter
                dialog.setSize(720, 360);
                dialog.addContent(card);
                dialog.addFooterButton(dialogCloseButton);
                dialog.addFooterButton(applyFilter);
                dialog.show(this);
            }
            case "products" -> {
                // production line name to filter products by
                TextInputField productionLineName = new TextInputField("Production line name", "production line name");
                productionLineName.setText(filters.get("production line name"));

                PrimaryButton applyFilter = new PrimaryButton("Apply filter", e -> {
                    dialog.hide(); // close dialog

                    filters.put("production line name", productionLineName.getText());

                    fillProductsTable();
                });

                Card card = new Card("Filter products", "Filter products by production line name");
                card.addContent(productionLineName);

                // Show dialog with form to add new filter
                dialog.setSize(720, 360);
                dialog.addContent(card);
                dialog.addFooterButton(dialogCloseButton);
                dialog.addFooterButton(applyFilter);
                dialog.show(this);
            }
            case "tasks" -> {
                // production line name to filter tasks by
                TextInputField productionLineName = new TextInputField("Production line name", "production line name");
                productionLineName.setText(filters.get("production line name"));
                TextInputField productName = new TextInputField("Product name", "product name");
                productName.setText(filters.get("product name"));

                RadioInputField state = new RadioInputField("State");
                state.setOptions(new String[]{"all", "completed", "ongoing", "canceled"});
                state.setSelectedValue(filters.get("task state"));

                PrimaryButton applyFilter = new PrimaryButton("Apply filter", e -> {
                    dialog.hide(); // close dialog

                    filters.put("production line name", productionLineName.getText());
                    filters.put("product name", productName.getText());
                    filters.put("task state", state.getSelectedValue());

                    fillTasksTable();
                });

                Card card = new Card("Filter tasks", "Filter tasks by production line name, product name or task state");
                card.addContent(productionLineName);
                card.addContent(productName);
                card.addContent(state);

                // Show dialog with form to add new filter
                dialog.setSize(720, 640);
                dialog.addContent(card);
                dialog.addFooterButton(dialogCloseButton);
                dialog.addFooterButton(applyFilter);
                dialog.show(this);
            }
            case "items" -> {
                // production line name to filter tasks by
                TextInputField itemName = new TextInputField("Item name", "Item name");
                itemName.setText(filters.get("item name"));

                TextInputField itemCategory = new TextInputField("Item category", "Item category");
                itemCategory.setText(filters.get("item category"));

                RadioInputField itemAvailability = new RadioInputField("Availability");
                itemAvailability.setOptions(new String[]{"all", "available", "under minimum stock", "out of stock"});
                itemAvailability.setSelectedValue(filters.get("item availability"));

                PrimaryButton applyFilter = new PrimaryButton("Apply filter", e -> {
                    dialog.hide(); // close dialog

                    filters.put("item name", itemName.getText());
                    filters.put("item category", itemCategory.getText());
                    filters.put("item availability", itemAvailability.getSelectedValue());

                    fillItemsTable();
                });

                Card card = new Card("Filter items", "Filter items by name, category or availability");
                card.addContent(itemName);
                card.addContent(itemCategory);
                card.addContent(itemAvailability);

                // Show dialog with form to add new filter
                dialog.setSize(720, 640);
                dialog.addContent(card);
                dialog.addFooterButton(dialogCloseButton);
                dialog.addFooterButton(applyFilter);
                dialog.show(this);
            }
        }
    }

    private void openAddDialog() {
        Dialog dialog = new Dialog();

        SecondaryButton dialogCloseButton = new SecondaryButton("Cancel", e -> {
            dialog.hide(); // close dialog
        });

        switch (activeTap) {
            case "items" -> {
                TextInputField name = new TextInputField("Name", "item name");
                name.setValidator(new RequiredValidator());

                TextInputField category = new TextInputField("Category", "item category");
                name.setValidator(new RequiredValidator());

                TextInputField price = new TextInputField("Price", "item price");
                name.setValidator(new RequiredValidator());

                TextInputField stock = new TextInputField("Stock", "Stock in inventory");
                name.setValidator(new RequiredValidator());

                TextInputField minimumStock = new TextInputField("Minimum stock", "Minimum stock");
                name.setValidator(new RequiredValidator());

                PrimaryButton saveButton = new PrimaryButton("Save", e -> {
                    if (!name.getText().isEmpty() ||
                        !category.getText().isEmpty() ||
                        !price.getText().isEmpty() || !stock.getText().isEmpty() ||
                        !minimumStock.getText().isEmpty()) {


                        try {
                            Item newItem = new Item(
                                inventory.itemMaxId + 1,
                                name.getText(),
                                category.getText(),
                                Double.parseDouble(price.getText()),
                                Integer.parseInt(stock.getText()),
                                Integer.parseInt(minimumStock.getText())
                            );

                            try {
                                inventory.addItem(newItem);
                            } catch (JSONException exception) {
                                System.out.println(exception.getMessage());
                            }

                            dialog.hide(); // close dialog
                        } catch (NumberFormatException ex) {
                            price.setError(true, "You have to enter a number");
                            stock.setError(true, "You have to enter a number");
                            minimumStock.setError(true, "You have to enter a number");
                        }

                        fillItemsTable();
                    }
                });

                Card card = new Card("Adding new production line", "Please fill the form with the required data");
                card.addContent(name);
                card.addContent(category);
                card.addContent(price);
                card.addContent(stock);
                card.addContent(minimumStock);

                // Show dialog with form to add new production line
                dialog.setSize(720, 720);
                dialog.addContent(card);
                dialog.addFooterButton(dialogCloseButton);
                dialog.addFooterButton(saveButton);
                dialog.show(this);
            }
            case "tasks" -> {
                TextInputField productionLineName = new TextInputField("Production line name", "production line name");
                productionLineName.setValidator(new RequiredValidator());

                TextInputField productName = new TextInputField("Product name", "product name");
                productName.setValidator(new RequiredValidator());

                TextInputField quantity = new TextInputField("Quantity", "Quantity");
                quantity.setValidator(new RequiredValidator());

                TextInputField customer = new TextInputField("Customer", "Customer");
                customer.setValidator(new RequiredValidator());

                TextInputField startDate = new TextInputField("Start date", "Start date");
                startDate.setValidator(new RequiredValidator());

                TextInputField finishDate = new TextInputField("finish date", "finish date");
                finishDate.setValidator(new RequiredValidator());

                RadioInputField state = new RadioInputField("State");
                state.setOptions(new String[]{"ongoing", "completed", "canceled"});
                state.setSelectedValue("ongoing");

                PrimaryButton saveButton = new PrimaryButton("Save", e -> {
                    if (!productName.getText().isEmpty() || !productionLineName.getText().isEmpty() ||
                            !quantity.getText().isEmpty() ||
                            !customer.getText().isEmpty() || !startDate.getText().isEmpty() ||
                            !finishDate.getText().isEmpty()) {

                        try {
                            ProductionLine.Task newTask = new ProductionLine.Task(
                                    inventory.taskMaxId + 1,
                                    productName.getText(),
                                    Integer.parseInt(quantity.getText()),
                                    customer.getText(),
                                    startDate.getText(),
                                    finishDate.getText(),
                                    state.getSelectedValue(),
                                    0
                            );

                            try {
                                ProductionLine selectedProductionLine = inventory.findProductionLineByName(productionLineName.getText());

                                try {
                                    inventory.addTaskToProductionLine(selectedProductionLine.id, newTask);
                                } catch (JSONException exception) {
                                    System.out.println(exception.getMessage());
                                }

                                dialog.hide(); // close dialog
                            } catch (NullPointerException ex) {
                                productionLineName.setError(true, "Couldn't find production line");
                            }


                        } catch (NumberFormatException ex) {
                            quantity.setError(true, "You have to enter a number");
                        }

                        fillTasksTable();
                    }
                });

                Card card = new Card("Adding new production line", "Please fill the form with the required data");
                card.addContent(productionLineName);
                card.addContent(productName);
                card.addContent(quantity);
                card.addContent(customer);
                card.addContent(startDate);
                card.addContent(finishDate);
                card.addContent(state);

                // Show dialog with form to add new production line
                dialog.setSize(720, 1080);
                dialog.addContent(card);
                dialog.addFooterButton(dialogCloseButton);
                dialog.addFooterButton(saveButton);
                dialog.show(this);
            }
        }
    }

    private void openDeleteDialog() {
        Dialog dialog = new Dialog();

        SecondaryButton dialogCloseButton = new SecondaryButton("Cancel", e -> {
            dialog.hide(); // close dialog
        });

        switch (activeTap) {
            case "items" -> {
                try {
                    int selectedItemId = (int) table.getSelectedRowData()[0]; // selected row id
                    Item selectedItem = inventory.findItemById(selectedItemId);


                    PrimaryButton confirmDelete = new PrimaryButton("Confirm delete", 144, 20, e -> {
                        dialog.hide(); // close dialog

                        try {
                            inventory.removeItem(selectedItem);
                        } catch (JSONException exception) {
                            System.out.println(exception.getMessage());
                        }

                        fillItemsTable();
                    });

                    SecondaryButton cancelDelete = new SecondaryButton("Cancel delete", 147, 20, e -> {
                        dialog.hide(); // close dialog
                    });

                    Card card = new Card("Confirm deleting: " + selectedItem.name, "Deleted data will be lost permanently!");
                    card.addContent(cancelDelete);
                    card.addContent(confirmDelete);

                    // Show dialog to confirm delete
                    dialog.setSize(480, 360);
                    dialog.addContent(card);
                    dialog.show(this);
                } catch (NullPointerException exception) {
                    // Show error dialog
                    dialog.setTitle("Error, Please select an item first");
                    dialog.setSize(520, 160);
                    dialog.addFooterButton(dialogCloseButton);
                    dialog.show(this);
                }
            }
            case "tasks" -> {
                try {
                    int selectedTaskId = (int) table.getSelectedRowData()[0]; // selected row id
                    ProductionLine.Task selectedTask = inventory.findTaskById(selectedTaskId);

                    PrimaryButton confirmDelete = new PrimaryButton("Confirm delete", 144, 20, e -> {
                        dialog.hide(); // close dialog

                        try {
                            inventory.removeTask(selectedTask);
                        } catch (JSONException exception) {
                            System.out.println(exception.getMessage());
                        }

                        fillTasksTable();
                    });

                    SecondaryButton cancelDelete = new SecondaryButton("Cancel delete", 147, 20, e -> {
                        dialog.hide(); // close dialog
                    });

                    Card card = new Card("Confirm deleting: " + selectedTask.productName, "Deleted data will be lost permanently!");
                    card.addContent(cancelDelete);
                    card.addContent(confirmDelete);

                    // Show dialog to confirm delete
                    dialog.setSize(480, 360);
                    dialog.addContent(card);
                    dialog.show(this);
                } catch (NullPointerException exception) {
                    // Show error dialog
                    dialog.setTitle("Error, Please select a task first");
                    dialog.setSize(520, 160);
                    dialog.addFooterButton(dialogCloseButton);
                    dialog.show(this);
                }
            }

        }
    }

    private void openEditDialog() {
        Dialog dialog = new Dialog();

        SecondaryButton dialogCloseButton = new SecondaryButton("Cancel", e -> {
            dialog.hide(); // close dialog
        });

        switch (activeTap) {
            case "items" -> {
                try {
                    int selectedItemId = (int) table.getSelectedRowData()[0]; // selected row id
                    Item selectedItem = inventory.findItemById(selectedItemId);

                    TextInputField name = new TextInputField("Name", "item name");
                    name.setValidator(new RequiredValidator());
                    name.setText(selectedItem.name);

                    TextInputField category = new TextInputField("Category", "item category");
                    category.setValidator(new RequiredValidator());
                    category.setText(selectedItem.category);

                    TextInputField price = new TextInputField("Price", "item price");
                    price.setValidator(new RequiredValidator());
                    price.setText(String.valueOf(selectedItem.price));

                    TextInputField stock = new TextInputField("Stock", "Stock in inventory");
                    stock.setValidator(new RequiredValidator());
                    stock.setText(String.valueOf(selectedItem.stock));

                    TextInputField minimumStock = new TextInputField("Minimum stock", "Minimum stock");
                    minimumStock.setValidator(new RequiredValidator());
                    minimumStock.setText(String.valueOf(selectedItem.minimumStock));

                    PrimaryButton saveButton = new PrimaryButton("Save", e -> {
                        if (!name.getText().isEmpty() ||
                                !category.getText().isEmpty() ||
                                !price.getText().isEmpty() || !stock.getText().isEmpty() ||
                                !minimumStock.getText().isEmpty()) {

                            try {
                                selectedItem.name = name.getText();
                                selectedItem.category = category.getText();
                                selectedItem.price = Double.parseDouble(price.getText());
                                selectedItem.stock = Integer.parseInt(stock.getText());
                                selectedItem.minimumStock = Integer.parseInt(minimumStock.getText());

                                try {
                                    inventory.saveItems();
                                } catch (JSONException exception) {
                                    System.out.println(exception.getMessage());
                                }

                                dialog.hide(); // close dialog
                            } catch (NumberFormatException ex) {
                                price.setError(true, "You have to enter a number");
                                stock.setError(true, "You have to enter a number");
                                minimumStock.setError(true, "You have to enter a number");
                            }

                            fillItemsTable();
                        }
                    });

                    Card card = new Card("Adding new production line", "Please fill the form with the required data");
                    card.addContent(name);
                    card.addContent(category);
                    card.addContent(price);
                    card.addContent(stock);
                    card.addContent(minimumStock);

                    // Show dialog with form to add new production line
                    dialog.setSize(720, 720);
                    dialog.addContent(card);
                    dialog.addFooterButton(dialogCloseButton);
                    dialog.addFooterButton(saveButton);
                    dialog.show(this);
                } catch (NullPointerException exception) {
                    // Show error dialog
                    dialog.setTitle("Error, Please select an item first");
                    dialog.setSize(520, 160);
                    dialog.addFooterButton(dialogCloseButton);
                    dialog.show(this);
                }
            }
            case "tasks" -> {
                try {
                    int selectedTaskId = (int) table.getSelectedRowData()[0]; // selected row id
                    ProductionLine.Task selectedTask = inventory.findTaskById(selectedTaskId);

                    TextInputField productName = new TextInputField("Product name", "product name");
                    productName.setValidator(new RequiredValidator());
                    productName.setText(selectedTask.productName);

                    TextInputField quantity = new TextInputField("Quantity", "Quantity");
                    quantity.setValidator(new RequiredValidator());
                    quantity.setText(String.valueOf(selectedTask.quantity));

                    TextInputField customer = new TextInputField("Customer", "Customer");
                    customer.setValidator(new RequiredValidator());
                    customer.setText(selectedTask.customer);

                    TextInputField startDate = new TextInputField("Start date", "Start date");
                    startDate.setValidator(new RequiredValidator());
                    startDate.setText(selectedTask.startingDate);

                    TextInputField finishDate = new TextInputField("finish date", "finish date");
                    finishDate.setValidator(new RequiredValidator());
                    finishDate.setText(selectedTask.finishingDate);

                    RadioInputField state = new RadioInputField("State");
                    state.setOptions(new String[]{"ongoing", "completed", "canceled"});
                    state.setSelectedValue(selectedTask.state);

                    PrimaryButton saveButton = new PrimaryButton("Save", e -> {
                        dialog.hide(); // close dialog

                        if (!productName.getText().isEmpty() ||
                                !quantity.getText().isEmpty() ||
                                !customer.getText().isEmpty() || !startDate.getText().isEmpty() ||
                                !finishDate.getText().isEmpty()) {

                            selectedTask.productName = productName.getText();
                            selectedTask.quantity = Integer.parseInt(quantity.getText());
                            selectedTask.customer = customer.getText();
                            selectedTask.startingDate = startDate.getText();
                            selectedTask.finishingDate = finishDate.getText();
                            selectedTask.state = state.getSelectedValue();

                            fillTasksTable();
                        }
                    });

                    Card card = new Card("Editing task", "Please fill the form with the required data");
                    card.addContent(productName);
                    card.addContent(quantity);
                    card.addContent(customer);
                    card.addContent(startDate);
                    card.addContent(finishDate);
                    card.addContent(state);

                    // Show dialog with form to add new production line
                    dialog.setSize(720, 1080);
                    dialog.addContent(card);
                    dialog.addFooterButton(dialogCloseButton);
                    dialog.addFooterButton(saveButton);
                    dialog.show(this);

                } catch (NullPointerException ex) {
                    // Show error dialog
                    dialog.setTitle("Error, Please select a task first");
                    dialog.setSize(520, 160);
                    dialog.addFooterButton(dialogCloseButton);
                    dialog.show(this);
                }
            }
        }
    }

    private void openInfoDialog() {
        Dialog dialog = new Dialog();

        DataTable tasksTable = new DataTable(tasksTableColumnNames);

        SecondaryButton dialogCloseButton = new SecondaryButton("Close", e -> {
            dialog.hide(); // close dialog
        });

        try {
            int selectedProductionLineId = (int) table.getSelectedRowData()[0]; // selected row id
            ProductionLine selectedProductionLine = inventory.findProductionLineById(selectedProductionLineId);

            tasksTable.clearData(); // clear table state

            for (ProductionLine.Task task : selectedProductionLine.tasks) {
                Object[] taskData = {
                        task.id, task.productName,
                        task.quantity,
                        task.customer,
                        task.startingDate + " to " + task.finishingDate,
                        task.state,
                        task.progress
                };

                tasksTable.addRow(taskData);
            }
            // Show dialog with selected production line tasks
            dialog.setTitle(selectedProductionLine.name + " running tasks");
            dialog.setSize(1400, 360);
            dialog.addContent(tasksTable);
            dialog.addFooterButton(dialogCloseButton);
            dialog.show(this);
        } catch (NullPointerException exception) {
            // Show error dialog
            dialog.setTitle("Error, Please select a production line first");
            dialog.setSize(520, 160);
            dialog.addFooterButton(dialogCloseButton);
            dialog.show(this);
        }
    }

    private void resetFilters () {
        filters.put("production line name", "");
        filters.put("product name", "");
        filters.put("task state", "all");
        filters.put("item name", "");
        filters.put("item category", "");
        filters.put("item availability", "all");

        switch (activeTap) {
            case "production lines" -> fillProductionLinesTable();
            case "products" -> fillProductsTable();
            case "tasks" -> fillTasksTable();
            case "items" -> fillItemsTable();
        }
    }
}