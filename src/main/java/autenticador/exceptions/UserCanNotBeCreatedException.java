package autenticador.exceptions;

public class UserCanNotBeCreatedException extends RuntimeException{
    public UserCanNotBeCreatedException(String message) {
        super(message);
    }
}
