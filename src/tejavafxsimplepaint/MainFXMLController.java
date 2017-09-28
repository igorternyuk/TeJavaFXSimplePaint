/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tejavafxsimplepaint;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 *
 * @author igor
 * Last edited 28.09.2017
 */

public class MainFXMLController implements Initializable {
    DrawingMode mode = DrawingMode.PEN;
    private boolean isFirstPointSelected = false;
    private double x1, y1, x3, y3; // x3 and y3 - for parabola by 3 points
     
    @FXML
    private ChoiceBox toolChooser;
    
    @FXML
    private Canvas canvasBottom, canvasTop;
    
    @FXML
    private ColorPicker colorPicker;
    
    @FXML
    private TextField brushSize;
    
    @FXML
    private CheckBox fillShapes;
    
    @FXML
    private CheckBox eraser;
    
    GraphicsContext gcTop, gcBottom;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initialize();
    }   

    public void initialize() {
        gcTop = canvasTop.getGraphicsContext2D();
        gcBottom = canvasBottom.getGraphicsContext2D();
        toolChooser.setItems(FXCollections.observableArrayList(
                "Straight line", "Rectangle", "Square", "Ellipse", "Circle",
                "Polyline", "Pen"));
        toolChooser.setValue("Straight line");
        mode = DrawingMode.STRAIGHT_LINE;
        toolChooser.getSelectionModel().selectedItemProperty().
                addListener((ObservableValue o, Object o1, Object o2) -> {
            switch (o2.toString()) {
                case "Straight line":
                    mode = DrawingMode.STRAIGHT_LINE;
                    break;
                case "Rectangle":
                    mode = DrawingMode.RECTANGLE;
                    break;
                case "Square":
                    mode = DrawingMode.SQUARE;
                    break;
                case "Ellipse":
                    mode = DrawingMode.ELLIPSE;
                    break;
                case "Circle":
                    mode = DrawingMode.CIRCLE;
                    break;
                case "Polyline":
                    mode = DrawingMode.POLYLINE;
                    break;
                case "Pen":
                    mode = DrawingMode.PEN;
                    break;
                default:
                    break;
            }
        });

        canvasTop.setOnMouseClicked(e -> {
           
        });
        
        canvasTop.setOnMouseReleased(e -> {
            if(isFirstPointSelected){
                gcTop.clearRect(0, 0, canvasTop.getWidth(),
                        canvasTop.getHeight());
                double size = Double.parseDouble(brushSize.getText());
                double x2 = e.getX() - size / 2;
                double y2 = e.getY() - size / 2;
                drawFunc(canvasBottom, mode, x2, y2, size, false);
                if(mode == DrawingMode.POLYLINE){
                    x1 = x2;
                    y1 = y2;
                    if(e.getButton() == MouseButton.SECONDARY){
                        isFirstPointSelected = false;
                    }
                }
                else {
                     isFirstPointSelected = false;
                }
                System.out.println("Second point was choosen x1 = " + x1 + 
                        " y1 = " + y1);
            }
            else {
                x1 = e.getX();
                y1 = e.getY();
                isFirstPointSelected = true;
                System.out.println("Initial point was choosen x0 = " + x1 +
                        " y0 = " + y1);
            }

        });
        
        canvasTop.setOnMouseDragged(e -> {
            if(mode == DrawingMode.PEN){
                double size = Double.parseDouble(brushSize.getText());
                double x = e.getX() - size / 2;
                double y = e.getY() - size / 2;
                if(eraser.isSelected()){
                    gcBottom.clearRect(x, y, size, size);
                }
                else {
                    gcBottom.setFill(colorPicker.getValue());
                    gcBottom.fillRect(x, y, size, size);
                }
            }
        });
        
        canvasTop.setOnMouseMoved(e -> {
            if(isFirstPointSelected){
                gcTop.clearRect(0, 0, canvasTop.getWidth(),
                        canvasTop.getHeight());
                double size = Double.parseDouble(brushSize.getText());
                double x2 = e.getX() - size / 2;
                double y2 = e.getY() - size / 2;
                drawFunc(canvasTop, mode, x2, y2, size, true);
            }
        });
    }
    
    private void showErrorMessage(String msg){
        System.out.println(msg);
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Error");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    public void onOpen(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser
                .ExtensionFilter("Select a file (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String filePath = file.toURI().toURL().toString();
                Image img;
                img = new Image(file.toURI().toURL().toString());
                gcBottom.drawImage(img, 0, 0, canvasBottom.getWidth(),
                        canvasBottom.getHeight());
            } catch (MalformedURLException ex) {
                showErrorMessage(ex.toString());
            }
        }
    }
    
    public void onSave(){
        try{
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser
                    .ExtensionFilter("Select a file (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showSaveDialog(null);
            
            Image snapshot = canvasBottom.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null),
                    "png", file != null ? file : new File("snapshot.png"));            
        }
        catch(IOException ex){
            showErrorMessage(ex.toString());
        }
    }
    
    public void onExit(){
        String msg = "Do you really want to exit?";
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.YES,
                ButtonType.NO);
        alert.setTitle("Exit confirmation");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Platform.exit();
            System.exit(0);
        } else if(alert.getResult() == ButtonType.NO){
            alert.close();
        }        
    }
    
    private void drawFunc(Canvas canvas, DrawingMode mode, double x2, double y2,
            double brushSize, boolean isHintingDrawing){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(colorPicker.getValue());
        gc.setStroke(colorPicker.getValue());
        gc.beginPath();
        gc.setLineWidth( brushSize);
        
        if (null != mode) {
            switch (mode) {
                case STRAIGHT_LINE:
                    gc.strokeLine(x1, y1, x2, y2);
                    break;
                case RECTANGLE:
                    if (fillShapes.isSelected()) {
                        gc.fillRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(y1 - y2));
                    } else {
                        gc.strokeRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(y1 - y2));
                    }
                    break;
                case SQUARE:
                    if (fillShapes.isSelected()) {
                        gc.fillRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(x1 - x2));
                    } else {
                        gc.strokeRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(x1 - x2));
                    }
                    break;
                case ELLIPSE:
                    if(fillShapes.isSelected()) {
                        gc.fillOval(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(y1 - y2));
                    } else {
                        gc.strokeOval(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(y1 - y2));
                    }
                    if(isHintingDrawing){
                        gc.setFill(Color.LIGHTBLUE);
                        gc.setLineWidth(1);
                        gc.strokeRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(y1 - y2));
                    }
                    break;
                case CIRCLE:
                    if (fillShapes.isSelected()) {
                        gc.fillOval(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(x1 - x2));
                    } else {
                        gc.strokeOval(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(x1 - x2));
                    }
                    if(isHintingDrawing){
                        gc.setFill(Color.LIGHTBLUE);
                        gc.setLineWidth(1);
                        gc.strokeRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2,
                                Math.abs(x1 - x2), Math.abs(x1 - x2));
                    }
                    break;
                case POLYLINE:
                    gc.strokeLine(x1, y1, x2, y2);
                    break;
                default:
                    break;
            }
        }
        gc.closePath();
    }
}
