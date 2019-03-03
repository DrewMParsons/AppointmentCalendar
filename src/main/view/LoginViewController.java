package main.view;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.exceptions.Alerts;
import main.model.Appointment;
import main.model.User;
import main.utils.DataBaseConnector;

/**
 * FXML Controller class
 *
 * @author Drew Parsons
 */
public class LoginViewController implements Initializable
{

    @FXML
    private Label loginHeader;
    @FXML
    private Label userName;
    @FXML
    private Label password;
    @FXML
    private TextField userIDTextField;
    @FXML
    private PasswordField pwField;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private Button loginButton;
    private User user;
    private  ObservableList<Appointment> appointments = FXCollections.observableArrayList();

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
    
    
    
    public static String USERNAME;
    public static int USERID;
    
    //private ResourceBundle resourceBundle;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //hide the error message label
        errorMsgLabel.setVisible(false);
        
        //set the resourcebundle to the user's default Locale
        ResourceBundle resourceBundle = ResourceBundle.getBundle("LoginViewController",Locale.getDefault());
        loginHeader.setText(resourceBundle.getString("login"));
        userName.setText(resourceBundle.getString("name"));
        password.setText(resourceBundle.getString("password"));
        errorMsgLabel.setText(resourceBundle.getString("error"));
        
        
        
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
                        rs.getInt("appointmentId"),rs.getString("type"),
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
    private void loginButtonPushed(ActionEvent event) throws IOException
    {
        String userID = userIDTextField.getText();
        String pwEntered = pwField.getText();
        String sql = "SELECT * FROM user WHERE userName =?;";
        

        //Query the DB with the ID and pword provided
        try
        {
            Connection conn = DataBaseConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
        
            
            while (rs.next())
            {
                
                
                user = new User(rs.getInt("userId"),
                                rs.getString("userName"), 
                                rs.getString("password"));

            }
            if( user!=null && pwEntered.equals(user.getPassword()))
            {
                loadAppointments();
                for(Appointment a : appointments)
                {
                    if(a.getDate().equals(LocalDate.now()))
                    {
                        if(a.getStartTime().isAfter(LocalTime.now()) && a.getStartTime().isBefore(LocalTime.now().plusMinutes(15)))
                                {
                                    Alerts.errorAlert("You have an appointment within 15 minutes of now");
                                  
                                }
                    }
                }
                SceneChanger sc = new SceneChanger();
                USERID = user.getUserID();
                USERNAME= user.getUserName();
                SceneChanger.setLoggedInUser(user);
                CustomerTableViewController ctvc = new CustomerTableViewController();
                sc.changeScenes(event, "CustomerTableView.fxml", "All Customers",user,ctvc);
                
            }
            else
            {
               
                
                 errorMsgLabel.setVisible(true);
            }

        } catch (SQLException ex)
        {
            Logger.getLogger(LoginViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    

}
