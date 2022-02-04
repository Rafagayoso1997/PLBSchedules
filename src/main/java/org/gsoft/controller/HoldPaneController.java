package org.gsoft.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.gsoft.model.Empleado;
import org.gsoft.services.ServicesLocator;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class HoldPaneController implements Initializable {

    @FXML
    private TabPane tabPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            LocalDateTime dateTime = LocalDateTime.now();
            int month = dateTime.getMonth().getValue();
            Empleado empleado = ServicesLocator.getEmployee().getEmployeeById(2);
            String TEST = empleado.getDireccionCronograma();
            int i =1;
            for(Tab tab : tabPane.getTabs()){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/TestExcelTable.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                TestExcelTableController test  = fxmlLoader.getController();
                test.setData(TEST, i);
                tab.setContent(anchorPane);
                if(i > month) tab.setDisable(true);
                i++;

            }
        }catch (Exception E){
            E.printStackTrace();
        }
    }
}
