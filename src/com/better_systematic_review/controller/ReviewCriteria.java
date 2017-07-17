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
    private ObservableList<Criteria> criteriaList;

    @FXML
    public void initialize() {
        // Create the RootNode
        Criteria criteria = new Criteria();
        criteria.setName("test");
        TreeItem<Criteria> root = new TreeItem<Criteria>(criteria);
        root.setExpanded(true);

        treeTable.setRoot(root);

        // Create Columns with Cell Factories
        criteriaName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        criteriaName.setCellFactory(TextFieldTreeTableCell.<Criteria>forTreeTableColumn());

        criteriaReq.setCellValueFactory(new TreeItemPropertyValueFactory<>("required"));
        criteriaReq.setCellFactory(CheckBoxTreeTableCell.<Criteria>forTreeTableColumn(criteriaReq));
    }
}
