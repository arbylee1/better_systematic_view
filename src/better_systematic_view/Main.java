package better_systematic_view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("better_systematic_view.fxml"));
        primaryStage.setTitle("A Better Systematic Review");
        primaryStage.setWidth(300);
        primaryStage.setHeight(500);

        final Label label = new Label("Reviews");
        label.setFont(new Font("Arial", 20));

        TableView table = new TableView();
        table.setEditable(true);

        TableColumn nameCol = new TableColumn("Name");
        TableColumn iDCol = new TableColumn("ID");
        TableColumn loginCol = new TableColumn("Last Login");

        table.getColumns().addAll(nameCol, iDCol, loginCol);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}