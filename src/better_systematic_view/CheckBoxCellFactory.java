package better_systematic_view;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 * This class is a simple factory for CheckBoxTableCells because there is none
 * already available to use in FXML.
 *
 * @param <S> The data type stored in the parent TableView
 * @param <T> The data type represented by the column (and therefore the cell)
 */
public class CheckBoxCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new CheckBoxTableCell<>();
    }
}
