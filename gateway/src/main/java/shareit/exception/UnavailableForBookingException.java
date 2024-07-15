package shareit.exception;

public class UnavailableForBookingException extends RuntimeException {
    public UnavailableForBookingException(String msg) {
        super(msg);
    }
}
