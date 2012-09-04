/**
 * Copyright 2012 CSG@IFI
 * 
 * This file is part of Radiommender.
 * 
 * Radiommender is free software: you can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Radiommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Radiommender. If not, see 
 * http://www.gnu.org/licenses/.
 * 
 */
package org.radiommender.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

/**
 * 
 * 
 * @author nicolasbaer
 */
public class ConfigurationFactory {
	
	private final static String configPath = "app.cfg.xml";
                
    public static String getProperty(String key){
            try {
                    // initialize properties
                    Properties properties = new Properties();
                    
                    // read properties from xml file stream
                    properties.loadFromXML(new FileInputStream(configPath));
                    
                    // entry entry by key
                return properties.getProperty(key);
            } catch (Exception e) {
                    // on an exception return null, mostly file not found...
                    e.printStackTrace();
                    return null;
            }
    }
    
    public static void setProperty(String key, String value){
            try {
                    // initialize properties
                    Properties properties = new Properties();
                    
                    // read properties from xml file stream
                    properties.loadFromXML(new FileInputStream(configPath));

                    // write changes
                    properties.put(key, value);
                    
                    properties.storeToXML(new FileOutputStream(configPath), "last changed: "+new Date());
            } catch (Exception e) {
                    // on an exception return null, mostly file not found...
                    e.printStackTrace();
            }
    }
}