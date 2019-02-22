/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.utils;

import main.model.Customer;

/**
 *
 * @author Drew Parsons
 * @param <T>
 */
public interface ControllerInterface<T>
{
    public abstract void preloadData(T t);
    
}
