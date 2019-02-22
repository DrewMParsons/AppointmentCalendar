/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.model.Customer;
import main.model.User;
import main.utils.ControllerInterface;

/**
 *
 * @author Drew Parsons
 */
public class SceneChanger
{
    private static User loggedInUser;
    
    //Getters and Setters
    
    public static User getLoggedInUser()
    {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser)
    {
        SceneChanger.loggedInUser = loggedInUser;
    }
    /**
     * Method handles an event and accepts the .fxml file for a view and the Title
     * of the new scene to load
     * @param event
     * @param viewName
     * @param title
     * @throws IOException 
     */
    public void changeScenes(ActionEvent event, String viewName, String title) throws IOException
    {
        URL url = (getClass().getResource(viewName));
        FXMLLoader loader = new FXMLLoader(url);
        
        Parent parent = loader.load();
        
        Scene scene = new Scene(parent);
        
        //get the stage from the event that was passed in
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * This method will handleEvent by changing scenes, setting title for new
     * scene, and preload the new scene with the object provided
     * @param event
     * @param viewName
     * @param title
     * @param customer
     * @param controller
     * @throws java.io.IOException
     */
    public void changeScenes(ActionEvent event, String viewName, String title,Customer customer,ControllerInterface controller) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();
        
        Scene scene = new Scene(parent);
        
        //access the controller class and preload the  data
        controller = loader.getController();
        controller.preloadData(customer);
        
        //get the stage from the event that was passed in
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
        
    }

    
    public void changeScenes(ActionEvent event, String viewName, String title, User user, ControllerInterface controller) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();
        
        Scene scene = new Scene(parent);
        
        //access the controller class and preload the  data
        controller = loader.getController();
        controller.preloadData(user);
        
        //get the stage from the event that was passed in
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
    
}
