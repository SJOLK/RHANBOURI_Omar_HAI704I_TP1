package animal.client;

public class ErreurClientException extends Exception {

    public ErreurClientException(String message) {
        super(message);
    }

    public ErreurClientException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}