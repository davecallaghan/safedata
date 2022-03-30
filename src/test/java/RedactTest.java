import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.apache.commons.lang3.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

public class RedactTest {

    @Test
    public void redactBasicFull() {
        // given an ASCII string
        String test = "ABC123";

        // when a redaction operation is called
        Redact redact = new Redact();
        String result = redact.redact(test);

        // then the original string is replaced by a random string of the same size
        assertNotEquals(test, result);
        assertEquals(test.length(), result.length());
        assertTrue(StringUtils.isAlphanumeric(result));
        assertEquals(result, "XXX999");
        assertTrue(LevenshteinDistance.getDefaultInstance().apply(test, result) > 5);
    }

    @Test
    public void redactBasicPartialFront() {
        // given an ASCII string
        String test = "ABC123";

        // when a redaction operation is called
        Redact redact = new Redact();
        String result = redact.redact(test, 3, Redact.DIRECTION.front);

        // then the original string is replaced by a random string of the same size
        assertNotEquals(test, result);
        assertEquals(test.length(), result.length());
        assertTrue(StringUtils.isAlphanumeric(result));
        assertEquals(result, "XXX123");
        //assertTrue(LevenshteinDistance.getDefaultInstance().apply(test, result) > 5);
    }

    @Test
    public void redactBasicPartialBack() {
        // given an ASCII string
        String test = "ABC123";

        // when a redaction operation is called
        Redact redact = new Redact();
        String result = redact.redact(test, 3, Redact.DIRECTION.back);

        // then the original string is replaced by a random string of the same size
        assertNotEquals(test, result);
        assertEquals(test.length(), result.length());
        assertTrue(StringUtils.isAlphanumeric(result));
        assertEquals(result, "ABC999");
        //assertTrue(LevenshteinDistance.getDefaultInstance().apply(test, result) > 5);
    }

    //TOD think through how to handle null and empty
    /*
    @Test
    public void redactNull() {
        // given an ASCII string
        String test = null;

        // when a redaction operation is called
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Redact redact = new Redact();
            String result = redact.redact(test);
        });
    }

    @Test
    public void redactEmpty() {
        // given an ASCII string
        String test = "  ";

        // when a redaction operation is called
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Redact redact = new Redact();
            String result = redact.redact(test);
        });
    }
*/
    @Test
    public void redactSSNFull() {
        // given an ASCII string
        String test = "123-45-7890";

        // when a redaction operation is called
        Redact redact = new Redact();
        String result = redact.redact(test);

        // then the original string is replaced by a random string of the same size
        assertNotEquals(test, result);
        assertEquals(test.length(), result.length());
        assertFalse(StringUtils.isAlphanumeric(result));
        assertEquals(result, "999-99-9999");
        assertTrue(LevenshteinDistance.getDefaultInstance().apply(test, result) > 5);
    }

    @Test
    public void redactAddressFull() {
        // given an ASCII string
        String test = "1313 Mockingbird Ln.";

        // when a redaction operation is called
        Redact redact = new Redact();
        String result = redact.redact(test);

        // then the original string is replaced by a random string of the same size
        assertNotEquals(test, result);
        assertEquals(test.length(), result.length());
        assertFalse(StringUtils.isAlphanumeric(result));
        assertEquals(result, "9999 XXXXXXXXXXX XX.");

        assertTrue(LevenshteinDistance.getDefaultInstance().apply(test, result) > 5);
    }

    @Test
    public void redactPhoneNumberFull() {
        // given an ASCII string
        String test = "(555) 867-5309";

        // when a redaction operation is called
        Redact redact = new Redact();
        String result = redact.redact(test);

        // then the original string is replaced by a random string of the same size
        assertNotEquals(test, result);
        assertEquals(test.length(), result.length());
        assertFalse(StringUtils.isAlphanumeric(result));
        assertEquals(result, "(999) 999-9999");

        assertTrue(LevenshteinDistance.getDefaultInstance().apply(test, result) > 5);
    }

    @Test
    public void redactCreditCardFull() {
        // given an ASCII string
        String test = "6011000990139424";

        // when a redaction operation is called
        Redact redact = new Redact();
        String result = redact.redact(test);

        // then the original string is replaced by a random string of the same size
        assertNotEquals(test, result);
        assertEquals(test.length(), result.length());
        assertTrue(StringUtils.isAlphanumeric(result));
        assertEquals(result, "9999999999999999");

        assertTrue(LevenshteinDistance.getDefaultInstance().apply(test, result) > 5);
    }

}
