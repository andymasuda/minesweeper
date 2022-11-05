module edu.srjc.masuda.andy.final_masuda_andy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens edu.srjc.masuda.andy.Minesweeper to javafx.fxml;
    exports edu.srjc.masuda.andy.Minesweeper;
}