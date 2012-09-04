/**
 * 
 */
package p2pct.utils;

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