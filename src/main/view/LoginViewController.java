package main.view;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private TextField userIDTextField;
    @FXML
    private PasswordField pwField;
    @FXML
    private Label errorMsgLabel;

    public static int USERID;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        errorMsgLabel.setText("");
    }

    @FXML
    private void loginButtonPushed(ActionEvent event) throws IOException
    {
        String userID = userIDTextField.getText();
        String pwEntered = pwField.getText();
        String sql = "SELECT * FROM user WHERE userName =?;";
        User user=null;

        //Query the DB with the ID and pword provided
        try
        {
            Connection conn = DataBaseConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
        
            
            while (rs.next())
            {
                //String dbPassword = rs.getString("password");
                
                user = new User(rs.getInt("userId"),
                                rs.getString("userName"), 
                                rs.getString("password"));

            }
            if( user!=null && pwEntered.equals(user.getPassword()))
            {
                SceneChanger sc = new SceneChanger();
                USERID = user.getUserID();
                SceneChanger.setLoggedInUser(user);
                CustomerTableViewController ctvc = new CustomerTableViewController();
                sc.changeScenes(event, "CustomerTableView.fxml", "All Customers",user,ctvc);
            }
            else
            {
                errorMsgLabel.setText("User ID and password are incorrect");
            }

        } catch (SQLException ex)
        {
            Logger.getLogger(LoginViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
