package cli.ui;

public abstract class BaseScreen {

    protected String title;

    public BaseScreen(String title) {
        this.title = title;
    }

    public abstract void show();

    protected void printBoxTitle() {
        ConsolePrinter.printTitle(title);
    }
}
