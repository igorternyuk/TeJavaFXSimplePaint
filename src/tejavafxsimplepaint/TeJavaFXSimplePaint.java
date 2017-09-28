/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tejavafxsimplepaint;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author igor
 * Last edited 28.09.2017
 */
public class TeJavaFXSimplePaint extends Application {
    private static final String TITLE_OF_PROGRAM = "TeSimplePaint";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFXML.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        ((MainFXMLController)loader.getController())
                .bindResizeEventOfSceneToCanvas();       
        stage.setTitle(TITLE_OF_PROGRAM);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
