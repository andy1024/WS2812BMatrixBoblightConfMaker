package org.warheim.boblight;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author andy
 */
public class Preferences {
    private static final Properties props = new Properties();

    public Preferences() throws IOException {
        String resourceName = "config.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream resourceStream = loader.getResourceAsStream(resourceName);
        props.load(resourceStream);
        resourceStream.close();
    }
    
    String getProperty(String key) {
        return (String)props.get(key);
    }
}
