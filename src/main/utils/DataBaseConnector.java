
package main.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Drew Parsons
 */
public class DataBaseConnector 
{
    private final static String CONNURL = "jdbc:mysql://52.206.157.109:3306/U04W9r";
    private final static String USER = "U04W9r";
    private final static String PASSWORD = "53688361581";
    private  static Connection CONN; 
    
    public static Connection getConnection()
    {
        
        try
        {
         CONN = DriverManager.getConnection(CONNURL,USER,PASSWORD);
          
        } catch (SQLException ex)
        {
            Logger.getLogger(DataBaseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    return CONN;  
    }

    
    
}
