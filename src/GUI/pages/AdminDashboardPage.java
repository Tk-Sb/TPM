package GUI.pages;

import GUI.components.*;
import GUI.components.Dialog;
import GUI.lib.RequiredValidator;
import classes.ProductionLine;
import mapper.JSONException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import static GUI.GUI.inventory;

public class AdminDashboardPage extends JFrame {
    // Panels
    JPanel mainPanel = new JPanel();
    JPanel subPanel = new JPanel();

    // Production lines table
    String[] productionLinesTableColumnNames = {"Id", "Name", "State", "Notes"};
    DataTable productionLinesTable = new DataTable(productionLinesTableColumnNames);

    // Tasks table
    String[] tasksTableColumnNames = {"Id", "Product Name", "Quantity", "Customer", "Time span", "State", "Progress"};
    DataTable tasksTable = new DataTable(tasksTableColumnNames);

    // Buttons
    PrimaryButton addProductionLine = new PrimaryButton("Add new",
    e -> openAddDialog()
    );
    SecondaryButton deleteProductionLine = new SecondaryButton("Delete",
    e -> openDeleteDialog()
    );
    SecondaryButton editProductionLine = new SecondaryButton("Edit",
    e -> openEditDialog()
    );
    SecondaryButton checkProductionLineInfo = new SecondaryButton("Check info",
    e -> openInfoDialog()
    );

    public AdminDashboardPage() {
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
        mainPanel.add(productionLinesTable, BorderLayout.CENTER);

        // Buttons panel
        subPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Flow layout manager
        subPanel.setBackground(new Color(9, 9, 11)); // change background color
        subPanel.add(editProductionLine);
        subPanel.add(checkProductionLineInfo);
        subPanel.add(deleteProductionLine);
        subPanel.add(addProductionLine);

        add(mainPanel, BorderLayout.CENTER);
    }

    // fill production lines table with data from inventory
    private void fillProductionLinesTable() {
        productionLinesTable.clearData(); // clear table state

        for (ProductionLine pl: inventory.productionLines) {
            Object[] plData = {pl.id, pl.name, pl.state, pl.notes};
            productionLinesTable.addRow(plData);
        }
    }

    // fill tasks table with data from selected production line
    private void fillTasksTable(List<ProductionLine.Task> tasks) {
        tasksTable.clearData(); // clear table state

        for (ProductionLine.Task task : tasks) {
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
    }

    private void openAddDialog() {
        Dialog dialog = new Dialog();

        SecondaryButton dialogCloseButton = new SecondaryButton("Close", e -> {
            dialog.hide(); // close dialog
        });

        TextInputField name = new TextInputField("Name", "production line name");
        name.setValidator(new RequiredValidator());

        RadioInputField state = new RadioInputField("State");
        state.setOptions(new String[]{"active", "inactive", "maintenance"});
        state.setSelectedValue("inactive");

        TextInputField notes = new TextInputField("Notes", "notes about the production line");

        PrimaryButton saveButton = new PrimaryButton("Save", e -> {
            if (!name.getText().isEmpty()) {
                dialog.hide(); // close dialog

                // save form data
                ProductionLine newProductionLine = new ProductionLine(
                    inventory.productionLineMaxId + 1,
                    name.getText(),
                    state.getSelectedValue(),
                    notes.getText()
                );

                try {
                    inventory.addProductionLine(newProductionLine);
                } catch (JSONException exception) {
                    System.out.println(exception.getMessage());
                }

                fillProductionLinesTable();
            }
        });

        Card card = new Card("Adding new production line", "Please fill the form with the required data");
        card.addContent(name);
        card.addContent(notes);
        card.addContent(state);

        // Show dialog with form to add new production line
        dialog.setSize(720, 720);
        dialog.addContent(card);
        dialog.addFooterButton(dialogCloseButton);
        dialog.addFooterButton(saveButton);
        dialog.show(this);
    }

    private void openDeleteDialog() {
        Dialog dialog = new Dialog();

        SecondaryButton dialogCloseButton = new SecondaryButton("Close", e -> {
            dialog.hide(); // close dialog
        });

        try {
            int selectedProductionLineId = (int) productionLinesTable.getSelectedRowData()[0];  // selected row id
            ProductionLine selectedProductionLine = inventory.findProductionLineById(selectedProductionLineId);


            PrimaryButton confirmDelete = new PrimaryButton("Confirm delete", 144, 20, e -> {
                dialog.hide(); // close dialog

                try {
                    inventory.removeProductionLine(selectedProductionLine);
                } catch (JSONException exception) {
                    System.out.println(exception.getMessage());
                }

                fillProductionLinesTable();
            });

            SecondaryButton cancelDelete = new SecondaryButton("Cancel delete", 147, 20, e -> {
                dialog.hide(); // close dialog
            });

            Card card = new Card("Confirm deleting: " + selectedProductionLine.name, "Deleted data will be lost permanently!");
            card.addContent(cancelDelete);
            card.addContent(confirmDelete);

            // Show dialog to confirm delete
            dialog.setSize(480, 360);
            dialog.addContent(card);
            dialog.show(this);
        } catch (NullPointerException exception) {
            // Show error dialog
            dialog.setTitle("Error, Please select a production line first");
            dialog.setSize(520, 160);
            dialog.addFooterButton(dialogCloseButton);
            dialog.show(this);
        }
    }

    private void openEditDialog() {
        Dialog dialog = new Dialog();

        SecondaryButton dialogCloseButton = new SecondaryButton("Close", e -> {
            dialog.hide(); // close dialog
        });

        try {
            int selectedProductionLineId = (int) productionLinesTable.getSelectedRowData()[0];  // selected row id
            ProductionLine selectedProductionLine = inventory.findProductionLineById(selectedProductionLineId);

            TextInputField name = new TextInputField("Name", "production line name");
            name.setText(selectedProductionLine.name);
            name.setValidator(new RequiredValidator());

            RadioInputField state = new RadioInputField("State");
            state.setOptions(new String[]{"active", "inactive", "maintenance"});
            state.setSelectedValue(selectedProductionLine.state);

            TextInputField notes = new TextInputField("Notes", "notes about the production line");
            notes.setText(selectedProductionLine.notes);

            PrimaryButton saveButton = new PrimaryButton("Save", e -> {
                if (!name.getText().isEmpty()) {
                    dialog.hide(); // close dialog
                    // save form data
                    selectedProductionLine.name = name.getText();
                    selectedProductionLine.notes = notes.getText();
                    selectedProductionLine.state = state.getSelectedValue();

                    try {
                        inventory.saveProductionLines();
                    } catch (JSONException exception) {
                        System.out.println(exception.getMessage());
                    }

                    fillProductionLinesTable();
                }
            });

            Card card = new Card("Editing " + selectedProductionLine.name, "Edit the form data then click save");
            card.addContent(name);
            card.addContent(notes);
            card.addContent(state);

            // Show dialog with form filled with selected production line data
            dialog.setSize(720, 720);
            dialog.addContent(card);
            dialog.addFooterButton(dialogCloseButton);
            dialog.addFooterButton(saveButton);
            dialog.show(this);
        } catch (NullPointerException exception) {
            // Show error dialog
            dialog.setTitle("Error, Please select a production line first");
            dialog.setSize(520, 160);
            dialog.addFooterButton(dialogCloseButton);
            dialog.show(this);
        }
    }

    private void openInfoDialog() {
        Dialog dialog = new Dialog();

        SecondaryButton dialogCloseButton = new SecondaryButton("Close", e -> {
            dialog.hide(); // close dialog
        });

        try {
            int selectedProductionLineId = (int) productionLinesTable.getSelectedRowData()[0];  // selected row id
            ProductionLine selectedProductionLine = inventory.findProductionLineById(selectedProductionLineId);

            fillTasksTable(selectedProductionLine.tasks);

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
}
