import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ConfigurationUtil {

    public ConfigurationUtil() {
        try {
            URL url =  getClass().getClassLoader().getResource("application.yaml");
            YAMLConfiguration yamlConfiguration = new YAMLConfiguration();
            InputStream inputStream = url.openStream();
            yamlConfiguration.read(inputStream);
            //return new Configuration.Builder()
            //        .build();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
