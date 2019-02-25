/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.utils.DataBaseConnector;

/**
 *
 * @author Drew Parsons
 */
public class Customer
{

    
    private StringProperty CustomerName;
    private StringProperty Address;
    private StringProperty City;
    private StringProperty Country;
    private StringProperty Phone;
    private int customerID;
    private int addressID;

    /**
     * Default Constructor
     */
    public Customer()
    {
        this(null, null, null, null, null);
    }

    /**
     * Constructor
     *
     * @param customerName
     * @param address
     * @param city
     * @param country
     * @param phone
     */
    public Customer(String customerName, String address, String city, String country, String phone)
    {
        this.CustomerName = new SimpleStringProperty(customerName);
        this.Address = new SimpleStringProperty(address);
        this.City = new SimpleStringProperty(city);
        this.Country = new SimpleStringProperty(country);
        this.Phone = new SimpleStringProperty(phone);
    }

    public Customer(int customerID,String customerName, int addressID,String address, String city, String country, String phone)
    {
        this.customerID = customerID;
        this.CustomerName = new SimpleStringProperty(customerName);
        this.addressID = addressID;
        this.Address = new SimpleStringProperty(address);
        this.City = new SimpleStringProperty(city);
        this.Country = new SimpleStringProperty(country);
        this.Phone = new SimpleStringProperty(phone);
    }

    //Getters and Setters

    public int getCustomerID()
    {
        return customerID;
    }

    public int getAddressID()
    {
        return addressID;
    }
    
    

    /**
     * @return the customerName Property
     */
    public StringProperty CustomerNameProperty()
    {
        return CustomerName;
    }

    public String getCustomerName()
    {
        return CustomerName.get();
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName)
    {
        CustomerName.set(customerName);
    }

    /**
     * @return the address
     */
    public StringProperty AddressProperty()
    {
        return Address;
    }

    public String getAddress()
    {
        return Address.get();
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address)
    {
        Address.set(address);
    }

    /**
     * @return the city
     */
    public StringProperty CityProperty()
    {
        return City;
    }

    public String getCity()
    {
        return City.get();
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city)
    {
        City.set(city);
    }

    /**
     * @return the country
     */
    public StringProperty CountryProperty()
    {
        return Country;
    }

    public String getCountry()
    {
        return Country.get();
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country)
    {
        Country.set(country);
    }

    /**
     * @return the phone
     */
    public StringProperty PhoneProperty()
    {
        return Phone;
    }

    public String getPhone()
    {
        return Phone.get();
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone)
    {
        Phone.set(phone);
    }

    /**
     * Adds new Customer to the DB
     */
    public void addInDB(String city) throws SQLException
    {
        int cityID;
        Date createDate = Date.valueOf(LocalDate.now());
        String sqlCountryID = "SELECT cityId FROM city WHERE city =?;";
        String sqlCustomer = "INSERT INTO customer (customerName,addressId,active,createDate,createdBy,lastUpdateBy) "
                           + "VALUES(?,last_insert_id(),1,?,?,?);";
        String sqlAddress = "INSERT INTO address (address,address2,cityId,phone,createDate,createdBy,lastUpdateBy)"
                + " VALUES(?,?,?,?,?,?,?);";
        
        try (Connection conn = DataBaseConnector.getConnection())
        {
            try(PreparedStatement ps = conn.prepareStatement(sqlCountryID))
                    
            {
                
                ps.setString(1, city);
                ResultSet rs = ps.executeQuery();
                rs.next();
                cityID = rs.getInt(1);
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlAddress))
            {
                
                ps.setString(1, getAddress());               //address
                ps.setString(2, "");                         //address2
                ps.setInt(3, cityID);       //cityId
                ps.setString(4, getPhone());                 //phone
                ps.setDate(5,createDate );//createDate
                ps.setString(6, "test");                     //createdBy
                ps.setString(7, "test");                     //lastUpdateBy
                
                ps.executeUpdate();

            }

            try (PreparedStatement ps = conn.prepareStatement(sqlCustomer))

            {

                ps.setString(1, getCustomerName());         //customerName
                ps.setDate(2, createDate);                  //createDate
                ps.setString(3, "test");                    //createdBy
                ps.setString(4, "test");                    //lastUpdateBy
                ps.executeUpdate();
            }

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Updates the Customer in the Database
     * @param city
     * @param customerID
     * @param addressID
     * @throws java.sql.SQLException
     */
    public void updateInDB(String city,int customerID,int addressID) throws SQLException
    {
        int cityID;
        Date updateLastDate = Date.valueOf(LocalDate.now());
        
       //SQL statemtents to update Customer City, and Address
        String sqlCountryID = "SELECT cityId FROM city WHERE city =?;";
        String updateCustomerSQL = "UPDATE customer SET customerName =?,lastUpdate=?,lastUpdateBy=? "
                +"WHERE customerId =?;";
        String updateAddressSQL = "UPDATE address SET address =?, cityId =?, "
                + "phone =?,lastUpdate =?,lastUpdateBy=? WHERE addressId =?;";
        
        try (Connection conn = DataBaseConnector.getConnection())
        {
            try(PreparedStatement ps = conn.prepareStatement(sqlCountryID);)
                    
            {
                
                ps.setString(1, city);
                ResultSet rs = ps.executeQuery();
                rs.next();
                cityID = rs.getInt(1);
            }
            try (PreparedStatement ps = conn.prepareStatement(updateAddressSQL))
            {
                
                ps.setString(1, getAddress());               //address
                ps.setInt(2, cityID);                           //cityId
                ps.setString(3, getPhone());                 //phone
                ps.setDate(4,updateLastDate );              //updateLast
                ps.setString(5,"test");                     //lastUpdateBy
                ps.setInt(6, addressID);
                ps.executeUpdate();

            }

            try (PreparedStatement ps = conn.prepareStatement(updateCustomerSQL))

            {

                ps.setString(1, getCustomerName());         //customerName
                ps.setDate(2, updateLastDate);                  //createDate
                ps.setString(3, "test");                    //lastUpdateBy
                ps.setInt(4, customerID);
                ps.executeUpdate();
            }

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        
    }

    public void deleteFromDB(int customerID) throws SQLException
    {
        String sqlDelete = "DELETE FROM customer WHERE customerId ="+customerID+";";
        try(Connection conn = DataBaseConnector.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlDelete);)
        {
            ps.executeUpdate();
        }
    }

}
