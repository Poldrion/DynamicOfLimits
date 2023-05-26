package dynamics.utils;

import static dynamics.utils.TitlesUtils.*;

public enum Contents {
    Main(MAIN_CONTENT_TITLE),
    Details(DETAILS_CONTENT_TITLE),
    BusinessPlan(BUSINESS_PLAN_CONTENT_TITLE);

    private String title;

    Contents(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getFxml() {
        return String.format("views/%s.fxml", name());
    }
}
