import lombok.Builder;
import lombok.Getter;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class EncryptionConfig {

    @Getter
    private String secretKey;

    @Getter
    private String secretKeySpec;

    @Getter
    private String ivParameter;

    @Getter
    private String transform;

    public EncryptionConfig() {
        /**
        https://www.programcreek.com/java-api-examples/?code=apache%2Fcassandra-sidecar%2Fcassandra-sidecar-master%2Fsrc%2Fmain%2Fjava%2Forg%2Fapache%2Fcassandra%2Fsidecar%2FMainModule.java#
        */
        try {
            //TODO put this in try with resources
            URL url =  getClass().getClassLoader().getResource("application.yaml");
            YAMLConfiguration yamlConfiguration = new YAMLConfiguration();
            InputStream inputStream = url.openStream();
            yamlConfiguration.read(inputStream);
            secretKey = yamlConfiguration.get(String.class, "encryption.secretKey", null);
            secretKeySpec = yamlConfiguration.get(String.class, "encryption.secretKeySpec", null);
            ivParameter = yamlConfiguration.get(String.class, "encryption.IvParameter", null);
            transform = yamlConfiguration.get(String.class, "encryption.transform", null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
