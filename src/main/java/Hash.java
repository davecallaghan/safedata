import org.apache.commons.crypto.stream.CryptoOutputStream;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

public class Hash {
    /**
     *
     * https://commons.apache.org/proper/commons-crypto/xref-test/org/apache/commons/crypto/examples/StreamExample.html
     * @param value
     * @return
     */
    public String encrypt(final String value) {
        final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes("1234567890123456"),"AES");
        final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
        final Properties properties = new Properties();
        final String transform = "AES/CBC/PKCS5Padding";
        final String input = value;

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //EncryptionConfig encryptionConfig = new EncryptionConfig();
        //try (CryptoOutputStream cos = new CryptoOutputStream(encryptionConfig.getTransform(), properties, outputStream, encryptionConfig.getSecretKey(), encryptionConfig.getIvParameter())) {
        try (CryptoOutputStream cos = new CryptoOutputStream(transform, properties, outputStream, key, iv)) {
            cos.write(getUTF8Bytes(input));
            cos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("encrypted: " + Arrays.toString(outputStream.toByteArray()));

        //decrypt with CryptoInputStream
        final InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        return null;
    }

    private static byte[] getUTF8Bytes(final String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }
}
