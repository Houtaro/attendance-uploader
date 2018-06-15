
package utils;

import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertHelper {
    
    private static Alert alert = null;
    
    public static void show(AlertType alertType, String title, String headerText, String contentText) {
        Platform.runLater(() -> {
            alert = new Alert(alertType);
            if(alert.getAlertType() != AlertType.CONFIRMATION) {
                alert.setTitle(title);
                alert.setHeaderText(headerText);
                alert.setContentText(contentText);
                alert.show();
            }
        });
    }
    
    public static Optional showAndWait(AlertType alertType, String title, String headerText, String contentText) {
        
        alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        
        return alert.showAndWait();
    }
}
