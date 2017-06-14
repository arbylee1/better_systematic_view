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
        Document[] docsArray = new Document[3];
        docsArray[0] = new Document(new String[] {"David Thomson"}, "How stuff works", "2020");
        docsArray[1] = new Document(new String[] {"Albert Einstein"}, "Space is weird", "2020");
        docsArray[2] = new Document(new String[] {"John Smith"}, "Behavioral Study", "2021");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("review_screen.fxml"));
        Parent root = loader.load();
        ReviewScreen controller = loader.getController();
        controller.setDocuments(Arrays.asList(docsArray));

        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) table.getScene().getWindow();
        stage.setScene(scene);
    }
}
