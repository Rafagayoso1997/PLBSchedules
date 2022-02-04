module org.gsoft {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires itextpdf;
    requires java.sql;
    requires java.datatransfer;
    requires java.desktop;
    requires org.apache.poi.ooxml;
    requires java.persistence;

    opens org.gsoft to javafx.fxml;
    opens org.gsoft.controller to javafx.fxml;

    opens org.gsoft.model to javafx.base;
    exports org.gsoft;
}