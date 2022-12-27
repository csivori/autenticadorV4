package autenticador.exceptions;

public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException(String tokenId) {super("El Token " + tokenId + " no se encontró");}
    public TokenNotFoundException(String tokenId, String userId) {super("El Token " + tokenId + " del ID de Usuario " + userId + " no se encontró");}
}
