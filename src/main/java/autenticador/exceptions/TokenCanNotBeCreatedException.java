package autenticador.exceptions;

public class TokenCanNotBeCreatedException extends RuntimeException{
    public TokenCanNotBeCreatedException(String message) {
        super(message);
    }
}
