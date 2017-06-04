package better_systematic_view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

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

    @FXML
    public void initialize() {
        System.out.println("INITIALIZE");

        checkCol.setCellValueFactory(new PropertyValueFactory<Document, Integer>("id"));
        checkCol.setCellFactory(docList -> new DocumentSelectedCell());
    }

    @FXML
    private void includeCheckedDocs(ActionEvent event) {
        Document[] docsArray = new Document[3];
        docsArray[0] = new Document(new String[] {"David Thomson"}, "How stuff " +
                "works", "2020", 1);
        docsArray[1] = new Document(new String[] {"Vince Nguyen"}, "Cats cats " +
                "cats!", "2020", 2);
        docsArray[2] = new Document(new String[] {"Vince Nguyen"}, "Walking back " +
                "from strange places", "2021", 3);

        ObservableList<Document> docs = FXCollections.observableArrayList(docsArray);
        docsTable.setItems(docs);
    }

    @FXML
    private void excludeCheckedDocs(ActionEvent event) {

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
