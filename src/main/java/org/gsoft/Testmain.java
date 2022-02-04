package org.gsoft;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Testmain extends Application {


        @Override
        public void start(Stage primaryStage) throws Exception{
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/HoldPane.fxml"));

            primaryStage.setTitle("Control de Horarios Palobiofarma S.L & Medibiofarma");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/palobiofarma.png")));
            //primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }


        public static void main(String[] args) {
            launch(args);
        }

}
