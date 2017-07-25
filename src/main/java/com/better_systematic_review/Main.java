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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.*;
import java.util.ArrayList;

public class Main extends Application {
    private static Path documentPath;
    private static Path reviewPath;
    private static ArrayList<Review> reviewList = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Path applicationPath = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        Path localPath = applicationPath.getParent().resolve("local");
        reviewPath = localPath.resolve("reviews");
        documentPath = localPath.resolve("documents");
        try {
            Files.createDirectory(localPath);
            Files.createDirectory(reviewPath);
            Files.createDirectory(documentPath);
        } catch (FileAlreadyExistsException e) {
        }

        try {
            for (File file : new File(reviewPath.toString()).listFiles()) {
                FileInputStream f_in = new FileInputStream(file);
                ObjectInputStream obj_in = new ObjectInputStream(f_in);
                Object obj = obj_in.readObject();
                if (obj instanceof Review) {
                    reviewList.add((Review) obj);
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/review_selection.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("A Better Systematic Review");
            Scene scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
            ReviewSelection reviewSelection = loader.getController();
            reviewSelection.setTable(reviewList);
            primaryStage.show();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path getDocumentPath(){
        return documentPath;
    }

    public static Path getReviewPath() {
        return reviewPath;
    }
    public static ArrayList<Review> getReviewList () {
        return reviewList;
    }
}
