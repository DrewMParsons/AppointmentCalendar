package main.exceptions;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

/**
 *
 * @author Drew
 */
public class Alerts {

    /**
     * Basic Alert Error dialog is opened
     *
     * @param alertText
     */
    public static void errorAlert(String alertText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(alertText);
        Optional<ButtonType> result = alert.showAndWait();
    }

    /**
     * Alert used when cancel button is pressed
     *
     * @return
     */
    public static boolean cancelAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Confirming!");
        alert.setContentText("Are you sure you wish to cancel?");

        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;

    }

    /**
     * Alert dialog used when deleting an item
     *
     * @param name
     * @return
     */
    public static boolean deleteAlert(String name) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Deleting!");
        alert.setContentText("Are you sure you wish to delete " + name + "?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;

    }

    /**
     * Alert used for Search when item not found
     *
     * @param textfield
     */
    public static void searchAlert(TextField textfield) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Error");
        alert.setContentText(textfield.getText() + " not found");

        alert.showAndWait();
    }
    /**
     * Alert used to inform the user that the information was saved to the DB
     */
    public static void saveAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Confirmation");
        alert.setHeaderText("Success!");
        alert.setContentText("Information was saved successfully");
        alert.showAndWait();
    }

}
