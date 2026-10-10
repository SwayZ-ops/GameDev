package castlecrashers.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;

/** Tiny Swords frame remains fixed while the red fill shrinks with HP. */
public final class HealthBar extends Region {
    private final Canvas canvas = new Canvas();
    private final Image frame = TinyUi.image("Bars/BigBar_Base.png");
    private final Image fill = TinyUi.image("Bars/BigBar_Fill.png");
    private double progress = 1;

    public HealthBar() {
        setMinSize(100, 30);
        setPrefSize(220, 36);
        setMaxHeight(36);
        getChildren().add(canvas);
        setMouseTransparent(true);
        setAccessibleRole(javafx.scene.AccessibleRole.PROGRESS_INDICATOR);
    }

    // GameView passes HP as a fraction from zero to one and this requests a redraw
    // of the fill.
    public void setProgress(double value) {
        progress = Math.max(0, Math.min(1, value));
        setAccessibleText("Health " + Math.round(progress * 100) + " percent");
        requestLayout();
    }

    public double getProgress() {
        return progress;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight(), edge = h * 24 / 64;
        canvas.setWidth(w);
        canvas.setHeight(h);
        var g = canvas.getGraphicsContext2D();
        g.setImageSmoothing(false);
        g.drawImage(frame, 40, 0, 24, 64, 0, 0, edge, h);
        g.drawImage(frame, 128, 0, 64, 64, edge, 0, w - 2 * edge, h);
        g.drawImage(frame, 256, 0, 24, 64, w - edge, 0, edge, h);
        double start = h * 18 / 64, end = w - start;
        if (progress > 0)
            g.drawImage(fill, 0, 20, 64, 24, start, h * 20 / 64, (end - start) * progress, h * 24 / 64);
    }
}
