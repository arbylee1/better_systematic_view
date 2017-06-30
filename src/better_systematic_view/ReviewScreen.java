package better_systematic_view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jpedal.examples.text.FindTextInRectangle;
import org.jpedal.exception.PdfException;
import org.jpedal.grouping.SearchType;

public class ReviewScreen {

    @FXML private TableView<TableDocument> docsTable;
    @FXML private Label reviewLabel;
    @FXML private CheckBox selectAllCheckBox;
    @FXML private TextField filterTextBox;
    @FXML private Label searchingProgressLabel;

    private static final String CONFIRM_DELETE_TITLE = "Delete files";
    private static final String CONFIRM_DELETE = "Are you sure you want to delete these files from the review?";

    private static String labelText;
    private List<TableDocument> selectedDocs = new ArrayList<>();

    private final FileChooser fileChooser = new FileChooser();

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
        File file = fileChooser.showOpenDialog(docsTable.getScene().getWindow());

        if (file != null) {
            Document newDoc = new Document(new String[0], file.getName(), "None", file);
            TableDocument forTable = new TableDocument(newDoc);
            docsTable.getItems().add(forTable);
        }
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

    @FXML
    private void filter(ActionEvent event) throws Exception {
        searchingProgressLabel.setVisible(true);
        String searchText = filterTextBox.getText();
        List<TableDocument> docsWithNoResults = new ArrayList<>();

        Task searchPDFTask = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (TableDocument doc : docsTable.getItems()) {
                    String path = doc.getFile().getAbsolutePath();
                    FindTextInRectangle extract = new FindTextInRectangle(path);

                    boolean hit = false;

                    try {
                        if (extract.openPDFFile()) {
                            int pageCount = extract.getPageCount();
                            for (int page = 1; page <= pageCount; page++) {
                                float[] resultsThisPage = extract.findTextOnPage(page, searchText, SearchType.MUTLI_LINE_RESULTS);

                                if (resultsThisPage.length > 0) {
                                    hit = true;
                                    break;
                                }
                            }
                        }
                    } catch (PdfException e) {
                        e.printStackTrace();
                    }

                    if (!hit) {
                        docsWithNoResults.add(doc);
                    }
                }

                return null;
            }

            @Override
            public void succeeded() {
                searchingProgressLabel.setVisible(false);

                if (docsWithNoResults.isEmpty()) {
                    String message = "All documents contained a match for \"" + searchText + "\".";

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Filter results");
                    alert.setContentText(message);

                    alert.showAndWait();
                } else {
                    String message = "There" +
                            (docsWithNoResults.size() == 1 ? " was " : " were ") +
                            docsWithNoResults.size() +
                            (docsWithNoResults.size() == 1 ? " document " : " documents ") +
                            "with no matches for \"" +
                            searchText +
                            "\". Would you like to delete" +
                            (docsWithNoResults.size() == 1 ? " this" : " these") +
                            (docsWithNoResults.size() == 1 ? " document " : " documents ") +
                            "from the review?";

                    Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDelete.setHeaderText(null);
                    confirmDelete.setTitle("Filter results");
                    confirmDelete.setContentText(message);

                    confirmDelete.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            for (TableDocument doc : docsWithNoResults) {
                                docsTable.getItems().remove(doc);
                            }
                        }
                    });
                }
            }
        };

        (new Thread(searchPDFTask)).start();
    }

    /**
     * This inner class is used to provide an easy way to represent documents in
     * the TableView on the review screen. It includes the selected property,
     * which allows the review screen to keep track of which documents are
     * selected.
     */
    public class TableDocument {

        private File file;
        private SimpleStringProperty authorsString;
        private SimpleStringProperty title;
        private SimpleStringProperty year;
        private SimpleBooleanProperty selected;

        TableDocument(Document doc) {
            file = doc.getFile();
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

        File getFile() {
            return file;
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
