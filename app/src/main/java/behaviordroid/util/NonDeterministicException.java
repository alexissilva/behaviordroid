package behaviordroid.util;

/**
 * Created by Alexis on 11-09-15.
 */
public class NonDeterministicException extends Exception{

    public NonDeterministicException() {
    }

    public NonDeterministicException(String detailMessage) {
        super(detailMessage);
    }
}
