package better_systematic_view;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

public class CheckBoxCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new CheckBoxTableCell<>();
    }
}
