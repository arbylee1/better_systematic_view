package better_systematic_view;

import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ReviewSelectionController {
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
}
