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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import static main.exceptions.Alerts.deleteAlert;
import main.model.Appointment;
import main.model.Customer;
import main.model.User;
import main.utils.ControllerInterface;
import main.utils.DataBaseConnector;

/**
 * FXML Controller class
 *
 * @author Drew Parsons
 */
public class AppointmentViewController implements Initializable, ControllerInterface<User>
{

    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableColumn<Appointment, String> dayOfWeekColumn;
    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> startTimeColumn;
    @FXML
    private TableColumn<Appointment, String> endTimeColumn;
    @FXML
    private TableColumn<Appointment, String> customerColumn;
    @FXML
    private Button addAppointmentButton;
    @FXML
    private Button editAppointmentButton;
    @FXML
    private Button deleteAppointmentButton;
    @FXML
    private Button backButton;
    @FXML
    private ToggleButton monthlyToggleButton;
    @FXML
    private ToggleGroup calendarTypeToggleButtonGroup;
    @FXML
    private ToggleButton weeklyToggleButton;
    private User user;
    private Appointment appointment;
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        
        System.out.println("User ID:" + LoginViewController.USERID);
        // Disable Edit and Delete Appointment Buttons
        editAppointmentButton.setDisable(true);
        deleteAppointmentButton.setDisable(true);

        //Config Table Colums
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().TypeProperty());
        dayOfWeekColumn.setCellValueFactory(cellData -> cellData.getValue().DayProperty());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().StartTimeProperty());
        endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().EndTimeProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        customerColumn.setCellValueFactory(cellData -> cellData.getValue().CustomerNameProperty());

        {

            try
            {
                loadAppointments();

            } catch (SQLException e)
            {
                System.err.println(e.getMessage());
            }

        }
    }

    @FXML
    private void addAppointmentButtonHandler(ActionEvent event) throws IOException
    {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "EditAppointmentView.fxml", "Add Appointment");
    }

    @FXML
    private void backButtonHandler(ActionEvent event) throws IOException
    {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "CustomerTableView.fxml", "Customer Table");
    }
    /**
     * If appointment has been selected in the table, this enables the edit and
     * delete button
     */
    @FXML
    public void appointmentSelected()
    {
        deleteAppointmentButton.setDisable(false);
        editAppointmentButton.setDisable(false);

    }

    @FXML
    private void deleteAppointmentButtonHandler(ActionEvent event) throws SQLException
    {
        appointment = this.appointmentTableView.getSelectionModel().getSelectedItem();
        
        if(deleteAlert(" The Selected Appointment"))
        {
            appointment.deleteFromDB(appointment.getAppointmentID());
            appointmentTableView.getItems().remove(appointment);
        }
        

        
    }

    @FXML
    private void editAppointmentButtonHandler(ActionEvent event) throws IOException
    {
        appointment = this.appointmentTableView.getSelectionModel().getSelectedItem();
        SceneChanger sc = new SceneChanger();
        EditAppointmentViewController eavc = new EditAppointmentViewController();
        sc.changeScenes(event, "EditAppointmentView.fxml", "Edit Appointment",appointment,eavc);
    }

    private void loadAppointments() throws SQLException
    {

        
        String sql = "SELECT customer.customerName,appointment.customerId, appointmentId, type, start, end "
                + "FROM customer,appointment WHERE appointment.customerId=customer.customerId AND userId =" + LoginViewController.USERID + ";";

        try (Connection conn = DataBaseConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(sql);)
        {
            while (rs.next())
            {
                Appointment newAppointment = new Appointment(rs.getString("customerName"),
                        rs.getInt("appointmentId"),rs.getString("type"),
                        rs.getInt("customerId"), LoginViewController.USERID,
                        rs.getTimestamp("start"), rs.getTimestamp("end"));
                
                appointments.add(newAppointment);
                
            }

            appointmentTableView.getItems().addAll(appointments);
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

    }

    @Override
    public void preloadData(User user)
    {
        this.user = user;

    }

}
