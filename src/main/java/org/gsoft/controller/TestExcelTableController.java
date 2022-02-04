package org.gsoft.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gsoft.model.TableExcelModel;
import org.gsoft.utils.CustomCell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class TestExcelTableController /*implements Initializable */{

    private final String REGULAR = "config_files" + File.separator + "Schedule Model Regular Year.xlsx";
    private final String TEST = System.getProperty("user.home") + "/Desktop" + File.separator +
            "Palobiofarma, S.L Mataró"+ File.separator +"Ivette Amalfi.xlsx";
    private final String WEEKEND_COLOR = "FFFFFFCC";
    private final String AUTONOMIC_COLOR = "FF92D050";
    private final String LOCAL_COLOR = "FF00B0F0";
    private final String NATIONAL_COLOR = "FFFF0000";
    private final int SHEET_NUMBER = 1;
    private DateFormat inFormat;

    @FXML
    private JFXButton saveButton;

    @FXML
    private TableView<TableExcelModel> excelTable;

    @FXML
    private TableColumn<TableExcelModel, String> dayColumn;

    @FXML
    private TableColumn<TableExcelModel, String> entryColumn;

    @FXML
    private TableColumn<TableExcelModel, String> exitColumn;

    @FXML
    private TableColumn<TableExcelModel, String> journalColumn;

    public void setData(String address, int sheet){
        inFormat = new SimpleDateFormat( "hh:mm");

        saveButton.setOnAction(actionEvent -> saveData(address, sheet));



        dayColumn.setCellValueFactory(cellData -> cellData.getValue().dayProperty());

        entryColumn.setCellValueFactory(cellData -> cellData.getValue().entryHourProperty());

        entryColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());

        entryColumn.setOnEditCommit(event -> {
            try {
                String hourValue = event.getNewValue();
                TableExcelModel model = event.getRowValue();
                setDataToModel(hourValue, model, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        exitColumn.setCellValueFactory(cellData -> cellData.getValue().exitHourProperty());
        exitColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());

        exitColumn.setOnEditCommit(event -> {
            try {
                String hourValue = event.getNewValue();
                TableExcelModel model = event.getRowValue();
                setDataToModel(hourValue, model, false);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        journalColumn.setCellValueFactory(cellData -> cellData.getValue().journalTimeProperty());
        journalColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());

        excelTable.setRowFactory(tableView -> {
            TableRow<TableExcelModel> row = new TableRow<>();
            return row;
        });

        populateTable(address, sheet);

        /*PseudoClass higlighted = PseudoClass.getPseudoClass("highlighted");
        excelTable.setRowFactory(tableView -> {
            TableRow<TableExcelModel> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldOrder, model) ->
                    row.pseudoClassStateChanged(higlighted,
                            oldOrder.getEntryHour().equals("FIN DE SEMANA") ));
            return row;
        });*/
    }

    /*@Override
    public void initialize(URL location, ResourceBundle resources) {
        inFormat = new SimpleDateFormat( "hh:mm");

        dayColumn.setCellValueFactory(cellData -> cellData.getValue().dayProperty());

        entryColumn.setCellValueFactory(cellData -> cellData.getValue().entryHourProperty());

        entryColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());

        entryColumn.setOnEditCommit(event -> {
            try {
                String hourValue = event.getNewValue();
                TableExcelModel model = event.getRowValue();
                setDataToModel(hourValue, model, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        exitColumn.setCellValueFactory(cellData -> cellData.getValue().exitHourProperty());
        exitColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());

        exitColumn.setOnEditCommit(event -> {
            try {
                String hourValue = event.getNewValue();
                TableExcelModel model = event.getRowValue();
                setDataToModel(hourValue, model, false);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        journalColumn.setCellValueFactory(cellData -> cellData.getValue().journalTimeProperty());
        journalColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());



    }*/

    private void populateTable(String address, int sheetNumber) {
        ArrayList<TableExcelModel> models = new ArrayList<>();
        try {

            File file = new File(address);

            XSSFWorkbook b; b = new XSSFWorkbook(file);

            XSSFSheet sheet = b.getSheetAt(sheetNumber);

            for(int i =15; i < 46; i++){
                XSSFRow row =sheet.getRow(i);
                if(row != null){
                    XSSFCell cell = row.getCell(1);
                    XSSFCell cellEntryHour = row.getCell(2);
                    XSSFCell cellExitHour = row.getCell(4);
                    XSSFCell cellJournalTime = row.getCell(6);
                    if(cell != null){
                        int day = (int)cell.getNumericCellValue();
                        String color = cell.getCellStyle().getFillForegroundColorColor().getARGBHex();
                        String freeDay = "";

                        if((color != null && !color.equalsIgnoreCase("FFFFFFFF")) || cellEntryHour.getCellType() == CellType.BLANK){
                            freeDay = returnFreeDay(color);
                            models.add(new TableExcelModel(Integer.toString(day), freeDay,freeDay, freeDay));
                        }else if(cellEntryHour.getCellType() == CellType.STRING){
                            models.add(new TableExcelModel(Integer.toString(day), cellEntryHour.getStringCellValue(),
                                    cellExitHour.getStringCellValue(),cellJournalTime.getStringCellValue()));
                        }else if(cellEntryHour.getCellType() == CellType.NUMERIC){

                            String entryTime = combineHoursAndMinutes(cellEntryHour.getDateCellValue().getHours(),cellEntryHour.getDateCellValue().getMinutes());
                            String exitTime = combineHoursAndMinutes(cellExitHour.getDateCellValue().getHours(),cellExitHour.getDateCellValue().getMinutes());

                            Date entryDate = inFormat.parse(entryTime);
                            Date exitDate = inFormat.parse(exitTime);

                            String resultHour = subtractHour(entryDate, exitDate);
                            models.add(new TableExcelModel(Integer.toString(day), entryTime,
                                    exitTime,resultHour.substring(1)));
                        }
                    }
                }
            }
            ObservableList<TableExcelModel> excelModels = FXCollections.observableArrayList(models);
            excelTable.setItems(excelModels);
            b.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void populateTable() {
        ArrayList<TableExcelModel> models = new ArrayList<>();
        try {

            File file = new File(TEST);

            XSSFWorkbook b; b = new XSSFWorkbook(file);

            XSSFSheet sheet = b.getSheetAt(SHEET_NUMBER);

            for(int i =15; i < 46; i++){
                XSSFRow row =sheet.getRow(i);
                if(row != null){
                    XSSFCell cell = row.getCell(1);
                    XSSFCell cellEntryHour = row.getCell(2);
                    XSSFCell cellExitHour = row.getCell(4);
                    XSSFCell cellJournalTime = row.getCell(6);
                    if(cell != null){
                        int day = (int)cell.getNumericCellValue();
                        String color = cell.getCellStyle().getFillForegroundColorColor().getARGBHex();
                        String freeDay = "";

                        if((color != null && !color.equalsIgnoreCase("FFFFFFFF")) || cellEntryHour.getCellType() == CellType.BLANK){
                            freeDay = returnFreeDay(color);
                            models.add(new TableExcelModel(Integer.toString(day), freeDay,freeDay, freeDay));
                        }else if(cellEntryHour.getCellType() == CellType.STRING){
                            models.add(new TableExcelModel(Integer.toString(day), cellEntryHour.getStringCellValue(),
                                    cellExitHour.getStringCellValue(),cellJournalTime.getStringCellValue()));
                        }else if(cellEntryHour.getCellType() == CellType.NUMERIC){

                            String entryTime = combineHoursAndMinutes(cellEntryHour.getDateCellValue().getHours(),cellEntryHour.getDateCellValue().getMinutes());
                            String exitTime = combineHoursAndMinutes(cellExitHour.getDateCellValue().getHours(),cellExitHour.getDateCellValue().getMinutes());

                            Date entryDate = inFormat.parse(entryTime);
                            Date exitDate = inFormat.parse(exitTime);

                            String resultHour = subtractHour(entryDate, exitDate);
                            models.add(new TableExcelModel(Integer.toString(day), entryTime,
                                    exitTime,resultHour.substring(1)));
                        }
                    }
                }
            }
            ObservableList<TableExcelModel> excelModels = FXCollections.observableArrayList(models);
            excelTable.setItems(excelModels);
            b.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private String returnFreeDay(String colorHex){
        String freeDay = "FESTIVO NACIONAL";
        if(colorHex == null){
            return "";
        }
        if(colorHex.equalsIgnoreCase(WEEKEND_COLOR)){
            freeDay = "FIN DE SEMANA";
        }else if(colorHex.equalsIgnoreCase(LOCAL_COLOR)){
            freeDay = "FESTIVO LOCAL";
        }else if(colorHex.equalsIgnoreCase(AUTONOMIC_COLOR)) {
            freeDay = "FESTIVO AUTONÓMICO";
        }
        return freeDay;
    }

    private void saveData(String address, int sheetNumber){
        try {
            File file = new File(address);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);

            CreationHelper createHelper = b.getCreationHelper();
            CellStyle cellStyle = b.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("h:mm"));

            XSSFSheet sheet = b.getSheetAt(sheetNumber);
            for(TableExcelModel model : excelTable.getItems()){
                int row = excelTable.getItems().indexOf(model)+15;
                XSSFCell cellEntryHour = sheet.getRow(row).getCell(2);
                XSSFCell cellExitHour = sheet.getRow(row).getCell(4);
                XSSFCell cellJournalTime = sheet.getRow(row).getCell(6);
                if(model.getIntegerJournalTime() != 0){
                    cellEntryHour.setCellStyle(cellStyle);
                    cellExitHour.setCellStyle(cellStyle);
                    cellEntryHour.setCellValue(DateUtil.convertTime(model.getEntryHour()));
                    cellExitHour.setCellValue(DateUtil.convertTime(model.getExitHour()));
                }else if(model.getEntryHour().equalsIgnoreCase("Vacaciones") ||
                        model.getEntryHour().equalsIgnoreCase("Baja") ||
                        model.getEntryHour().equalsIgnoreCase("Vacaciones Anteriores")){
                    cellEntryHour.setCellValue(model.getEntryHour());
                    cellExitHour.setCellValue(model.getEntryHour());
                    cellJournalTime.setCellType(CellType.STRING);
                    cellJournalTime.setCellValue(model.getEntryHour());
                }
            }
            OutputStream out = new FileOutputStream(file.getAbsolutePath(), false);
            b.write(out);
            b.close();
            //out.flush();
            out.close();
            inputStream1.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        for(TableExcelModel model : excelTable.getItems()){
            System.out.println(model);
        }
    }

    @FXML
    void saveData(ActionEvent event) {

        try {
            File file = new File(TEST);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);

            CreationHelper createHelper = b.getCreationHelper();
            CellStyle cellStyle = b.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("h:mm"));

            XSSFSheet sheet = b.getSheetAt(SHEET_NUMBER);
            for(TableExcelModel model : excelTable.getItems()){
                int row = excelTable.getItems().indexOf(model)+15;
                XSSFCell cellEntryHour = sheet.getRow(row).getCell(2);
                XSSFCell cellExitHour = sheet.getRow(row).getCell(4);
                XSSFCell cellJournalTime = sheet.getRow(row).getCell(6);
                if(model.getIntegerJournalTime() != 0){
                    cellEntryHour.setCellStyle(cellStyle);
                    cellExitHour.setCellStyle(cellStyle);
                    cellEntryHour.setCellValue(DateUtil.convertTime(model.getEntryHour()));
                    cellExitHour.setCellValue(DateUtil.convertTime(model.getExitHour()));
                }else if(model.getEntryHour().equalsIgnoreCase("Vacaciones") ||
                        model.getEntryHour().equalsIgnoreCase("Baja") ||
                        model.getEntryHour().equalsIgnoreCase("Vacaciones Anteriores")){
                    cellEntryHour.setCellValue(model.getEntryHour());
                    cellExitHour.setCellValue(model.getEntryHour());
                    cellJournalTime.setCellType(CellType.STRING);
                    cellJournalTime.setCellValue(model.getEntryHour());
                }
            }
            OutputStream out = new FileOutputStream(file.getAbsolutePath(), false);
            b.write(out);
            b.close();
            //out.flush();
            out.close();
            inputStream1.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        for(TableExcelModel model : excelTable.getItems()){
            System.out.println(model);
        }
    }


    private String formatDouble(double time){
        String value = Double.toString(time);
        String correctedValue = value.replace(".",":");
        if(correctedValue.length() == 3){
            return correctedValue + "0";
        }
        return correctedValue;
    }

    private boolean validateHour(String hour){
        return hour.contains(":");
    }

    private void setDataToModel(String value, TableExcelModel model, boolean entry) throws ParseException {
        if(entry){
            model.setEntryHour(value);
        }else{
            model.setExitHour(value);
        }

        if(validateHour(value)){
            double hour;
            if(model.getIntegerJournalTime() == 0.0){
                hour = calculateHourOnEntryOrExit(entry, model);
            }
            else{
                Date entryDate = inFormat.parse(model.getEntryHour());
                Date exitDate = inFormat.parse(model.getExitHour());
                String resultHour = subtractHour(entryDate, exitDate);

                hour = Double.parseDouble(resultHour.replace(":","."));
            }
            model.setJournalTime(formatDouble(hour));
        }else{
            model.setJournalTime(value);
            model.setExitHour(value);
            model.setEntryHour(value);
        }

    }

    private double calculateHourOnEntryOrExit(boolean entry, TableExcelModel model) throws ParseException {
        Date defaultDate = inFormat.parse("16:00");
        Date date;
        String resultHour;
        if (!entry) {
            date = inFormat.parse(model.getExitHour());
            resultHour = subtractHour(date,defaultDate);
        }
        else{
            date = inFormat.parse(model.getEntryHour());
            resultHour = subtractHour(defaultDate,date);
        }
        return Double.parseDouble(resultHour.replace(":","."));

    }

    private String combineHoursAndMinutes(double hours, double minutes){
        int integerHours = (int) hours;
        int integerMinutes = (int) minutes;
        String minutesString = Integer.toString(integerMinutes).length() == 1 ? integerMinutes + "0": Integer.toString(integerMinutes);
        return integerHours+":"+minutesString;
    }

    private String subtractHour(Date entryDate, Date exitDate){
        LocalTime localExitTime = LocalTime.of(exitDate.getHours(), exitDate.getMinutes());

        LocalTime updatedTime = localExitTime.minusHours(entryDate.getHours() + 1).
                minusMinutes(entryDate.getMinutes());

        return updatedTime.toString();
    }

}
