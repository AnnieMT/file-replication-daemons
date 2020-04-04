package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	
	protected static Properties prop = null;
	
	public static void Load() {		
		try {
			prop = new Properties();
			InputStream inStream = new FileInputStream("./config/config.properties");
			prop.load(inStream);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {			
		}
	}
	
	
	public static String getValue(String key)
	{
		if (prop != null)
			return prop.getProperty(key);
		
		return null;
	}
}
