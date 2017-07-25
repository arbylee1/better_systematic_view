package com.better_systematic_review.controller;

import com.better_systematic_review.Main;
import com.better_systematic_review.model.Document;
import com.better_systematic_review.model.PdfSearchService;
import com.better_systematic_review.model.Review;
import com.better_systematic_review.model.TextExtractionTask;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static Review currentReview;
    private static String labelText;
    private Set<TableDocument> selectedDocs = new HashSet<>();
    private PdfSearchService filterService;
    private final FileChooser fileChooser = new FileChooser();
    private static String filterString;

    void setDocuments() {
        selectedDocs.clear();
        docsTable.getItems().clear();
        if (currentReview.getDocuments() != null) {
            currentReview.getDocuments().forEach(d -> docsTable.getItems().add(new TableDocument(d)));
        }
    }

    private void addFileInfoToReview(File file) {
        String name = file.getName();
        Path filePath = file.toPath();
        Path localPath = Main.getDocumentPath().resolve(file.getName());
        try {
            boolean fileExists = false;
            byte[] loadedContent = Files.readAllBytes(file.toPath());
            for (File document : Main.getDocumentPath().toFile().listFiles()) {
                byte[] existingContent = Files.readAllBytes(document.toPath());
                if (Arrays.equals(loadedContent, existingContent)) {
                    file = document;
                    fileExists = true;
                }
            }
            if (!fileExists) {
                int i = 0;
                Path newPath = localPath;
                String[] tokens = name.split("\\.(?=[^\\.]+$)");
                while(Files.exists(newPath)) {
                    newPath = localPath.getParent().resolve(tokens[0] + "(" + i + ")" + "." + tokens[1]);
                    file = localPath.toFile();
                }
                Files.copy(filePath, newPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document newDoc = new Document(file, name, "None", new String[0]);
        docsTable.getItems().add(new TableDocument(newDoc));
        currentReview.addDocument(newDoc);
    }

    static void setReview(Review review) {
        currentReview = review;
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

        Map<TableDocument, Integer> searchResults = filterService.getValue();
        String searchText = filterService.getSearchText();

        long failedToReadCount = searchResults.values().stream()
                .filter(v -> v == PdfSearchService.FILE_ERROR_FLAG)
                .count();

        if (failedToReadCount > 0) {
            notifyFailedDocuments(failedToReadCount);
        }

        List<TableDocument> docsWithNoMatches = searchResults.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (TableDocument doc: docsWithNoMatches) {
            if (doc.getDocument().getTags().contains(filterString)) {
                docsWithNoMatches.remove(doc);
            }
        }
        if (docsWithNoMatches.isEmpty()) {
            notifyAllDocumentsMatched(searchText);
        } else {
            notifyDocsWithNoMatches(searchText, docsWithNoMatches);
        }
    }

    private void notifyFailedDocuments(long failedToReadCount) {
        String message = failedToReadCount + " documents caused an error and were not searched.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void notifyDocsWithNoMatches(String searchText, List<TableDocument> docsWithNoResults) {
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

    private void notifyAllDocumentsMatched(String searchText) {
        String message = "All documents contained a match for \"" + searchText + "\".";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(FILTER_RESULT_TITLE);
        alert.setContentText(message);
        alert.showAndWait();
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
    private void tagDocument (ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("tag");
        dialog.setTitle("Set tag");
        dialog.setHeaderText("Set tag");
        dialog.setContentText("Please enter your tag:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tag -> {
            for (TableDocument doc: selectedDocs) {
                doc.getDocument().getTags().add(tag);
            }
        }
        );
        currentReview.save();
        setDocuments();
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
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/add_file_progress.fxml"));
        Parent root = loader.load();
        progressPopup.getContent().add(root);
        progressPopup.setHideOnEscape(false);

        // There is no way to measure the progress of Apache's text stripper.
        // Setting the progress to a negative number puts the progress bar in
        // the "indeterminate" state, where it just goes back and forth to show
        // that work is being done.
        ProgressBar progressBar = (ProgressBar) root.lookup("#progressBar");
        progressBar.setProgress(-1);

        TextExtractionTask textExtractor = new TextExtractionTask(file);
        textExtractor.setOnFailed(fail -> {
            progressPopup.hide();
        });
        textExtractor.setOnSucceeded(success -> {
            progressPopup.hide();
            addFileInfoToReview(file);
    });

        progressPopup.show(docsTable.getScene().getWindow());
        startDaemonThread(textExtractor);
    }

    private void startDaemonThread(Runnable target) {
        Thread th = new Thread(target);
        th.setDaemon(true);
        th.start();
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

        if (docsTable.getItems().isEmpty()) {
            return;
        }

        String searchText = filterTextBox.getText().trim();

        if (searchText.isEmpty()) {
            return;
        }
        filterString = searchText;
        filterProgressBar.setVisible(true);

        filterService.setDocs(docsTable.getItems());
        filterService.setSearchText(searchText);
        filterService.setIgnoreCase(ignoreCaseCheckBox.isSelected());
        filterService.setRegexMode(regexModeCheckBox.isSelected());

        filterService.reset();
        filterService.start();
    }

    @FXML
    private void editReviewCriteria(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/criteria.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 700, 500);

        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(docsTable.getScene().getWindow());
        dialog.setScene(scene);
        dialog.show();
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
        private SimpleStringProperty tagString;

        TableDocument(Document document) {
            this.document = document;
            authorsString = new SimpleStringProperty(document.getAuthorsString());
            title = new SimpleStringProperty(document.getTitle());
            year = new SimpleStringProperty(document.getYear());
            selected = new SimpleBooleanProperty(false);
            if(document.getTags().size() > 0) {
                String[] tags = new String[document.getTags().size()];
                document.getTags().toArray(tags);
                tagString = new SimpleStringProperty(String.join(",", tags));
            } else {
                tagString = new SimpleStringProperty("");
            }
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

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }

            if (!(other instanceof TableDocument)) {
                return false;
            }

            if (this == other) {
                return true;
            }

            TableDocument that = (TableDocument) other;
            return this.document.equals(that.document);
        }

        @Override
        public int hashCode() {
            return document.hashCode();
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

        public SimpleStringProperty tagStringProperty() {
            return tagString;
        }

    }
}
