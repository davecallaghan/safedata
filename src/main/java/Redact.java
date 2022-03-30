
class Redact {

    public enum DIRECTION{ front, back}

    String redact(final String value, int offset, DIRECTION direction) {
        if (value.length() < offset)  throw new IllegalArgumentException("Offset cannot be larger than value");
        if (offset <= 0)  throw new IllegalArgumentException("Offset must be greater than zero");

        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        switch (direction) {
            case front:
                sb.append(redact(value.substring(0, offset)));
                sb.append(value.substring(offset));
                break;
            case back:
                sb.append(value.substring(0, offset));
                sb.append(redact(value.substring(offset, value.length())));
                break;
            default:
                sb.append(redact(value));
                break;
        }
        return sb.toString();
    }

    String redact(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        value.chars().forEach(c -> {
            if (Character.isLetter(c)) {
                sb.append("X");
            } else if (Character.isDigit(c)) {
                sb.append("9");
            } else {
                sb.append(Character.toString(c));
            }
        });

        return sb.toString();
    }




}
