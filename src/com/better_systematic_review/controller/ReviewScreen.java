package com.better_systematic_review.controller;

import com.better_systematic_review.model.TextExtractionTask;
import com.better_systematic_review.model.PdfFilterService;
import com.better_systematic_review.model.Document;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReviewScreen {

    @FXML private TableView<TableDocument> docsTable;
    @FXML private Label reviewLabel;
    @FXML private CheckBox selectAllCheckBox;
    @FXML private TextField filterTextBox;
    @FXML private ProgressBar filterProgressBar;

    private static final String CONFIRM_DELETE_TITLE = "Delete files";
    private static final String CONFIRM_DELETE = "Are you sure you want to delete these files from the review?";
    private static final String FILTER_RESULT_TITLE = "Filter results";

    private static String labelText;
    private List<TableDocument> selectedDocs = new ArrayList<>();
    private PdfFilterService filterService;
    private final FileChooser fileChooser = new FileChooser();

    public void setDocuments(Collection<Document> docs) {
        if (docs.isEmpty()) {
            return;
        }

        selectAllCheckBox.setDisable(false);
        docsTable.getItems().clear();
        docs.forEach(d -> docsTable.getItems().add(new TableDocument(d)));
    }

    public void addFileInfoToTable(File file) {
        Document newDoc = new Document(new String[0], file.getName(), "None", file);
        TableDocument forTable = new TableDocument(newDoc);
        docsTable.getItems().add(forTable);
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

    private void onFilterCompleted() {
        filterProgressBar.setVisible(false);
        List<TableDocument> docsWithNoResults = filterService.getValue();
        String searchText = filterService.getSearchText();

        if (docsWithNoResults.isEmpty()) {
            String message = "All documents contained a match for \"" + searchText + "\".";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle(FILTER_RESULT_TITLE);
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
            confirmDelete.setTitle(FILTER_RESULT_TITLE);
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

    @FXML
    public void initialize() {
        reviewLabel.setText(labelText);
        filterService = new PdfFilterService();
        filterService.setOnSucceeded(event -> onFilterCompleted());
        filterProgressBar.progressProperty().bind(filterService.progressProperty());
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
    private void addDocument(ActionEvent event) throws IOException {
        File file = fileChooser.showOpenDialog(docsTable.getScene().getWindow());

        if (file == null) {
            return;
        }

        Popup progressPopup = new Popup();
        Parent root = FXMLLoader.load(getClass().getResource("../view/add_file_progress.fxml"));
        progressPopup.getContent().add(root);
        progressPopup.setHideOnEscape(false);

        // There is no way to measure the progress of Apache's text stripper.
        // Setting the progress to a negative number puts the progress bar in
        // the "indeterminate" state, where it just goes back and forth to show
        // that work is being done.
        ProgressBar progressBar = (ProgressBar) root.lookup("#progressBar");
        progressBar.setProgress(-1);

        TextExtractionTask textExtractor = new TextExtractionTask(file);
        textExtractor.setOnFailed(fail -> progressPopup.hide());
        textExtractor.setOnSucceeded(success -> {
            progressPopup.hide();
            addFileInfoToTable(file);
        });

        Thread th = new Thread(textExtractor);
        th.setDaemon(true);
        th.start();
        progressPopup.show(docsTable.getScene().getWindow());
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
        if (filterService.isRunning()) {
            return;
        }

        String searchText = filterTextBox.getText().trim();

        if (searchText.isEmpty()) {
            return;
        }

        filterProgressBar.setVisible(true);
        filterService.setDocs(docsTable.getItems());
        filterService.setSearchText(searchText);
        filterService.reset();
        filterService.start();
    }

    @FXML
    private void editReviewCriteria(ActionEvent event) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner((Stage) docsTable.getScene().getWindow());
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("This screen will be for editing criteria."));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
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

        public File getFile() {
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
