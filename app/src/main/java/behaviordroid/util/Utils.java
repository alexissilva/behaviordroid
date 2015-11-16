package behaviordroid.util;

/**
 * Created by Alexis on 18-07-15.
 */
public class Utils {

    private static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Transform a number to sequence of letter, for example:
     *  1 -> A, 3 -> C, 27 -> AA, 28 -> AB ...
     *
     * @throws IllegalArgumentException if the number is negative.
     */
    public static String numberToLetters(int number) {

        if (number <= 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }

        if (number <= ALPHABET.length()) {
            return ALPHABET.charAt(number - 1) + "";
        }

        StringBuilder sb = new StringBuilder();

        int current = number;
        while (current > 0) {
            sb.append(ALPHABET.charAt(--current % ALPHABET.length()));
            current /= ALPHABET.length();
        }
        return sb.reverse().toString();
    }

}
