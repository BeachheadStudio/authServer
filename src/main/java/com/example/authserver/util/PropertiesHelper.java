package com.example.authserver.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: johnduffy
 * Date: 5/8/14
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesHelper {
    final static Logger logger = LogManager.getLogger(PropertiesHelper.class);
    public static final String PROPERTIES_FILE = "default.properties";
    public static final String PROPERTIES_ARG = "properties";
    private static Properties cachedProperties;

    public static synchronized Properties getProperties() {
        if (cachedProperties == null) {
            cachedProperties = new Properties();
            //load defaults
            try {
                try (InputStream is = getDefaultPropertiesStream()) {
                    cachedProperties.load(is);
                }
                //load custom properties
                try (InputStream is = getCustomPropertiesStream()) {
                    if (is != null) {
                        cachedProperties.load(is);
                    }
                }
            } catch (IOException ex) {
                logger.error("Unable to load properties", ex);
                // if we can't read the properties - throw an exception, application will not start
                throw new RuntimeException(ex);
            }
        }
        return cachedProperties;
    }

    private static InputStream getDefaultPropertiesStream() {
        String propertiesFileLocation = System.getProperty(PROPERTIES_FILE, null);
        if(propertiesFileLocation == null) {
            return PropertiesHelper.class.getResourceAsStream("/"+PROPERTIES_FILE);
        } else {
            try {
                return new FileInputStream(propertiesFileLocation);
            } catch (FileNotFoundException e) {
                logger.error("Properties file: "+propertiesFileLocation+" was not found! Using default.");
                return PropertiesHelper.class.getResourceAsStream("/"+PROPERTIES_FILE);
            }
        }
    }

    /** will return null stream if no custom properties are set*/
    private static InputStream getCustomPropertiesStream() {
        InputStream is = null;
        String propsArg = System.getProperty(PROPERTIES_ARG);
        if (propsArg != null) {
            logger.info("found custom properties arg : " + propsArg);

            try {
                is = new FileInputStream(new File(propsArg));
            } catch (Exception e) {
                logger.error("could not load custom properties " + propsArg);
                e.printStackTrace();
            }
        }
        return is;
    }

    public static String getMatchingProperty(String input) {
        String val = "";

        try {
            //This can probably be improved without much change downstream
            Properties properties = getProperties();

            String key;
            for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
                key = (String) e.nextElement();

                if (input.matches(key)) {
                    val = properties.getProperty(key);
                    break;
                }
            }
        } catch (Exception e) {
        }

        return val;
    }

    public static HashMap<String, String> getMatchingProperties(String input) {
        HashMap<String, String> values = new HashMap<String, String>();

        try {
            //This can probably be improved without much change downstream
            Properties properties = getProperties();

            String key;
            for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
                key = (String) e.nextElement();
                if (key.matches(input)) {
                    values.put(key, properties.getProperty(key));
                }
            }
        } catch (Exception e) { }

        return values;
    }

    /**
     * Returns property by provided key or default value provided as second arg
     * @param propName property lookup key
     * @param deft default value that will be returned if property is not set
     */

    public static String getMatchingPropertyOrDefault(String propName, String deft) {
        String propValue = getMatchingProperty(propName);
        return "".equals(propValue) ? deft : propValue;
    }
}
