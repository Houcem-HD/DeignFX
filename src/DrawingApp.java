import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrawingApp extends Application {

    private List<Shape> shapes = new ArrayList<>();
    private Shape selectedShape = null;
    private double startX, startY;
    private TextArea logArea;
    private ColorPicker colorPicker;
    private Canvas canvas;

    private final TextField widthField = new TextField();
    private final TextField heightField = new TextField();
    private final TextField radiusField = new TextField();

    private final String logFilePath = "/home/houcem-hd/Desktop/IIT/modelisation/workspace/project/PRojectJavaFx/src/log/log.txt";

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        logArea = new TextArea();
        logArea.setPrefWidth(200);
        logArea.setEditable(false);

        Button addCircleBtn = new Button("Add Circle");
        Button addRectBtn = new Button("Add Rectangle");
        Button addTriangleBtn = new Button("Add Triangle");
        Button saveLogBtn = new Button("Save Log");

        colorPicker = new ColorPicker(Color.LIGHTBLUE);

        // Size controls
        Label sizeLabel = new Label("Resize:");
        widthField.setPromptText("Width");
        heightField.setPromptText("Height");
        radiusField.setPromptText("Radius");

        VBox sizeControls = new VBox(5, sizeLabel, widthField, heightField, radiusField);
        sizeControls.setPadding(new Insets(5));

        widthField.setOnAction(e -> updateSize());
        heightField.setOnAction(e -> updateSize());
        radiusField.setOnAction(e -> updateSize());

        VBox controlPane = new VBox(10,
                new HBox(10, addCircleBtn, addRectBtn, addTriangleBtn),
                new HBox(10, saveLogBtn, new Label("Color:"), colorPicker),
                sizeControls,
                logArea);
        controlPane.setPadding(new Insets(10));

        root.setLeft(controlPane);
        root.setCenter(canvas);

        // Add triangle
        addTriangleBtn.setOnAction(e -> {
            Shape triangle = new Triangle(400, 400, 80);
            shapes.add(triangle);
            log("Added triangle");
            redraw(gc);
        });

        // Add circle
        addCircleBtn.setOnAction(e -> {
            Shape circle = new Circle(200, 200, 40);
            shapes.add(circle);
            log("Added circle");
            redraw(gc);
        });

        // Add rectangle
        addRectBtn.setOnAction(e -> {
            Shape rect = new Rectangle(300, 300, 80, 60);
            shapes.add(rect);
            log("Added rectangle");
            redraw(gc);
        });

        // Save log
        saveLogBtn.setOnAction(e -> saveLogToFile());

        // Color picker
        colorPicker.setOnAction(e -> {
            if (selectedShape != null) {
                Color selectedColor = colorPicker.getValue();
                selectedShape.setFillColor(selectedColor);
                log("Changed shape color to: " + selectedColor.toString());
                redraw(gc);
            }
        });

        // Mouse pressed
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            selectedShape = null;
            for (Shape shape : shapes) {
                if (shape.contains(e.getX(), e.getY())) {
                    selectedShape = shape;
                    shape.setSelected(true);
                    startX = e.getX();
                    startY = e.getY();
                } else {
                    shape.setSelected(false);
                }
            }
            updateSizeFields();
            redraw(gc);
        });

        // Mouse dragged
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (selectedShape != null) {
                double dx = e.getX() - startX;
                double dy = e.getY() - startY;
                selectedShape.move(dx, dy);
                startX = e.getX();
                startY = e.getY();
                redraw(gc);
            }
        });

        // Mouse released
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (selectedShape != null) {
                log("Moved shape to (" + selectedShape.getX() + ", " + selectedShape.getY() + ")");
            }
        });

        primaryStage.setTitle("Drawing App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void updateSizeFields() {
        widthField.clear();
        heightField.clear();
        radiusField.clear();

        if (selectedShape instanceof Rectangle rect) {
            widthField.setText(String.valueOf(rect.getWidth()));
            heightField.setText(String.valueOf(rect.getHeight()));
        } else if (selectedShape instanceof Circle circle) {
            radiusField.setText(String.valueOf(circle.getRadius()));
        } else if (selectedShape instanceof Triangle triangle) {
            radiusField.setText(String.valueOf(triangle.getSize()));
        }

    }

    private void updateSize() {
        if (selectedShape instanceof Rectangle rect) {
            try {
                double newWidth = Double.parseDouble(widthField.getText());
                double newHeight = Double.parseDouble(heightField.getText());
                rect.setWidth(newWidth);
                rect.setHeight(newHeight);
                log("Resized rectangle to " + newWidth + "x" + newHeight);
                redrawShapes();
            } catch (NumberFormatException ignored) {
            }
        } else if (selectedShape instanceof Triangle triangle) {
            try {
                double newSize = Double.parseDouble(radiusField.getText());
                triangle.setSize(newSize);
                log("Resized triangle to size " + newSize);
                redrawShapes();
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void redrawShapes() {
        redraw(canvas.getGraphicsContext2D());
    }

    private void redraw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Shape shape : shapes) {
            shape.draw(gc);
        }
    }

    private void log(String msg) {
        logArea.appendText(msg + "\n");
    }

    private void saveLogToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath))) {
            writer.write(logArea.getText());
            log("Log saved to file.");
        } catch (IOException e) {
            log("Error saving log: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
