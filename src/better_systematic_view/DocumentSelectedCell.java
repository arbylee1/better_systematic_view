package better_systematic_view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.layout.AnchorPane;

public class DocumentSelectedCell extends TableCell<Document, Integer> {
    @FXML private CheckBox checkBox;

    public int id;

    @Override
    public void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);



        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("document_selected_cell.fxml"));
            setGraphic(loader.load());

            if (!empty) {
                DocumentSelectedCell cell = loader.getController();
                System.out.println(item);
                cell.id = item;
            }
        } catch (Exception e) {
            System.out.println("WHOOPS");
        }
    }

    @FXML
    public void checkBoxChanged(ActionEvent actionEvent) {
        System.out.println(this.id);
    }
}
