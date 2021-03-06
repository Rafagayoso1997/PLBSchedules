package org.gsoft.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.gsoft.App;
import org.gsoft.model.Empleado;
import org.gsoft.model.Empresa;
import org.gsoft.services.ServicesLocator;


import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class MainMenuController implements Initializable {
    private final String LEAP_YEAR = "config_files" + File.separator + "Schedule Model Leap Year.xlsx";
    private final String REGULAR = "config_files" + File.separator + "Schedule Model Regular Year.xlsx";
    private final String HELP = "config_files" + File.separator + "Control de horarios Palobiofarma S,L & Medibiofarma.pdf";
    private final String PALOBIOFARMA = "config_files" + File.separator + "palobiofarma.png";
    private final String MEDIBIOFARMA = "config_files" + File.separator + "medibiofarma.png";

    @FXML
    private Label resultLabel;

    @FXML
    private JFXButton fileMenu;


    @FXML
    private ProgressBar progressBar;

    @FXML
    private AnchorPane root;

    @FXML
    private Pane dropInstructions;

    private ArrayList<File> listFiles;

    private ImageView[] slides;


    private File file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.requestFocus();
        fileMenu.setOnAction(event -> generateSchedule());
        makeTextAreaDragTarget(root);

        listFiles = new ArrayList<>();
        getSlides();
        createSlideShow();
        resultLabel.setText("");

        progressBar.setProgress(0);
    }

    private void makeTextAreaDragTarget(Node node) {
        node.setOnDragOver(event -> event.acceptTransferModes(TransferMode.COPY));

        node.setOnDragExited(event -> dropInstructions.setVisible(false));

        node.setOnDragEntered(event -> dropInstructions.setVisible(true));

        node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if(event.getDragboard().hasFiles()){
                fileLoaderTask(db.getFiles().get(0)).run();
            }

            dropInstructions.setVisible(false);
        });
    }

    private Task<String> fileLoaderTask(File fileToLoad){
        //Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task() {
            @Override
            protected String call() {
                file = fileToLoad;
                listFiles.add(file);
                if(file.getName().contains("Mat")){
                    mergeExcel(1, System.getProperty("user.home") + "/Desktop");
                }else{
                    mergeExcel(2, System.getProperty("user.home") + "/Desktop");
                    mergeExcel(3, System.getProperty("user.home") + "/Desktop");
                }

                return fileToLoad.getAbsolutePath();
            }
        };

        //If successful, update the text area, display a success message and store the loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {

            try {
                System.out.println(loadFileTask.get());
                listFiles = new ArrayList<>();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            }
        });

        //If unsuccessful, set text area with error message and status message to failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            //textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            System.out.println("No s?? pudo cargar");
        });

        return loadFileTask;
    }

    private void createSlideShow() {

        SequentialTransition slideshow = new SequentialTransition();
        for (ImageView slide : slides) {

            SequentialTransition sequentialTransition = new SequentialTransition();

            FadeTransition fadeIn = getFadeTransition(slide, 0.0, 1.0);
            PauseTransition stayOn = new PauseTransition(Duration.millis(2000));
            FadeTransition fadeOut = getFadeTransition(slide, 1.0, 0.0);

            sequentialTransition.getChildren().addAll(fadeIn, stayOn, fadeOut);
            slide.setOpacity(0);
            slide.setY(75);
            this.root.getChildren().add(slide);
            slideshow.getChildren().add(sequentialTransition);

        }

        slideshow.play();
    }

    private FadeTransition getFadeTransition(ImageView imageView, double fromValue, double toValue) {

        FadeTransition ft = new FadeTransition(Duration.millis(2000), imageView);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);
        return ft;
    }

    private void getSlides() {
        try{
            slides = new ImageView[100];

            Image image1 = new Image(new FileInputStream(PALOBIOFARMA));
            Image image2 = new Image(new FileInputStream(MEDIBIOFARMA));

            for (int i = 0; i < 100; i++) {
                if (i % 2 == 0) {
                    slides[i] = new ImageView(image1);
                } else {
                    slides[i] = new ImageView(image2);
                }

                slides[i].setFitHeight(200);
                slides[i].setFitWidth(794);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void generateSchedule(){
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();

        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Documento Excel", "*xlsx"));
        file = fc.showOpenDialog(stage);

        if (file == null) {

        }
        else{
            listFiles.add(file);
            if(file.getName().contains("Mat")){
                mergeExcel(1, System.getProperty("user.home") + "/Desktop");

            }else{
                mergeExcel(2, System.getProperty("user.home") + "/Desktop");
                mergeExcel(3, System.getProperty("user.home") + "/Desktop");
            }

        }


    }

    public void mergeExcel(int cod_empresa, String ruta) {

        try {

            Empresa empresa = ServicesLocator.getEnterprise().getEmpresaByCod(cod_empresa);
            ArrayList<Empleado> lista = ServicesLocator.getEmployee().listadoEmpleadosXEmpresa(empresa.getNombre());

            String [] nombre = this.file.getName().split(" ");
            int year = Integer.parseInt(nombre[0]);
            File file;
            if(year%4 !=0 ) file = new File(REGULAR);
            else file = new File(LEAP_YEAR);

            listFiles.add(file);

            File foto1 = new File(PALOBIOFARMA);
            listFiles.add(foto1);

            File foto2 = new File(MEDIBIOFARMA);
            listFiles.add(foto2);

            Task<Void> longTask = new Task<Void>() {
                @Override
                protected Void call() {
                    Controller controller = new Controller();
                    controller.mergeExcelFiles(lista, empresa, listFiles, ruta);
                    return null;
                }
            };


            longTask.setOnRunning(event -> {
                resultLabel.setText("Generando horarios de los trabajadores");
                progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS); //el progressbar este esta al berro
            });

            longTask.setOnSucceeded(event -> {
                progressBar.setProgress(100);
                resultLabel.setText("Horarios generados satisfactoriamente");
                listFiles = new ArrayList<>();

            });

            new Thread(longTask).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void showEmployeesData() {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(MainMenuController.class.getResource("/fxml/EmployesManagement.fxml"));
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Gestionar Empleados");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            /*dialogStage.setMinHeight(dialogStage.getMinHeight());
            dialogStage.setMinWidth(dialogStage.getMinWidth());*/
            //dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/palobiofarma.png")));
            //dialogStage.setAlwaysOnTop(true);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();
            //App.setRoot("fxml/EmployesManagement");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void showHelp() throws IOException {
        File file = new File(HELP);

        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        //let's try to open PDF file
        if(file.exists()) {
            desktop.open(file);
        }
    }

    @FXML
    void executeKeyboardShortcuts(KeyEvent event) throws IOException{
        KeyCode code = event.getCode();
        if(code == KeyCode.A){
            showHelp();
        }
        else if(code == KeyCode.G){
            generateSchedule();
        }
        else if(code == KeyCode.E){
            showEmployeesData();
        }
    }

}
