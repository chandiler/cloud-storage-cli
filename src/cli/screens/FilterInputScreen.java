package cli.screens;

import cli.ui.BaseScreen;
import cli.utils.InputReader;
import filter.UserFilter;
import model.UserRequest;

public class FilterInputScreen extends BaseScreen {

    public FilterInputScreen() {
        super("Filter Options");
    }

    public UserRequest showAndGetResult() {
        printBoxTitle();

        boolean useFilter = InputReader.readYesNo("Would you like to narrow results using budget & storage filters?");
        if (useFilter) {
            return new UserFilter().collect();
        }

        return new UserRequest();
    }

    @Override
    public void show() {
        // No-op
    }
}
