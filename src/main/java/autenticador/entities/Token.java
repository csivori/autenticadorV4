package autenticador.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @Column(name = "tokenValue")
    private String tokenValue;
    @Column(length = 30, nullable = false)
    private String idUser;
    @Column(nullable = false)
    private LocalDateTime createdDateTime;
    @Column(nullable = false)
    private LocalDateTime validUntil;

    public Token(String idUser) {
        this.tokenValue = TokenValue.getNewTokenValue();
        this.idUser = idUser;
        this.createdDateTime = LocalDateTime.now();
        this.validUntil = LocalDateTime.now().plusMinutes(10);
    }

    public boolean isExpired() {
        return getValidUntil().isBefore(LocalDateTime.now());
    }

//    public void setNewTokenValue(List<Token> tokens) {
//        this.tokenValue.setNewTokenValue();
//        while (!esValidoElNuevoToken(tokens)) {
//            this.tokenValue.setNewTokenValue();
//        }
//    }

    // Métodos Privados
    private boolean esValidoElNuevoToken(List<Token> tokens){
        for (Token tk: tokens){
            if (tk.getTokenValue().equals(this.tokenValue)){return false;}
        }
        return true;
    }

}