/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Drew Parsons
 */
public class Appointment
{

    private StringProperty Type;
    private int customerID;
    private int userID;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalTime startTime;
    private LocalTime endTime;
    private StringProperty Day;
    private LocalDate date;

    public Appointment(String type, int customerID, int userID, LocalDateTime startTime, LocalDateTime endTime)
    {
        this.Type = new SimpleStringProperty(type);
        this.customerID = customerID;
        this.userID = userID;
        this.start = startTime;
        this.end = endTime;
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
    public Appointment(String type, int customerID, int userID, Date start, Date end)
    {
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

    public Appointment(String type, int customerID, int userID)
    {
        this.Type = new SimpleStringProperty(type);
        this.customerID = customerID;
        this.userID = userID;
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

    public LocalTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(LocalTime endTime)
    {
        this.endTime = endTime;
    }

}
