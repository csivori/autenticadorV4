package autenticador.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

public final class TokenValue {
    public static String getNewTokenValue() {
        int precisionToken = 10;
        String t = "";
        for (int i=0; i<precisionToken; i++){
            t += getChar((int) (Math.random() * 61));
        }
        return t;
    }

    private static char getChar(int pos){
        if (pos<=9) {return (char) (pos + 48);}       // Números
        else if (pos<=35) {return (char) (pos + 55);} // Letras Mayúsculas
        else if (pos<=61) {return (char) (pos + 61);} // Letras Minúsculas
        return '_';
    }
}
