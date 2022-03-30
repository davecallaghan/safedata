import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Random;

public class PerturbTest {

    @Test
    public void test() {
        int result = sum(10);
        //System.out.println(result);
    }

    int sum(int k) {
        if (k > 0) {
            //System.out.println(k);
            //System.out.println(k-1);
            //System.out.println("-------");
            return k + sum(k-1);
        } else {
            return 0;
        }
    }

    @Test
    public void testMonth() {
        int month = new Random().nextInt(12) + 1;
        LocalDate date = LocalDate.now();
        int a = perturbMonth(date.getMonthValue());
        System.out.println("result : " + date.plusMonths(a));
    }

    int perturbMonth(int month) {

        int random = new Random().nextInt(10 + 10) - 10;
        System.out.println(month + " " + random);

        if (random == 0 || month + random > 12 || month + random < 1) {
            System.out.println("try again");
            return perturbMonth(month);
        } else {
            System.out.println("works");
            return random;
        }

    }

}