/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.exceptions;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 *
 * @author Drew Parsons
 */
public class Validations
{

/**
     * Tests that the value of a TextField is not empty or null. This method will 
     * alert the user if a field is empty with a pop up alert message box
     *
     * @param textField
     * @return
     */
    public static boolean isInputValid(TextField textField) {
        boolean b = false;
        if (!(textField.getText() == null || textField.getText().isEmpty())) {

            b = true;
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(textField.getId() + " cannot be empty");
            Optional<ButtonType> result = alert.showAndWait();
        }
        return b;
    }
    /**
     * Tests that the value of a ComboBox is not empty. This method will 
     * alert the user if a field is empty with a pop up alert message box
     * @param combobox
     * @return 
     */
    public static boolean isInputValid(ComboBox combobox)
    {
        boolean b = false;
        if (combobox.getValue()!= null) {

            b = true;
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(combobox.getId() + " cannot be empty");
            Optional<ButtonType> result = alert.showAndWait();
        }
        return b;
        
    }
    public static boolean isInputValid(DatePicker datePicker)
    {
        boolean b = false;
        if (datePicker.getValue()!= null) {

            b = true;
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(datePicker.getId() + " cannot be empty");
            Optional<ButtonType> result = alert.showAndWait();
        }
        return b;
        
    }

}