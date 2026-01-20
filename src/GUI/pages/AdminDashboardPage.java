package GUI.pages;

import GUI.components.*;
import GUI.components.Dialog;
import classes.Inventory;
import classes.ProductionLine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboardPage extends JFrame {

    public AdminDashboardPage(Inventory inventory) {
        setTitle("TPM Admin Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1200, 900);
        getContentPane().setBackground(new Color(9, 9, 11));

        // Table panel
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(new Color(9, 9, 11));
        mainPanel.setBorder(new EmptyBorder(100, 48, 100, 48));

        String[] productionLinesTableColumnNames = {"Id", "Name", "State", "Notes"};
        DataTable productionLinesTable = new DataTable(productionLinesTableColumnNames);

        for (ProductionLine pl: inventory.productionLines) {
            Object[] plData = {pl.id, pl.name, pl.state, "Admin notes"};
            productionLinesTable.addRow(plData);
        }

        // Buttons panel
        JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        subPanel.setBackground(new Color(9, 9, 11));

        PrimaryButton addProductionLine = new PrimaryButton("Add new production line", e -> {});
        SecondaryButton editProductionLine = new SecondaryButton("Edit production line", e -> {});
        SecondaryButton checkProductionLineInfo = new SecondaryButton("Check production line info", e -> {
            try {
                int selectedProductionLineId = (int) productionLinesTable.getSelectedRowData()[0];
                ProductionLine selectedProductionLine = inventory.findProductionLineById(selectedProductionLineId);

                String[] tasksTableColumnNames = {"Id", "Product Name", "State", "Progress"};
                DataTable tasksTable = new DataTable(tasksTableColumnNames);

                for (ProductionLine.Task task : selectedProductionLine.tasks) {
                    Object[] taskData = {task.id, task.productName, task.state, task.progress};
                    tasksTable.addRow(taskData);
                }
//                for (ProductionLine pl: inventory.productionLines) {
//                    Object[] plData = {pl.id, pl.name, pl.state, "Admin notes"};
//                    productionLinesTable.addRow(plData);
//                }


                // Show dialog with selected production line tasks
                Dialog productionLineInfoDialog = new Dialog(selectedProductionLine.name + " information");
                productionLineInfoDialog.addContent(tasksTable);
                productionLineInfoDialog.show(this);

            } catch (NullPointerException exception) {
                // Show error dialog
                Dialog errorDialog = new Dialog("Error");
                errorDialog.addContent(new JLabel("Please select a production line first"));
                PrimaryButton closeButton = new PrimaryButton("Close", ev -> {
                    errorDialog.hide();
                });
                errorDialog.addFooterButton(closeButton);
                errorDialog.show(this);
            }

        });


        subPanel.add(editProductionLine);
        subPanel.add(checkProductionLineInfo);
        subPanel.add(addProductionLine);
        mainPanel.add(subPanel, BorderLayout.NORTH);

        mainPanel.add(productionLinesTable, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
