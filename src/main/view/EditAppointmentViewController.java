/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.view;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import static main.exceptions.Alerts.saveAlert;
import main.model.Appointment;
import main.utils.ControllerInterface;
import main.utils.DataBaseConnector;

/**
 * FXML Controller class
 *
 * @author Drew Parsons
 */
public class EditAppointmentViewController implements Initializable, ControllerInterface<Appointment>
{

    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private DatePicker appointmentDatePicker;
     @FXML
    private Spinner<Integer> startTimeHourSpinner;

    @FXML
    private Spinner<Integer> startTimeMinuteSpinner;

    @FXML
    private Spinner<Integer> endTimeHourSpinner;

    @FXML
    private Spinner<Integer> endTimeMinuteSpinner;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> customerComboBox;
    private Appointment appointment;
    private ObservableList<String> timeList;
    public HashMap<String,Integer> customerHashMap = new HashMap<String,Integer>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //set the hour spinners to only allow values during workhours.
        startTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17));
        endTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17));
        startTimeMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        endTimeMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        try
        {
            //initialize the Customer combo box
            initCustomer();
        } catch (SQLException ex)
        {
            Logger.getLogger(EditAppointmentViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        initType();
        
        
    }  
    /**
     * This method will populate the Customer comboBox with a list of Customers 
     * from the Database
     */
    private void initCustomer() throws SQLException
    {

        String sql = ("SELECT customerName, customerId  FROM customer");
        // Connect to DB
        try (Connection conn = DataBaseConnector.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                customerHashMap.put(rs.getString(1),rs.getInt(2));
                customerComboBox.getItems().add(rs.getString(1));
            }

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }
    /**
     * Method initialized the StartTime Spinner
     */
    /**
     * Adds appointment types to the types combobox
     */
    private void initType() 
    {
        typeComboBox.getItems().addAll("Casual","Contract","General","Presentation");
    }

    

    @FXML
    private void cancelButtonHandler(ActionEvent event) throws IOException
    {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "AppointmentView.fxml", "Appointments");
    }

    @Override
    public void preloadData(Appointment appointment)
    {
        this.appointment = appointment;
        startTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17,appointment.getStartTime().getHour()));
        endTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17,appointment.getEndTime().getHour()));
        startTimeMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59,appointment.getStartTime().getMinute()));
        endTimeMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59,appointment.getEndTime().getMinute()));
        customerComboBox.setValue(appointment.getCustomerName());
        typeComboBox.setValue(appointment.getType());
        appointmentDatePicker.setValue(appointment.getDate());
        
    }
    /**
     * Method will attempt to connect to DB and update the appointment if it 
     * is an existing appointment, or add a new appointment
     * @param event
     * @throws IOException 
     */
    @FXML
    private void savebuttonHandler(ActionEvent event) throws IOException, SQLException
    {
        int customerId = customerHashMap.get(customerComboBox.getValue());
        Time startTime = new Time(startTimeHourSpinner.getValue(),startTimeMinuteSpinner.getValue(),0);
        Time endTime = new Time(endTimeHourSpinner.getValue(),endTimeMinuteSpinner.getValue(),0);
        if(appointment !=null)
        {
            updateAppointment(customerId,startTime, endTime);
            appointment.updateInDB(appointment.getAppointmentID());
        }
        else
        {
            Appointment appointment = new Appointment(typeComboBox.getValue(), customerId, customerComboBox.getValue(),
                    LoginViewController.USERID, appointmentDatePicker.getValue(),
                    startTime.toLocalTime(), endTime.toLocalTime());

            try
            {
                appointment.addInDB();
            } catch (SQLException ex)
            {
                Logger.getLogger(EditAppointmentViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        saveAlert();
        
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "AppointmentView.fxml", "Appointments");
        
    }

    private void updateAppointment(int customerId,Time startTime,Time endTime)
    {
        
        
        appointment.setCustomerName(customerComboBox.getValue());
        appointment.setCustomerID(customerId);
        appointment.setDate(appointmentDatePicker.getValue());
        appointment.setUserID(LoginViewController.USERID);
        appointment.setStartTime(startTime.toLocalTime());
        appointment.setEndTime(endTime.toLocalTime());
        
        
    }
    
    
}
