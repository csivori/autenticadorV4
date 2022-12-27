package autenticador.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdDto {
    @Size(min=5, max=20, message = "El ID de usuario debe contener entre 5 y 20 caracteres")
    private String id;
}
