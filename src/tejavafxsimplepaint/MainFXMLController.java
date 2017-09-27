/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tejavafxsimplepaint;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author igor
 * Last edited 27.09.2017
 */
public class MainFXMLController implements Initializable {
    private boolean isFirstPointSelected = false;
    private double x0, y0;
    @FXML
    private Canvas canvasBottom, canvasTop;
    
    @FXML
    private ColorPicker colorPicker;
    
    @FXML
    private TextField brushSize;
    
    @FXML
    private CheckBox eraser;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initialize();
    }

    public void initialize(){
        GraphicsContext g = canvasTop.getGraphicsContext2D();
        
        canvasTop.setOnMouseClicked(e -> {
           
        });
        canvasTop.setOnMouseReleased(e -> {
            if(isFirstPointSelected){
                GraphicsContext gc = canvasBottom.getGraphicsContext2D();
                gc.setFill(colorPicker.getValue());
                gc.setStroke(colorPicker.getValue());
                gc.beginPath();
                double size = Double.parseDouble(brushSize.getText());
                gc.setLineWidth(size);
                gc.moveTo(x0, y0);
                double x1 = e.getX() - size / 2;
                double y1 = e.getY() - size / 2;
                gc.lineTo(x1, y1);
                gc.stroke();
                gc.closePath();
                isFirstPointSelected = false;
                System.out.println("Second point was choosen x1 = " + x0 + 
                        " y1 = " + y0);
            }
            else {
                x0 = e.getX();
                y0 = e.getY();
                isFirstPointSelected = true;
                System.out.println("Initial point was choosen x0 = " + x0 +
                        " y0 = " + y0);
            }

        });
        canvasTop.setOnMouseMoved(e -> {
            if(isFirstPointSelected){
                GraphicsContext gc = canvasTop.getGraphicsContext2D();
                gc.setStroke(colorPicker.getValue());
                gc.setFill(colorPicker.getValue());
                gc.clearRect(0, 0, 600, 600);
                gc.beginPath();
                double size = Double.parseDouble(brushSize.getText());
                gc.setLineWidth(size);
                gc.moveTo(x0, y0);
                double x1 = e.getX() - size / 2;
                double y1 = e.getY() - size / 2;
                gc.lineTo(x1, y1);
                gc.stroke();
                gc.closePath();
            }

            /*double size = Double.parseDouble(brushSize.getText());
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;
            if(eraser.isSelected()){
                g.clearRect(x, y, size, size);
            }
            else {
                g.setFill(colorPicker.getValue());
                g.fillRect(x, y, size, size);
            }*/
        });
    }    
    
    public void onSave(){
        try{
            //Here must be file chooser
            Image snapshot = canvasTop.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null),
                    "png", new File("shapshot.png"));            
        }
        catch(IOException e){
            //Here must be alert
            System.out.println("Failed to save image");
        }
    }
    
    public void onExit(){
        Platform.exit();
    }
}
