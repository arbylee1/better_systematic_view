package better_systematic_view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;

public class ReviewScreen {

    @FXML private TabPane tabs;
    @FXML private Tab docsTab;
    @FXML private Tab searchTab;
    @FXML private Tab freqTab;
    @FXML private Tab metaAnalysisTab;
    @FXML private TableView<Document> docsTable;
    @FXML private TableColumn<Document, Integer> checkCol;
    @FXML private Button includeButton;
    @FXML private Button excludeButton;
    @FXML private Button deleteButton;
    @FXML private Button addDocButton;
    @FXML private Button importDocsButton;
    @FXML private Button exportDocsButton;
    @FXML private Button excelButton;
    @FXML private TextField filterText;
    @FXML private Label reviewLabel;

    static HashMap<Integer, Document> docs = new HashMap<>();
    static ArrayList<Document> selectedDocs = new ArrayList<>();
    static String labelText;

    @FXML
    public void initialize() {
        checkCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().hashCode()).asObject());
        checkCol.setCellFactory(docList -> new DocumentSelectedCell());
        docsTable.setItems(FXCollections.observableArrayList(docs.values()));
        reviewLabel.setText(labelText);
    }

    @FXML
    private void includeCheckedDocs(ActionEvent event) {

    }

    @FXML
    private void excludeCheckedDocs(ActionEvent event) {
        for (Document doc : selectedDocs) {
            docsTable.getItems().remove(doc);
        }

        selectedDocs.clear();
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
}
