package better_systematic_view;

import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ReviewSelection {

    @FXML private TableView<Review> table;
    @FXML private TextField addName;

    @FXML
    private void handleAddReview(ActionEvent event) {
        ObservableList<Review> data = table.getItems();

        if(!addName.getText().isEmpty()) {
            data.add(new Review(
                    addName.getText(),
                    String.valueOf(ThreadLocalRandom.current().nextInt(1, 100 + 1)),
                    "Never"
            ));

            addName.setText("");
        }
    }

    @FXML
    private void handleOpenReview(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("review_screen.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) table.getScene().getWindow();
        stage.setScene(scene);
        
        File f = new File("review_screen_ui.css");
        scene.getStylesheets().clear();
        System.out.print(f.getAbsolutePath());
        //scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("review_screen_ui.css", "src/better_systematic_view/review_screen_ui.css"));

    }
}
