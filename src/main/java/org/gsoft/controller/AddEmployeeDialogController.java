package org.gsoft.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.gsoft.model.Empleado;
import org.gsoft.services.ServicesLocator;

import java.net.URL;
import java.util.ResourceBundle;

public class AddEmployeeDialogController implements Initializable {


    private ObservableList<Empleado> appMainObservableList;

    @FXML
    private TextField nombreTextField;

    @FXML
    private TextField primApellidoTextField;

    @FXML
    private TextField segApellidoTextfield;

    @FXML
    private TextField nifTextfield;

    @FXML
    private TextField numTextfield;

    @FXML
    private JFXButton btnInsert;

    @FXML
    private TextField horasLaborables;

    @FXML
    private JFXComboBox<String> comboEmpresa;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> empresas = FXCollections.observableArrayList(ServicesLocator.getEnterprise().nombreEmpresas());
        comboEmpresa.setItems(empresas);

        horasLaborables.setText("8");
        horasLaborables.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[4-8]*$")) ? change : null));



        btnInsert.setOnAction(this::insertEmployee);

    }

    private void insertEmployee(ActionEvent event) {
        boolean validated = validateData();
        boolean existEmployee = ServicesLocator.getEmployee().getEmployeeByNif(nifTextfield.getText());
            if (!validated) {



            } else if(existEmployee){

            } else{
                Empleado empleado = new Empleado();
                setEmployeeData(empleado);
                ServicesLocator.getEmployee().insertEmployee(empleado);
                resetValues();

                ObservableList<Empleado> employees = FXCollections.observableArrayList(ServicesLocator.getEmployee().listadoEmpleadosModelo());
                appMainObservableList.setAll(employees);
                closeStage(event);
            }


    }

    private void setEmployeeData(Empleado empleado) {
        empleado.setNombre(nombreTextField.getText());
        empleado.setPrimer_apellido(primApellidoTextField.getText());
        empleado.setSegundo_apellido(segApellidoTextfield.getText());
        empleado.setNif(nifTextfield.getText());
        empleado.setNumero_afiliacion(numTextfield.getText());
        int cod_empresa = ServicesLocator.getEnterprise().getEmpresaCodByName(comboEmpresa.getSelectionModel().getSelectedItem());
        empleado.setCod_empresa(cod_empresa);
        empleado.setHoras_laborables(Integer.parseInt(horasLaborables.getText()));
    }

    private boolean validateData() {
        boolean validated = true;
        if (nombreTextField.getText().equalsIgnoreCase("") || nombreTextField.getText().trim().equalsIgnoreCase("")
                || primApellidoTextField.getText().equalsIgnoreCase("") || primApellidoTextField.getText().trim().equalsIgnoreCase("")
                || nifTextfield.getText().equalsIgnoreCase("") || nifTextfield.getText().trim().equalsIgnoreCase("")
                || numTextfield.getText().equalsIgnoreCase("") || numTextfield.getText().trim().equalsIgnoreCase("")
                || horasLaborables.getText().equalsIgnoreCase("") || horasLaborables.getText().trim().equalsIgnoreCase("")
                || comboEmpresa.getSelectionModel().getSelectedIndex() == -1) {
            validated = false;
        }
        return validated;
    }

    private void resetValues() {
        nombreTextField.setText("");
        primApellidoTextField.setText("");
        segApellidoTextfield.setText("");
        nifTextfield.setText("");
        numTextfield.setText("");
        comboEmpresa.getSelectionModel().select(-1);
        horasLaborables.setText("8");
        //populateTable();
    }


    public void setAppMainObservableList(ObservableList<Empleado> employeeObservableList) {
        this.appMainObservableList = employeeObservableList;

    }

    private void closeStage(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void closeModal(KeyEvent event) {
        if(event.getCode() == KeyCode.ESCAPE){
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        }

    }
}
