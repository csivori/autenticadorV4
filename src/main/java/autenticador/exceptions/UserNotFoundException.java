package autenticador.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String userId) {
        super("El Usuario " + ((userId.trim().isEmpty()) ? "" : userId.trim() + " ") + "no se encontrĂ³. Verifique el ID de Usuario ingresado.");}
}
