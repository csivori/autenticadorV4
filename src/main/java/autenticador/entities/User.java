package autenticador.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 30)
    private String id;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(length = 30, nullable = false)
    private String pwd;

    @Column(length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRol rol;
    @Column(nullable = false)
    private LocalDateTime createdDate;
    @Column(nullable = false)
    private LocalDateTime updatedDate;
    @Column(nullable = true)
    private LocalDateTime lastLogged;
    @Column(nullable = false)
    private LocalDate validUntil;

    public boolean isExpired() {
        return this.validUntil.isBefore(LocalDate.now());
    }

    public void logAccess(){
        lastLogged = LocalDateTime.now();
    }

    public void setNewUser() {
        LocalDateTime ahora = LocalDateTime.now();
        createdDate = ahora;
        updatedDate = ahora;
        lastLogged = null;
        if ((validUntil == null) || (validUntil.isBefore(LocalDate.now().plusDays(1)))){
            validUntil = LocalDate.now().plusMonths(2);
        }
    }

    public void setExistingUser(User oldUser) {
        LocalDateTime ahora = LocalDateTime.now();
        createdDate = oldUser.getCreatedDate();
        updatedDate = ahora;
        lastLogged = oldUser.getLastLogged();
        if ((validUntil == null) || (validUntil.isBefore(LocalDate.now().plusDays(1)))){
            validUntil = oldUser.getValidUntil();
        }
    }
}
