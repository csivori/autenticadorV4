package autenticador.dto.request;

import autenticador.entities.UserRol;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.*;

@Data
@AllArgsConstructor
public class UserDtoRequest {
    @Size(min=5, max=20, message = "El ID de usuario debe contener entre 5 y 20 caracteres")
    private String id;
    @Size(min=1, max=100, message = "El nombre debe contener entre 1 y 100 caracteres")
    private String name;
    @Email(message = "El correo debe ser válido")
    private String email;
    @Size(min=5, max=20, message = "La contraseña debe contener entre 5 y 20 caracteres")
    private String pwd;
    private String rol;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime lastLogged;
    private LocalDate validUntil;
}
