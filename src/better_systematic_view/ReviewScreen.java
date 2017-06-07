package better_systematic_view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReviewScreen {

    @FXML private TabPane tabs;
    @FXML private Tab docsTab;
    @FXML private Tab searchTab;
    @FXML private Tab freqTab;
    @FXML private Tab metaAnalysisTab;
    @FXML private TableView<TableDocument> docsTable;
    @FXML private TableColumn<TableDocument, Boolean> checkCol;
    @FXML private Button includeButton;
    @FXML private Button excludeButton;
    @FXML private Button deleteButton;
    @FXML private Button addDocButton;
    @FXML private Button importDocsButton;
    @FXML private Button exportDocsButton;
    @FXML private Button excelButton;
    @FXML private TextField filterText;
    @FXML private Label reviewLabel;
    @FXML private CheckBox selectAllCheckBox;

    private static String labelText;
    private List<TableDocument> selectedDocs = new ArrayList<>();

    public void setDocuments(Collection<Document> docs) {
        docsTable.getItems().clear();
        docs.forEach(d -> docsTable.getItems().add(new TableDocument(d)));
    }

    @FXML
    public void initialize() {
        // There is no way to set the cell factory for checkbox cells directly
        // in FXML
        checkCol.setCellFactory(CheckBoxTableCell.forTableColumn(checkCol));
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
        for (TableDocument doc : selectedDocs) {
            docsTable.getItems().remove(doc);
        }

        selectedDocs.clear();
        selectAllCheckBox.setSelected(false);
    }

    @FXML
    private void deleteCheckedDocs(ActionEvent event) {

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
