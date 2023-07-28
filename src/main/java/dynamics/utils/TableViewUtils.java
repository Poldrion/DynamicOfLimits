package dynamics.utils;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import static dynamics.utils.TitleConstants.SETTING_PLACEHOLDER;

public class TableViewUtils {

    public static void SettingPlaceholder(TableView<?> tableView) {
        tableView.setPlaceholder(new Label(SETTING_PLACEHOLDER));
    }


}
