package better_systematic_view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReviewScreen {

    @FXML private TableView<TableDocument> docsTable;
    @FXML private Label reviewLabel;
    @FXML private CheckBox selectAllCheckBox;

    private static final String CONFIRM_DELETE_TITLE = "Delete files";
    private static final String CONFIRM_DELETE = "Are you sure you want to delete these files from the review?";

    private static String labelText;
    private List<TableDocument> selectedDocs = new ArrayList<>();

    public void setDocuments(Collection<Document> docs) {
        if (docs.isEmpty()) {
            return;
        }

        selectAllCheckBox.setDisable(false);
        docsTable.getItems().clear();
        docs.forEach(d -> docsTable.getItems().add(new TableDocument(d)));
    }

    private void deleteSelectedDocuments() {
        selectAllCheckBox.setSelected(false);

        if (selectedDocs.size() == docsTable.getItems().size()) {
            selectAllCheckBox.setDisable(true);
        }

        for (TableDocument doc : selectedDocs) {
            docsTable.getItems().remove(doc);
        }

        selectedDocs.clear();
    }

    @FXML
    public void initialize() {
        reviewLabel.setText(labelText);
    }

    @FXML
    private void selectAllDocuments(ActionEvent event) {
        docsTable.getItems().forEach(d -> d.selected.set(selectAllCheckBox.isSelected()));
    }

    @FXML
    private void includeCheckedDocs(ActionEvent event) {

    }

    @FXML
    private void excludeCheckedDocs(ActionEvent event) {

    }

    @FXML
    private void confirmDelete(ActionEvent event) {
        if (selectedDocs.isEmpty()) {
            return;
        }

        Alert areYouSure = new Alert(Alert.AlertType.CONFIRMATION);
        areYouSure.setHeaderText(null);
        areYouSure.setTitle(CONFIRM_DELETE_TITLE);
        areYouSure.setContentText(CONFIRM_DELETE);

        areYouSure.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteSelectedDocuments();
            }
        });
    }

    @FXML
    private void addDocument(ActionEvent event) {

    }

    @FXML
    private void importDocuments(ActionEvent event) {

    }

    @FXML
    private void exportDocuments(ActionEvent event) {

    }

    @FXML
    private void exportToExcel(ActionEvent event) {

    }

    /**
     * This inner class is used to provide an easy way to represent documents in
     * the TableView on the review screen. It includes the selected property,
     * which allows the review screen to keep track of which documents are
     * selected.
     */
    public class TableDocument {

        private SimpleStringProperty authorsString;
        private SimpleStringProperty title;
        private SimpleStringProperty year;
        private SimpleBooleanProperty selected;

        TableDocument(Document doc) {
            authorsString = new SimpleStringProperty(doc.getAuthorsString());
            title = new SimpleStringProperty(doc.getTitle());
            year = new SimpleStringProperty(doc.getYear());
            selected = new SimpleBooleanProperty(false);

            selected.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedDocs.add(TableDocument.this);
                } else {
                    selectedDocs.remove(TableDocument.this);
                }
            });
        }

        // These function are necessary for the cell value factories for each
        // column in the table.

        public SimpleStringProperty authorsStringProperty() {
            return authorsString;
        }

        public SimpleStringProperty titleProperty() {
            return title;
        }

        public SimpleStringProperty yearProperty() {
            return year;
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }
    }
}
