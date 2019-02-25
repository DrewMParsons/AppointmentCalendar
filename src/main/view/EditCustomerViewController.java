package main.view;

import main.utils.ControllerInterface;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.exceptions.Alerts;
import static main.exceptions.Alerts.cancelAlert;
import static main.exceptions.Alerts.deleteAlert;
import static main.exceptions.Alerts.saveAlert;
import main.exceptions.Validations;
import main.model.Customer;
import main.utils.DataBaseConnector;

/**
 * FXML Controller class
 *
 * @author Drew Parsons
 */
public class EditCustomerViewController implements Initializable, ControllerInterface<Customer>
{

    @FXML
    private TextField customerNameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private ComboBox<String> countryComboBox;
    @FXML
    private Button deleteCustomerButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    Label header;
    @FXML
    Label errorMessageLabel;

    private Customer customer;
    private int customerID;
    private int addressID;

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
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
        //set Delete Button to visable(false)
        deleteCustomerButton.setVisible(false);
        //set error message to empty
        errorMessageLabel.setText("");
        try
        {
            initCountry();
        } catch (SQLException ex)
        {
            Logger.getLogger(EditCustomerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void preloadData(Customer customer)
    {

        deleteCustomerButton.setVisible(true);
        this.customer = customer;
        this.customerNameTextField.setText(customer.getCustomerName());
        this.addressTextField.setText(customer.getAddress());
        this.phoneTextField.setText(customer.getPhone());
        this.header.setText("Edit Customer");
        this.countryComboBox.setValue(customer.getCountry());
        this.cityComboBox.setValue(customer.getCity());
        this.customerID = customer.getCustomerID();
        this.addressID = customer.getAddressID();
        try
        {
            initCity();
        } catch (SQLException ex)
        {
            Logger.getLogger(EditCustomerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cancelButtonHandler(ActionEvent event) throws IOException
    {
        if (cancelAlert())
        {
            SceneChanger sc = new SceneChanger();
            sc.changeScenes(event, "CustomerTableView.fxml", "Customer Table");
        }
    }

    /**
     * if customer has been selected in the table, this method will delete the
     * customer from the Database.
     *
     * @param event
     */
    @FXML
    void deleteCustomerButtonHandler(ActionEvent event) throws SQLException, IOException
    {

        if (deleteAlert(customer.getCustomerName()))
        {
            customer.deleteFromDB(customer.getCustomerID());
            SceneChanger sc = new SceneChanger();
            sc.changeScenes(event, "CustomerTableView.fxml", "Customer Table");

        }

    }

    /**
     * This method will populate the City combobox with a list of city names
     * from the city table that match the Country selected in the Country
     * combobox
     */
    @FXML
    private void initCity() throws SQLException
    {

        String country = countryComboBox.getValue();

        String sql = "SELECT city "
                + "FROM city, country "
                + "WHERE city.countryId = country.countryId "
                + "AND country.country =?;";

        try
        {
            Connection conn = DataBaseConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, country);
            ResultSet rs = ps.executeQuery();

            cityComboBox.getItems().clear();
            while (rs.next())
            {
                cityComboBox.getItems().add(rs.getString(1));
            }
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * This method will populate the County comboBox with a list of Country from
     * DB
     */
    private void initCountry() throws SQLException
    {

        String sql = ("SELECT country FROM country");
        // Connect to DB
        try (Connection conn = DataBaseConnector.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                countryComboBox.getItems().add(rs.getString(1));
            }

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Method will attempt to connect to the DB and insert a new Customer
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void saveButtonHandler(ActionEvent event) throws IOException, SQLException
    {
        if (validateCustomer())
        {
            if (customer != null)//update existing Customer
            {

                updateCustomer();
                customer.updateInDB(cityComboBox.getValue(), customerID, addressID);
            } else //create new customer
            {

                customer = new Customer(customerNameTextField.getText(), addressTextField.getText(),
                        cityComboBox.getValue(), countryComboBox.getValue(),
                        phoneTextField.getText());
                customer.addInDB(cityComboBox.getValue());
            }
            saveAlert();

            SceneChanger sc = new SceneChanger();
            sc.changeScenes(event, "CustomerTableView.fxml", "Customer Table");
        }

    }

    /**
     * Method will accept the information entered in the fields and add them to
     * the Customer object
     *
     */
    private void updateCustomer()
    {
        customer.setCustomerName(customerNameTextField.getText());
        customer.setAddress(addressTextField.getText());
        customer.setPhone(phoneTextField.getText());
        customer.setCountry(countryComboBox.getValue());
        customer.setCity(cityComboBox.getValue());

    }

    /**
     * Method checks that all fields have values using the Validations method
     * isInputValid.  This method will alert the user if a field is empty will
     * a pop up alert message box
     * @return 
     */
    private boolean validateCustomer()
    {
        if (Validations.isInputValid(customerNameTextField))
        {
            if (Validations.isInputValid(addressTextField))
            {
                if (Validations.isInputValid(countryComboBox))
                {
                    if (Validations.isInputValid(cityComboBox))
                    {
                        if (Validations.isInputValid(phoneTextField))
                        {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }
}
