package types;

public enum CloudStoragePlatform {
    GOOGLE_DRIVE("Google Drive"),
    DROPBOX("Dropbox"),
    ONE_DRIVE("OneDrive"),
    BOX("Box"),
    ALL("All");

    private final String description;

    CloudStoragePlatform(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}