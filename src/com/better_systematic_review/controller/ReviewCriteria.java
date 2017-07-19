package com.better_systematic_review.controller;

import com.better_systematic_review.model.Criteria;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

/**
 * Created by Porter on 7/14/2017.
 */
public class ReviewCriteria {
    @FXML private TreeTableView<Criteria> treeTable;
    @FXML private TreeTableColumn<Criteria, String> criteriaName;
    @FXML private TreeTableColumn<Criteria, Boolean> criteriaReq;
    public static TreeItem<Criteria> root;

    //Will need to change table creation here to support persistence.
    @FXML
    public void initialize() {
        // Create the RootNode
        if(root == null) {
            Criteria criteria = new Criteria();
            criteria.setName("Root");
            root = new TreeItem<Criteria>(criteria);
        }
        root.setExpanded(true);
        treeTable.setRoot(root);

        // Create Columns with Cell Factories
        criteriaName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        criteriaName.setCellFactory(TextFieldTreeTableCell.<Criteria>forTreeTableColumn());

        criteriaReq.setCellValueFactory(new TreeItemPropertyValueFactory<>("required"));
        criteriaReq.setCellFactory(CheckBoxTreeTableCell.<Criteria>forTreeTableColumn(criteriaReq));
    }

    @FXML
    public void addRow() {
        if (treeTable.getSelectionModel().isEmpty()){
            // Indicate a row needs to be selected
            return;
        }
        else {
            // Add Child
            // Prepare a new TreeItem with a new Person object
            TreeItem<Criteria> item = new TreeItem<>(new Criteria());

            // Get the selection model
            TreeTableView.TreeTableViewSelectionModel<Criteria> sm = treeTable.getSelectionModel();

            // Get the selected row index
            int rowIndex = sm.getSelectedIndex();

            // Get the selected TreeItem
            TreeItem<Criteria> selectedItem = sm.getModelItem(rowIndex);

            // Add the new item as children to the selected item
            selectedItem.getChildren().add(item);

            // Make sure the new item is visible
            selectedItem.setExpanded(true);

            // Edit the item
            //this.editItem(item);
        }

    }

    @FXML
    public void deleteRow() {
        // Get the selection model
        TreeTableView.TreeTableViewSelectionModel<Criteria> sm = treeTable.getSelectionModel();
        if (sm.isEmpty()) {
            // Indicate a row needs to be selected
            return;
        }

        // Get the selected Entry
        int rowIndex = sm.getSelectedIndex();
        TreeItem<Criteria> selectedItem = sm.getModelItem(rowIndex);
        TreeItem<Criteria> parent = selectedItem.getParent();

        if (parent != null) {
            // Remove the Item
            parent.getChildren().remove(selectedItem);
        }
    }

    @FXML
    public void saveCriteria() {

    }
}
