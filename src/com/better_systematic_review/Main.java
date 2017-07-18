package com.better_systematic_review;

import com.better_systematic_review.controller.ReviewSelection;
import com.better_systematic_review.model.Review;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;

public class Main extends Application {
    private static Path applicationPath;
    private static Path documentPath;
    private static Path reviewPath;

    @Override
    public void start(Stage primaryStage) throws Exception{
        applicationPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        reviewPath = applicationPath.resolve("local").resolve("reviews");
        Files.createDirectories(reviewPath);
        documentPath = applicationPath.resolve("local").resolve("documents");
        Files.createDirectories(documentPath);
        ArrayList<Review> reviewList = new ArrayList<>();
        for(File file : new File(reviewPath.toString()).listFiles()) {
            FileInputStream f_in = new FileInputStream(file);
            ObjectInputStream obj_in = new ObjectInputStream(f_in);
            Object obj = obj_in.readObject();
            System.out.println(obj);
            if (obj instanceof Review)
            {
               reviewList.add((Review) obj);
            }
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/better_systematic_review/view/review_selection.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("A Better Systematic Review");
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        ReviewSelection reviewSelection = loader.getController();
        reviewSelection.setTable(reviewList);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    public void init() throws Exception {
    }
    public static Path getApplicationPath() {
        return applicationPath;
    }
    public static Path getDocumentPath(){
        return documentPath;
    }
    public static Path getReviewPath() {
        return reviewPath;
    }
}