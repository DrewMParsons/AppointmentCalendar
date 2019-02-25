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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.model.Customer;
import main.model.User;
import main.utils.ControllerInterface;
import main.utils.DataBaseConnector;

/**
 * FXML Controller class
 *
 * @author Drew Parsons
 */
public class CustomerTableViewController implements Initializable, ControllerInterface<User>
{

    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> streetColumn;
    @FXML
    private TableColumn<Customer, String> cityColumn;
    @FXML
    private TableColumn<Customer, String> countryColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    @FXML
    private Button appointmentButton;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Button editCustomerButton;

    @FXML
    private Button reportsButton;
    private Customer customer;
    private User user;

    //private Customer customer;
    /**
     * Initializes the controller class. Lambda expression is used to set the
     * values of the table columns
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        // Disable Edit and Delete Customer Buttons
        editCustomerButton.setDisable(true);

        //Config Table Columns
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().CustomerNameProperty());
        streetColumn.setCellValueFactory(cellData -> cellData.getValue().AddressProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().PhoneProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().CityProperty());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().CountryProperty());

        try
        {

            loadCustomers();
        } catch (SQLException ex)
        {
            Logger.getLogger(CustomerTableViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Method will load the Edit Customer View with no preloaded data, for the
     * user to enter a new Data
     *
     * @param event
     */

    @FXML
    void addCustomerButtonHandler(ActionEvent event) throws IOException
    {

        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "EditCustomerView.fxml", "Add New Customer");
        customerTableView.refresh();

    }

    @FXML
    private void appointmentButtonHandler(ActionEvent event) throws IOException
    {
        AppointmentViewController controller = new AppointmentViewController();
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "AppointmentView.fxml", "Appointments",user,controller);
    }

    /**
     * If customer has been selected in the table, this enables the edit button
     */
    @FXML
    public void customerSelected()
    {
        editCustomerButton.setDisable(false);

    }

    /**
     * If a customer is selected from the table, This method will pass the
     * Customer to the Edit Customer View
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void editCustomerButtonHandler(ActionEvent event) throws IOException, SQLException
    {
        customer = this.customerTableView.getSelectionModel().getSelectedItem();
        SceneChanger sc = new SceneChanger();
        EditCustomerViewController controller = new EditCustomerViewController();

        sc.changeScenes(event, "EditCustomerView.fxml", "Edit Customer", customer, controller);

    }

    /**
     * This method will connect to the Database and load the Customers into the
     * Customer TableView
     */
    private void loadCustomers() throws SQLException
    {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String sql = "SELECT customerId, customerName, address.addressId, address, phone, city,country "
                + "FROM customer, address, city, country "
                + "WHERE address.addressId=customer.addressId "
                + "AND address.cityId= city.cityId "
                + "AND city.countryId= country.countryId;";

        try (Connection conn = DataBaseConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(sql);)
        {
            while (rs.next())
            {
                Customer newCustomer = new Customer(rs.getInt("customerId"), rs.getString("customerName"),
                        rs.getInt("addressId"), rs.getString("address"), rs.getString("city"),
                        rs.getString("country"), rs.getString("phone"));
                customers.add(newCustomer);
            }

            customerTableView.getItems().addAll(customers);
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

    }

    @FXML
    private void reportsButtonHandler(ActionEvent event)
    {

    }

    @Override
    public void preloadData(User user)
    {
        this.user = user;
        
    }

    

}
