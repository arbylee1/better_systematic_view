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

public class ReviewController {
    @FXML private TableView<Review> table;
    @FXML private TextField addName;
    @FXML private TextField addID;
    @FXML private TextField addLastLogin;

    @FXML
    private void handleAddReview(ActionEvent event) {
        ObservableList<Review> data = table.getItems();
        data.add(new Review(
                addName.getText(),
                addID.getText(),
                addLastLogin.getText()
        ));

        addName.setText("");
        addID.setText("");
        addLastLogin.setText("");
    }

    @FXML
    private void handleOpenReview(ActionEvent event) throws IOException {
        Document[] docsArray = new Document[3];
        docsArray[0] = new Document(new String[] {"David Thomson"}, "How stuff works", "2020");
        docsArray[1] = new Document(new String[] {"Albert Einstein"}, "Space is weird", "2020");
        docsArray[2] = new Document(new String[] {"John Smith"}, "Behavioral Study", "2021");

        for (Document doc : docsArray) {
            ReviewScreen.docs.put(doc.hashCode(), doc);
        }

        Review selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.print(selected.getName());
            ReviewScreen.labelText = selected.getName();
        }

        Stage stage = (Stage) table.getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("review_screen.fxml")); //Should point to second screen
        Scene scene = new Scene(root, 800, 600);

        stage.setScene(scene);
    }
}
