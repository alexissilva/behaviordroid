package behaviordroid.util;

/**
 * Created by Alexis on 07-09-15.
 *
 * Throw to indicate that the union of two o more automatons represents an inconsistent specification.
 * It happens when an unified state has both red and green behavior types.
 */
public class InconsistentSpecificationException extends Exception {

    public InconsistentSpecificationException() {
    }

    public InconsistentSpecificationException(String detailMessage) {
        super(detailMessage);
    }
}
