import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Triangle extends Shape {
    private double size;

    public Triangle(double x, double y, double size) {
        super(x, y);
        this.size = size;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double newSize) {
        this.size = newSize;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double[] xPoints = { x, x - size / 2, x + size / 2 };
        double[] yPoints = { y - size / 2, y + size / 2, y + size / 2 };

        gc.setFill(fillColor);
        gc.fillPolygon(xPoints, yPoints, 3);

        if (selected) {
            gc.setStroke(Color.RED);
            gc.strokePolygon(xPoints, yPoints, 3);
        }
    }

    @Override
    public boolean contains(double mx, double my) {
        // Simple bounding box check for click detection
        return mx >= x - size / 2 && mx <= x + size / 2 &&
               my >= y - size / 2 && my <= y + size / 2;
    }

    @Override
    public Bounds getBounds() {
        return new BoundingBox(x - size / 2, y - size / 2, size, size);
    }
}
