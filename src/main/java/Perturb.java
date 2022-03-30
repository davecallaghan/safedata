import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Random;

class Perturb {
    enum TEMPORAL_COMPONENT {year, month,  week, day}

    LocalDate perturb(final LocalDate date) {
        return perturb(date, TEMPORAL_COMPONENT.values()[new Random().nextInt(4)]);
    }

    LocalDate perturb(final LocalDate date, TEMPORAL_COMPONENT temporalComponent) {
        switch (temporalComponent) {
            case year:
                int diff = new Random().nextInt(10);
                if (date.isBefore(LocalDate.now())) {
                    date.minusYears(diff);
                } else {
                    date.plusYears(diff);
                }
            case month:
                 return date.plusMonths(perturbDate(date.getMonthValue(), 12));
            case week:
                return date.plusWeeks(perturbDate(date.get(WeekFields.of(Locale.US).weekOfYear()), 52));
            case day:
                return date.plusDays(perturbDate(date.getDayOfMonth(), date.lengthOfMonth()));
        }
        //TODO
        return null;
    }

    private LocalDate perturbYear(final LocalDate date) {
        return null;
    }


    private int perturbDate(final int dateVal, final int upperBound) {
        int lowerBound = upperBound - 1;
        int random = new Random().nextInt(upperBound + lowerBound) - lowerBound;

        if (random == 0 || dateVal + random > upperBound || dateVal + random < 1) {
            return perturbDate(dateVal, upperBound);
        } else {
            return random;
        }
    }

    private int perturbWeek(int week) {
        int random = new Random().nextInt(50 + 50) - 50;

        if (random == 0 || week + random > 52 || week + random < 1) {
            return perturbWeek(week);
        } else {
            return random;
        }
    }

    private int perturbDay(int day) {
        int random = new Random().nextInt(26 + 26) - 26;

        if (random == 0 || day + random > 28 || day + random < 1) {
            return perturbDay(day);
        } else {
            return random;
        }
    }


}
