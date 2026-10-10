package castlecrashers.ui;

import castlecrashers.character.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import java.nio.file.Path;
import java.util.*;

/** Builds resizable frames from the pack's separated nine-slice sheets. */
public final class TinyUi {
    private static final Map<String, Image> images = new HashMap<>();
    private static final Map<String, Image> frames = new HashMap<>();

    private TinyUi() {
    }

    // Shared by health bars, portraits, book, cursor, and letters to load and cache
    // UI images.
    public static Image image(String name) {
        return images.computeIfAbsent(name, key -> {
            String relative = "images/ui/" + key;
            var resource = TinyUi.class.getResource("/" + relative);
            Image image = new Image(resource != null ? resource.toExternalForm()
                    : Path.of("resources", relative).toUri().toString(), false);
            if (image.isError())
                throw new IllegalStateException("Missing UI asset: " + relative, image.getException());
            return image;
        });
    }

    // Join nine separated sprite pieces into a border that can stretch around
    // buttons and panels.
    private static Image assemble(String name, int[] starts, int[] sizes) {
        return frames.computeIfAbsent(name, key -> {
            Image sheet = image(name);
            int total = Arrays.stream(sizes).sum();
            WritableImage result = new WritableImage(total, total);
            int dy = 0;
            for (int y = 0; y < 3; y++) {
                int dx = 0;
                for (int x = 0; x < 3; x++) {
                    result.getPixelWriter().setPixels(dx, dy, sizes[x], sizes[y], sheet.getPixelReader(),
                            starts[x], starts[y]);
                    dx += sizes[x];
                }
                dy += sizes[y];
            }
            return result;
        });
    }

    private static Border border(Image image, double width, double slice, boolean fill) {
        return new Border(new BorderImage(image, new BorderWidths(width), Insets.EMPTY,
                new BorderWidths(slice), fill, BorderRepeat.STRETCH, BorderRepeat.STRETCH));
    }

    public static Border wood() {
        return wood(20);
    }

    public static Border wood(double width) {
        return border(
                assemble("Wood Table/WoodTable.png", new int[] { 40, 192, 320 }, new int[] { 88, 64, 88 }),
                width, 88, false);
    }

    public static Border paper() {
        return border(
                assemble("Papers/RegularPaper.png", new int[] { 0, 128, 256 }, new int[] { 64, 64, 64 }), 10,
                64, true);
    }

    public static void styleButton(Button button) {
        Runnable refresh = () -> {
            if (button.getStyleClass().contains("key")) {
                // Blank buttons from the middle brown column retain the game's feedback colors.
                boolean feedback = button.getStyleClass().stream()
                        .anyMatch(s -> s.equals("correct") || s.equals("present") || s.equals("absent"));
                String state = button.isArmed() ? "down" : "up";
                Image frame = frames.computeIfAbsent("wooden-key-" + state, key ->
                        new WritableImage(image("wooden/sheet.png").getPixelReader(),
                                418, button.isArmed() ? 1088 : 1056, 92, 32));
                button.setBorder(border(frame, 3, 6, !feedback));
                return;
            }
            Image frame = woodenButton();
            button.setBorder(border(frame, 8, frame.getHeight() / 3, true));
            button.setTranslateY(button.isArmed() ? 2 : 0);
        };
        button.armedProperty().addListener((o, a, b) -> refresh.run());
        button.getStyleClass()
                .addListener((javafx.collections.ListChangeListener<String>) c -> refresh.run());
        refresh.run();
    }

    // Trim only transparent canvas padding once; nine-slice borders keep corners intact.
    private static Image woodenButton() {
        return frames.computeIfAbsent("generated-wooden-button", key -> {
            Image source = image("wooden/button.png");
            PixelReader pixels = source.getPixelReader();
            int minX = (int) source.getWidth(), minY = (int) source.getHeight();
            int maxX = -1, maxY = -1;
            for (int y = 0; y < source.getHeight(); y++) {
                for (int x = 0; x < source.getWidth(); x++) {
                    if ((pixels.getArgb(x, y) >>> 24) > 16) {
                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                    }
                }
            }
            if (maxX < minX) throw new IllegalStateException("Wooden button artwork is empty");
            return new WritableImage(pixels, minX, minY, maxX - minX + 1, maxY - minY + 1);
        });
    }

    // The same pointed hanging banner occupies this viewport in all five sheets.
    public static ImageView banner(UnitColor color) {
        ImageView banner = new ImageView(image("banners/" + color.name().toLowerCase(Locale.ROOT) + ".png"));
        banner.setViewport(new Rectangle2D(49, 0, 30, 48));
        banner.setFitWidth(120);
        banner.setFitHeight(192);
        banner.setSmooth(false);
        banner.setAccessibleText(color + " faction banner");
        return banner;
    }

    public static ImageView portrait(UnitColor color, CharacterClass type) {
        int group = switch (color) {
            case BLUE -> 0;
            case RED -> 5;
            case YELLOW -> 10;
            case PURPLE -> 15;
            case BLACK -> 20;
        };
        int offset = switch (type) {
            case WARRIOR -> 1;
            case LANCER -> 2;
            case ARCHER -> 3;
            case MONK -> 5;
        };
        ImageView avatar = new ImageView(
                image(String.format(Locale.ROOT, "Human Avatars/Avatars_%02d.png", group + offset)));
        avatar.setViewport(new Rectangle2D(12, 12, 232, 232));
        avatar.setFitWidth(76);
        avatar.setFitHeight(76);
        avatar.setSmooth(false);
        return avatar;
    }
}
