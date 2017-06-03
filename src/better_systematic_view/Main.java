package better_systematic_view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Setting JavaFX Stage
        primaryStage.setTitle("A Better Systematic Review");
        primaryStage.setWidth(300);
        primaryStage.setHeight(500);

        //Creating table and adding data
        final Label label = new Label("Reviews");
        label.setFont(new Font("Arial", 20));

        TableView<Review> table = new TableView<Review>();
        table.setEditable(true);

        ObservableList<Review> data =
                FXCollections.observableArrayList(
                        new Review("Review 1", "1", "6/4/2016")
                );

        TableColumn nameCol = new TableColumn("Name");
        TableColumn iDCol = new TableColumn("ID");
        TableColumn loginCol = new TableColumn("Last Login");

        nameCol.setCellValueFactory(new PropertyValueFactory<Review,String>("name"));
        iDCol.setCellValueFactory(new PropertyValueFactory<Review,String>("id"));
        loginCol.setCellValueFactory(new PropertyValueFactory<Review,String>("lastLogin"));

        table.setItems(data);
        table.getColumns().addAll(nameCol, iDCol, loginCol);

        //Adding text fields and event handling
        HBox hb = new HBox();

        final TextField addName = new TextField();
        addName.setPromptText("Name");
        addName.setMaxWidth(nameCol.getPrefWidth());
        final TextField addID = new TextField();
        addID.setMaxWidth(iDCol.getPrefWidth());
        addID.setPromptText("ID");
        final TextField addLastLogin = new TextField();
        addLastLogin.setMaxWidth(loginCol.getPrefWidth());
        addLastLogin.setPromptText("Last Login");

        final Button addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                data.add(new Review(
                        addName.getText(),
                        addID.getText(),
                        addLastLogin.getText()));
                addName.clear();
                addID.clear();
                addLastLogin.clear();
            }
        });

        hb.getChildren().addAll(addName, addID, addLastLogin, addButton);
        hb.setSpacing(3);

        //Setting scene and adding to stage
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, hb);

        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}