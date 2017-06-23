package better_systematic_view;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("review_selection.fxml"));
        primaryStage.setTitle("A Better Systematic Review");
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        File f = new File("review_selection_ui.css");
        scene.getStylesheets().clear();
        System.out.print(f.getAbsolutePath());
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("review_selection_ui.css", "src/better_systematic_view/review_selection_ui.css"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}