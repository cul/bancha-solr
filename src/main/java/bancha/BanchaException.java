package bancha;


@SuppressWarnings("serial")
public class BanchaException extends Exception {

    public BanchaException(String message, Exception e) {
        super(message, e);
    }

}