package castlecrashers.character;

// Folder names to load the spritesheets in whats needed.
public enum UnitColor {
    BLUE("Blue"), RED("Red"), PURPLE("Purple"), YELLOW("Yellow"), BLACK("Black");

    private final String folder;

    UnitColor(String folder) {
        this.folder = folder;
    }

    public String folder() {
        return folder;
    }

    @Override
    public String toString() {
        return folder;
    }
}
