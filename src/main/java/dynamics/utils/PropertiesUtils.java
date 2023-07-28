package dynamics.utils;

import dynamics.model.entities.Department;
import dynamics.views.controllers.common.ErrorDialog;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static dynamics.utils.FilePathConstants.APPLICATION_PROPERTIES_FILE;

public class PropertiesUtils {

    public static Integer YEAR;
    public static Department CurrentDepartment;

    public static List<Integer> GetYears() {
        Properties properties = new Properties();
        List<Integer> years = new ArrayList<>();
        try {
            properties.load(new FileReader(APPLICATION_PROPERTIES_FILE));
            for (int i = 0; i < Integer.parseInt(properties.getProperty("countYears")); i++) {
                years.add(Integer.parseInt(properties.getProperty("firstYear")) + i);
            }
        } catch (IOException e) {
            ErrorDialog.ErrorDialogBuilder.builder().title(TitleConstants.ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
        }
        return years;
    }

    public static Integer getYearLimit() {
        return YEAR;
    }

    public static Integer getFirstYear() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(APPLICATION_PROPERTIES_FILE));
        } catch (IOException e) {
            ErrorDialog.ErrorDialogBuilder.builder().title(TitleConstants.ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
        }
        return Integer.parseInt(properties.getProperty("firstYear"));
    }
}
