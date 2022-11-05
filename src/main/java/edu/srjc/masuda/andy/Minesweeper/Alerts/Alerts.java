package edu.srjc.masuda.andy.Minesweeper.Alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerts

{
    public static void showAlert(String title, String msg, Alert.AlertType alertType)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static ButtonType showYesNoAlert(String title, String msg, Alert.AlertType alertType)
    {
        Alert alert = new Alert(alertType, msg, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText("");

        Optional<ButtonType> answer = alert.showAndWait();

        return answer.get();
    }
}
