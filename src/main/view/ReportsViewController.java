package main.view;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.utils.DataBaseConnector;

/**
 * FXML Controller class
 *
 * @author Drew Parsons
 */
public class ReportsViewController implements Initializable
{

    @FXML
    private Label reportsHeaderLabel;
    @FXML
    private BarChart<String, Integer> reportsBarChart;
    @FXML
    private NumberAxis appointmentCount;
    @FXML
    private CategoryAxis month;
    @FXML
    private Button typeButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button backButton;
    private XYChart.Series casualSeries;
    private XYChart.Series generalSeries;
    private XYChart.Series presentationSeries;
    private XYChart.Series contractSeries;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        casualSeries = new XYChart.Series<>();
        generalSeries = new XYChart.Series<>();
        presentationSeries = new XYChart.Series<>();
        contractSeries = new XYChart.Series<>();

        casualSeries.setName("Casual");
        generalSeries.setName("General");
        presentationSeries.setName("Presentation");
        contractSeries.setName("Contract");
        try
        {
            populateTypeByMonthFromDB();
        } catch (SQLException ex)
        {
            Logger.getLogger(ReportsViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        reportsBarChart.getData().addAll(casualSeries);
        reportsBarChart.getData().addAll(generalSeries);
        reportsBarChart.getData().addAll(presentationSeries);
        reportsBarChart.getData().addAll(contractSeries);

    }

    @FXML
    private void backButtonHandler(ActionEvent event) throws IOException
    {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "CustomerTableView.fxml", "Customer Table");
    }

    @FXML
    private void typeButtonHandler(ActionEvent event)
    {
        reportsBarChart.getData().clear();
        reportsBarChart.setTitle("Appointment Types by Month");
        casualSeries = new XYChart.Series<>();
        generalSeries = new XYChart.Series<>();
        presentationSeries = new XYChart.Series<>();
        contractSeries = new XYChart.Series<>();

        casualSeries.setName("Casual");
        generalSeries.setName("General");
        presentationSeries.setName("Presentation");
        contractSeries.setName("Contract");

        try
        {
            populateTypeByMonthFromDB();
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        reportsBarChart.getData().addAll(casualSeries);
        reportsBarChart.getData().addAll(generalSeries);
        reportsBarChart.getData().addAll(presentationSeries);
        reportsBarChart.getData().addAll(contractSeries);
    }

    /**
     * Method connects to database and populates the Series' for the type report
     */
    private void populateTypeByMonthFromDB() throws SQLException
    {
        String sql = "SELECT type, monthname(start), COUNT(type) "
                + "FROM appointment "
                + "GROUP BY type, MONTH(start) "
                + "ORDER BY MONTH(start);";

        try (Connection conn = DataBaseConnector.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);)
        {
            while (rs.next())
            {
                if (rs.getString(1).equalsIgnoreCase("casual"))
                {
                    casualSeries.getData().add(new XYChart.Data(rs.getString(2), rs.getInt(3)));
                } else if (rs.getString(1).equalsIgnoreCase("general"))
                {
                    generalSeries.getData().add(new XYChart.Data(rs.getString(2), rs.getInt(3)));
                } else if (rs.getString(1).equalsIgnoreCase("presentation"))
                {
                    presentationSeries.getData().add(new XYChart.Data(rs.getString(2), rs.getInt(3)));
                } else if (rs.getString(1).equalsIgnoreCase("contract"))
                {
                    contractSeries.getData().add(new XYChart.Data(rs.getString(2), rs.getInt(3)));
                }
            }
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * This method attempts to connect to the DB and populate the Series' for
     * the user report
     */
    private void populateUserByMonth() throws SQLException
    {
        HashMap<String, XYChart.Series> seriesName = new HashMap();

        String sql = "SELECT user.username , COUNT(appointment.userId), monthname(start) "
                + "FROM user, appointment "
                + "WHERE appointment.userId = user.userId "
                + "GROUP BY userName, MONTH(start) "
                + "ORDER BY MONTH(start); ";

        //create series for each user
        //run SQL and populate the bar graph
        try (Connection conn = DataBaseConnector.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);)
        {
            while (rs.next())
            {
                //check if series exists already, if so, add data 
                if (seriesName.containsKey(rs.getString(1)))
                {
                    seriesName.get(rs.getString(1)).getData().add(new XYChart.Data(rs.getString(3), rs.getInt(2)));
                } //else create and populate new series
                else
                {
                    XYChart.Series newSeries = new XYChart.Series<>();
                    newSeries.getData().add(new XYChart.Data(rs.getString(3), rs.getInt(2)));
                    newSeries.setName(rs.getString(1));
                    seriesName.put(newSeries.getName(), newSeries);
                }

            }
        }
        //Lambda expression used to iterate through the Series in the HashMap
        //and add them to the bar graph
        seriesName.forEach((String st, Series s) ->
        {
            reportsBarChart.getData().addAll(s);
        });

    }

    /**
     * This method populates the bar graph with appointments per User by Month
     *
     * @param event
     */
    @FXML
    private void usersButtonHandler(ActionEvent event) throws SQLException
    {
        reportsBarChart.getData().clear();
        reportsBarChart.setTitle("Appointments per User by Month");
        populateUserByMonth();
    }

}
