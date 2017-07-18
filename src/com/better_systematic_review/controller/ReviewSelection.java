package com.better_systematic_review.controller;


import com.better_systematic_review.model.Review;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReviewSelection {

    @FXML private TableView<Review> table;
    @FXML private TextField addName;

    @FXML
    private void handleAddReview(ActionEvent event) {
        ObservableList<Review> data = table.getItems();

        if(!addName.getText().isEmpty()) {
            Review newReview = new Review(addName.getText(), String.valueOf(data.size()), "Never");
            data.add(newReview);
            addName.setText("");
            newReview.save();
        }

    }

    @FXML
    private void handleOpenReview(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/review_screen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) table.getScene().getWindow();
        stage.setScene(scene);
        Review.setCurrent(table.getItems().get(table.getFocusModel().getFocusedIndex()));
        System.out.println(Review.getCurrent());
    }

    public void setTable(List<Review> items){
        table.getItems().addAll(items);
    }
}
