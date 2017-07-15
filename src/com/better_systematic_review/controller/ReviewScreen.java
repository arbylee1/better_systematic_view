package com.better_systematic_review.controller;

import com.better_systematic_review.model.TextExtractionTask;
import com.better_systematic_review.model.PdfSearchService;
import com.better_systematic_review.model.Document;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.stage.Popup;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ReviewScreen {

    @FXML private TableView<TableDocument> docsTable;
    @FXML private Label reviewLabel;
    @FXML private CheckBox selectAllCheckBox;
    @FXML private TextField filterTextBox;
    @FXML private ProgressBar filterProgressBar;
    @FXML private CheckBox ignoreCaseCheckBox;
    @FXML private CheckBox regexModeCheckBox;

    private static final String CONFIRM_DELETE_TITLE = "Delete files";
    private static final String CONFIRM_DELETE = "Are you sure you want to delete these files from the review?";
    private static final String FILTER_RESULT_TITLE = "Filter results";
    private static final String DELETE_WHILE_SEARCHING = "You can't delete documents while they are being searched.";
    private static final String SEARCH_FAILED = "Unfortunately the search has failed. Please make sure all documents are closed.";

    private static String labelText;
    private List<TableDocument> selectedDocs = new ArrayList<>();
    private PdfSearchService filterService;
    private final FileChooser fileChooser = new FileChooser();

    public void setDocuments(Collection<Document> docs) {
        if (docs.isEmpty()) {
            return;
        }

        selectedDocs.clear();
        docsTable.getItems().clear();
        docs.forEach(d -> docsTable.getItems().add(new TableDocument(d)));
    }

    public void addFileInfoToTable(File file) {
        Document newDoc = new Document(new String[0], file.getName(), "None", file);
        TableDocument forTable = new TableDocument(newDoc);
        docsTable.getItems().add(forTable);
    }

    private void deleteSelectedDocuments() {
        if (filterService.isRunning()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText(DELETE_WHILE_SEARCHING);
            alert.showAndWait();
            return;
        }

        selectAllCheckBox.setSelected(false);

        if (selectedDocs.size() == docsTable.getItems().size()) {
            selectAllCheckBox.setDisable(true);
        }

        for (TableDocument doc : selectedDocs) {
            docsTable.getItems().remove(doc);
        }

        selectedDocs.clear();
    }

    private void onFilterSucceeded() {
        filterProgressBar.setVisible(false);
        String searchText = filterService.getSearchText();

        Map<TableDocument, Integer> searchResults = filterService.getValue();
        List<TableDocument> docsWithNoResults = searchResults.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

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

    private void onFilterFailed() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(SEARCH_FAILED);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        reviewLabel.setText(labelText);
        filterService = new PdfSearchService();
        filterService.setOnSucceeded(success -> onFilterSucceeded());
        filterService.setOnFailed(fail -> onFilterFailed());
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
        if (docsTable.getItems().isEmpty()) {
            return;
        }

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
    public void ignoreCaseChecked(ActionEvent event) {
        filterService.setIgnoreCase(ignoreCaseCheckBox.isSelected());
    }

    @FXML
    public void regexModeChecked(ActionEvent event) {
        filterService.setRegexMode(regexModeCheckBox.isSelected());
    }

    /**
     * This inner class is used to provide an easy way to represent documents in
     * the TableView on the review screen. It includes the selected property,
     * which allows the review screen to keep track of which documents are
     * selected.
     */
    public class TableDocument {

        private Document document;
        private SimpleStringProperty authorsString;
        private SimpleStringProperty title;
        private SimpleStringProperty year;
        private SimpleBooleanProperty selected;

        TableDocument(Document document) {
            this.document = document;
            authorsString = new SimpleStringProperty(document.getAuthorsString());
            title = new SimpleStringProperty(document.getTitle());
            year = new SimpleStringProperty(document.getYear());
            selected = new SimpleBooleanProperty(false);

            selected.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedDocs.add(TableDocument.this);
                } else {
                    selectedDocs.remove(TableDocument.this);
                }
            });
        }

        public Document getDocument() {
            return document;
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
