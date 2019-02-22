/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import static main.exceptions.Alerts.saveAlert;

/**
 * FXML Controller class
 *
 * @author Drew Parsons
 */
public class EditAppointmentViewController implements Initializable
{

    @FXML
    private ComboBox<?> typeComboBox;
    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private Spinner<?> startTimeSpinner;
    @FXML
    private Spinner<?> endTimeSpinner;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }    

    @FXML
    private void savebuttonHandler(ActionEvent event) throws IOException
    {
        saveAlert();
        
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "AppointmentView.fxml", "Appointments");
        
    }

    @FXML
    private void cancelButtonHandler(ActionEvent event) throws IOException
    {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "AppointmentView.fxml", "Appointments");
    }
    
}
