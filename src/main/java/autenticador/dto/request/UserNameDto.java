package autenticador.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
@Data
@AllArgsConstructor
public class UserNameDto {
    @Size(min=1, max=100, message = "El nombre debe contener entre 1 y 100 caracteres")
    private String name;
}
