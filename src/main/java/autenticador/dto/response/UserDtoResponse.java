package autenticador.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDtoResponse {
    private String id;
    private String name;
    private String email;
    private String rol;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime lastLogged;
    private LocalDate validUntil;
}
