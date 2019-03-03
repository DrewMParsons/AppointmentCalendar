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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import main.exceptions.Alerts;
import static main.exceptions.Alerts.saveAlert;
import main.exceptions.Validations;
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
    public HashMap<String, Integer> customerHashMap = new HashMap<String, Integer>();
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    public ObservableList<Appointment> getAppointments()
    {
        return appointments;
    }

    public void setAppointments(ObservableList<Appointment> appointments)
    {
        this.appointments = appointments;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        System.out.println(appointments.isEmpty());
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
        //initialize the Type combo box
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
                customerHashMap.put(rs.getString(1), rs.getInt(2));
                customerComboBox.getItems().add(rs.getString(1));
            }

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Adds appointment types to the types combobox
     */
    private void initType()
    {
        typeComboBox.getItems().addAll("Casual", "Contract", "General", "Presentation");
    }

    private void loadAppointments() throws SQLException
    {

        String sql = "SELECT customer.customerName,appointment.customerId, appointmentId, type, userId,start, end "
                + "FROM customer,appointment WHERE appointment.customerId=customer.customerId;";

        try (Connection conn = DataBaseConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(sql);)
        {
            while (rs.next())
            {
                Appointment newAppointment = new Appointment(rs.getString("customerName"),
                        rs.getInt("appointmentId"), rs.getString("type"),
                        rs.getInt("customerId"), rs.getInt("userId"),
                        rs.getTimestamp("start"), rs.getTimestamp("end"));

                appointments.add(newAppointment);

            }

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

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
        startTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17, appointment.getStartTime().getHour()));
        endTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17, appointment.getEndTime().getHour()));
        startTimeMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, appointment.getStartTime().getMinute()));
        endTimeMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, appointment.getEndTime().getMinute()));
        customerComboBox.setValue(appointment.getCustomerName());
        typeComboBox.setValue(appointment.getType());
        appointmentDatePicker.setValue(appointment.getDate());

    }

    /**
     * Method will attempt to connect to DB and update the appointment if it is
     * an existing appointment, or add a new appointment
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void savebuttonHandler(ActionEvent event) throws IOException, SQLException
    {
        
        Time startTime = new Time(startTimeHourSpinner.getValue(), startTimeMinuteSpinner.getValue(), 0);
        Time endTime = new Time(endTimeHourSpinner.getValue(), endTimeMinuteSpinner.getValue(), 0);
        if(isValidTime(startTime.toLocalTime(), endTime.toLocalTime()))
        {
            Alerts.errorAlert("Times entered are Invalid");
        }
        else
        {
            if(isValidAppointment())
            {
                int customerId = customerHashMap.get(customerComboBox.getValue());
                if (isDuplicateAppointmentTime(appointmentDatePicker.getValue(), startTime.toLocalTime(), endTime.toLocalTime()))
                {

                    if (appointment != null)
                    {

                        updateAppointment(customerId, startTime, endTime);
                        appointment.updateInDB(appointment.getAppointmentID());
                    } else
                    {

                        appointment = new Appointment(typeComboBox.getValue(), customerId, customerComboBox.getValue(),
                                LoginViewController.USERID, appointmentDatePicker.getValue(),
                                startTime.toLocalTime(), endTime.toLocalTime());

                        appointment.addInDB();

                    }
                    saveAlert();

                    SceneChanger sc = new SceneChanger();
                    sc.changeScenes(event, "AppointmentView.fxml", "Appointments");
                } else
                {
                    Alerts.errorAlert("Appointment time conflicts with another appointment");
                }
            }
            
        }

    }

    private void updateAppointment(int customerId, Time startTime, Time endTime)
    {

        appointment.setCustomerName(customerComboBox.getValue());
        appointment.setCustomerID(customerId);
        appointment.setType(typeComboBox.getValue());
        appointment.setDate(appointmentDatePicker.getValue());
        appointment.setUserID(LoginViewController.USERID);
        appointment.setStartTime(startTime.toLocalTime());
        appointment.setStart(LocalDateTime.of(appointment.getDate(), appointment.getStartTime()));
        appointment.setEndTime(endTime.toLocalTime());
        appointment.setEnd(LocalDateTime.of(appointment.getDate(), appointment.getEndTime()));
        System.out.println(appointment.getCustomerName());
        System.out.println(appointment.getStartTime());
        System.out.println(appointment.getDate());
        System.out.println(appointment.getEndTime());
        System.out.println(appointment.getType());
        System.out.println(appointment.getStart());

    }

    /**
     * This method checks the start and end time of the current appointment to
     * ensure that it doesn't overlap with another appointment scheduled for the
     * current User
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private boolean isDuplicateAppointmentTime(LocalDate date, LocalTime startTime, LocalTime endTime) throws SQLException
    {
        loadAppointments();
        Predicate<Appointment> test = a -> ((a.getUserID() == LoginViewController.USERID && (a.getDate().isEqual(date)))
                    && ((startTime.isAfter(a.getStartTime()) && startTime.isBefore(a.getEndTime()))
                    || (endTime.isAfter(a.getStartTime()) && endTime.isBefore(a.getEndTime()))
                    || (startTime.equals(a.getStartTime())|| (startTime.equals(a.getEndTime())
                    ||(endTime.equals(a.getEndTime())) || (endTime.equals(a.getStartTime()))
                            ))));
        
        return(appointments.stream().noneMatch(test));
        
 
    }
    
    private boolean isValidAppointment()
    {
       if(Validations.isInputValid(typeComboBox))
           if(Validations.isInputValid(customerComboBox))
               if(Validations.isInputValid(appointmentDatePicker))
                   return true;
    return false;
    }
    
    private boolean isValidTime(LocalTime startTime, LocalTime endTime)
    {
        return(endTime.equals(startTime)|| endTime.isBefore(startTime));
            
    }

}
