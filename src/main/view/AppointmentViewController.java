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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
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
import javafx.scene.control.ComboBox;
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
    @FXML
    private ToggleButton allToggleButton;
    @FXML
    private ComboBox<String> userComboBox;

    private User user;
    private Appointment appointment;
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    public HashMap<String, Integer> userHashMap = new HashMap<String, Integer>();

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

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
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        this.user = SceneChanger.getLoggedInUser();

        //initalize the user combo box to current user
        userComboBox.setValue(user.getUserName());

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
                initUser();
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

        if (deleteAlert(" The Selected Appointment"))
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
        //eavc.setAppointments(appointments);
        eavc.getAppointments().addAll(appointments);
        sc.changeScenes(event, "EditAppointmentView.fxml", "Edit Appointment", appointment, eavc);
    }

    private void initUser() throws SQLException
    {
        String sql = "SELECT userName, userId FROM user;";

        // Connect to DB
        try (Connection conn = DataBaseConnector.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {

                userComboBox.getItems().add(rs.getString(1));
                userHashMap.put(rs.getString(1), rs.getInt(2));
            }

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

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

    @Override
    public void preloadData(User u)
    {
        this.user = u;

    }

    /**
     * This method adjusts the TableView to list all appointments when the ALL
     * ToggleButton is pushed
     */
    @FXML
    private void allToggleButtonHandler(ActionEvent event)
    {
        appointmentTableView.getItems().clear();
        int userID = userHashMap.get(userComboBox.getValue());
        appointments.stream().filter((a) ->
        {
            return (a.getUserID() == userID);
        }).forEachOrdered((a) ->
        {
            appointmentTableView.getItems().addAll(a);
        });

    }

    /**
     * This method adjusts the TableView to the current MONTH when the monthly
     * ToggleButton is pushed
     */
    @FXML
    private void monthlyToggleButtonHandler(ActionEvent event)
    {
        appointmentTableView.getItems().clear();
        int userID = userHashMap.get(userComboBox.getValue());
        appointments.stream().filter((a) ->
        {
            return a.getDate().getMonthValue() == LocalDate.now().getMonthValue()
                    && (a.getUserID() == userID);
        }).forEachOrdered((a) ->
        {
            appointmentTableView.getItems().add(a);
        });
    }

    /**
     * This method adjusts the TableView to display appointments for the user
     * specified in the User ComboBox
     *
     * @param event
     */
    @FXML
    private void userComboBoxHandler(ActionEvent event)
    {
        appointmentTableView.getItems().clear();
        int userID = userHashMap.get(userComboBox.getValue());
        appointments.stream().filter((a)
                -> (a.getUserID() == userID)).forEachOrdered((a) ->
        {
            appointmentTableView.getItems().add(a);
        });

    }

    /**
     * This method adjusts the TableView to the current week when the weekly
     * ToggleButton is pushed
     */
    @FXML
    private void weeklyToggleButtonHandler(ActionEvent event)
    {

        int userID = userHashMap.get(userComboBox.getValue());
        appointmentTableView.getItems().clear();
        appointments.stream().filter((a) ->
        {
            return (a.getDate().compareTo(currentWeekStart()) > -1
                    && a.getDate().compareTo(currentWeekEnd()) < 1)
                    && (a.getUserID() == userID);
        }).forEachOrdered((a) ->
        {
            appointmentTableView.getItems().add(a);
        });
    }

    /**
     * This Method returns the Start date of the current week
     */
    private LocalDate currentWeekStart()
    {
        LocalDate today = LocalDate.now();

        //start of week-MONDAY
        LocalDate monday = today;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY)
        {
            monday = monday.minusDays(1);
        }

        return monday;
    }

    /**
     * This Method returns the End date of the current week
     */
    private LocalDate currentWeekEnd()
    {
        LocalDate today = LocalDate.now();

        //End of week=Sunday
        LocalDate sunday = today;
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY)
        {
            sunday = sunday.plusDays(1);
        }
        return sunday;
    }

}
