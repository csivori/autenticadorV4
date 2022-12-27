package autenticador.exceptions;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException() {super("El Token ha Expirado!");}
}
