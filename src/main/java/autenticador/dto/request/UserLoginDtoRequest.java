package autenticador.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserLoginDtoRequest {
    @Size(min=5, max=20, message = "El ID de usuario debe contener entre 5 y 20 caracteres")
    private String id;
    @Size(min=5, max=20, message = "La contraseña debe contener entre 5 y 20 caracteres")
    private String pwd;
}
