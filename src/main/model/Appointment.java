/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.utils.DataBaseConnector;

/**
 *
 * @author Drew Parsons
 */
public class Appointment
{

    private int appointmentID;
    private StringProperty Type;
    private int customerID;
    private int userID;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalTime startTime;
    private LocalTime endTime;
    private StringProperty Day;
    private LocalDate date;

    public Appointment(int appointmentId, String type, int customerID, int userID, LocalDateTime startTime, LocalDateTime endTime)
    {
        this.appointmentID = appointmentId;
        this.Type = new SimpleStringProperty(type);
        this.customerID = customerID;
        this.userID = userID;
        this.start = startTime;
        this.end = endTime;
    }

    public Appointment(String type, int customerID, int userID, LocalDate date, LocalTime startTime, LocalTime endTime)
    {

        this.Type = new SimpleStringProperty(type);
        this.customerID = customerID;
        this.userID = userID;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.start= LocalDateTime.of(date,startTime);
        this.end=LocalDateTime.of(date, endTime);
    }

    /**
     * Constructor for getting Date info from SQL and sets to LOCAL TIME
     *
     * @param type
     * @param customerID
     * @param userID
     * @param start
     * @param end
     */
    public Appointment(int appointmentId, String type, int customerID, int userID, Timestamp start, Timestamp end)
    {
        this.appointmentID = appointmentId;
        this.Type = new SimpleStringProperty(type);
        this.customerID = customerID;
        this.userID = userID;
        this.start = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.end = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.startTime = this.start.toLocalTime();
        this.endTime = this.end.toLocalTime();
        this.Day = new SimpleStringProperty(this.start.getDayOfWeek().toString());
        this.date = this.start.toLocalDate();

    }

    public int getAppointmentID()
    {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID)
    {
        this.appointmentID = appointmentID;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public StringProperty DateProperty()
    {
        return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("MMM dd")));
    }

    public String getDay()
    {
        return Day.get();
    }

    public void setDay(String day)
    {
        Day.set(day);
    }

    public StringProperty DayProperty()
    {
        return Day;
    }

    public String getType()
    {
        return Type.get();
    }

    public void setType(String type)
    {
        Type.set(type);
    }

    public StringProperty TypeProperty()
    {
        return Type;
    }

    public int getCustomerID()
    {
        return customerID;
    }

    public void setCustomerID(int customerID)
    {
        this.customerID = customerID;
    }

    public int getUserID()
    {
        return userID;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    public LocalDateTime getStart()
    {
        return start;
    }

    public void setStart(LocalDateTime start)
    {
        this.start = start;
    }

    public LocalDateTime getEnd()
    {
        return end;
    }

    public void setEnd(LocalDateTime end)
    {
        this.end = end;
    }

    public LocalTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalTime startTime)
    {
        this.startTime = startTime;
    }

    public StringProperty StartTimeProperty()
    {
        return new SimpleStringProperty(startTime.toString());
    }

    public LocalTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(LocalTime endTime)
    {
        this.endTime = endTime;
    }

    public StringProperty EndTimeProperty()
    {
        return new SimpleStringProperty(endTime.toString());
    }

    /**
     * This Method attempts to connect to the DB and delete the selected
     * Appointment from the DB
     *
     * @param appointmentId
     * @throws SQLException
     */
    public void deleteFromDB(int appointmentId) throws SQLException
    {
        String sqlDelete = "DELETE FROM appointment WHERE appointmentId =" + appointmentId + ";";
        try (Connection conn = DataBaseConnector.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlDelete);)
        {
            ps.executeUpdate();
        }
    }

    public void addInDB() throws SQLException
    {

        
        String sqlAppointment = "INSERT INTO appointment (customerId,userId,type,start,end,createDate,createdBy,lastUpdate,LastUpdateBy) "
                + "VALUES(?,?,?,?,?,?,?,?,?);";

        try (Connection conn = DataBaseConnector.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(sqlAppointment))
            {
                ps.setInt(1, getCustomerID());
                ps.setInt(2, getUserID());
                ps.setString(3, getType());
                ps.setObject(4, getStart());
                ps.setObject(5, getEnd());
                ps.setObject(6, LocalDateTime.now());
                ps.setString(7, String.valueOf(userID));
                ps.setObject(8, LocalDateTime.now());
                ps.setString(9, String.valueOf(userID));
                
                ps.executeUpdate();

            }catch(SQLException e)
            {
                System.err.println(e.getMessage());
            }
        }
    }

}
