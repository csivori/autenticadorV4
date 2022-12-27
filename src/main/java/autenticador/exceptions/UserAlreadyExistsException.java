package autenticador.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String usuario) {
        super("El ID de Usuario " + usuario + " ya existe. Intente con otro ID");
    }
}
