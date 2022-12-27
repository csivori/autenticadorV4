package autenticador.exceptions;

import autenticador.dto.response.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionConfig {

//  LISTA de ERRORES
//  601: El Usuario buscado no se encuentra en la DB              (HTTP 400)
//  602: El ID de Usuario que se intenta agregar, ya esta en uso  (HTTP 422)
//  610: El Token buscado no se encuentra en la DB                (HTTP 400)
//  611: El Token no pudo ser creado                              (HTTP 500)
//  612: El Token esta expirado                                   (HTTP 410)

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> usuarioNoEncontradoException(UserNotFoundException e) {
        ErrorDto errorDto = new ErrorDto(601, e.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> yaExisteException(UserAlreadyExistsException e) {
        ErrorDto errorDto = new ErrorDto(602, e.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<?> tokenNoEncontradoException(TokenNotFoundException e) {
        ErrorDto errorDto = new ErrorDto(610, e.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenCanNotBeCreatedException.class)
    public ResponseEntity<?> tokenNoPuedeSerCreadoException(TokenCanNotBeCreatedException e) {
        ErrorDto errorDto = new ErrorDto(611, e.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> tokenExpiradoException(TokenExpiredException e) {
        ErrorDto errorDto = new ErrorDto(612, e.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.GONE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> argumentoInvalidoException(MethodArgumentNotValidException e) {
        //Para cuando es 1 solo error:
        //ErrorDto errorDto = new ErrorDto(400, e.getFieldError().getDefaultMessage());

        //Si puede haber mas de 1 error debo informar la lista de errores:
        List<ErrorDto> erroresDto = e.getBindingResult().getAllErrors().stream()
                .map(err -> new ErrorDto(400, err.getDefaultMessage()))
                .distinct()
                .collect(Collectors.toList());
        return new ResponseEntity<>(erroresDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> JSONInvalidoException(HttpMessageNotReadableException e) {
        ErrorDto errorDto = new ErrorDto(400, "Error parseando un JSON!    " +
                "Podría tratarse de un String en el JSON que no este encerrado entre comillas.     " +
                "Identifique el Token no reconocido en el siguiente mensaje:     " + e.getMessage() + " **** " + e.getHttpInputMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> faltaElTokenException(MissingServletRequestParameterException e) {
        ErrorDto errorDto = null;
        if (e.getParameterName().equals("t")) {
            errorDto = new ErrorDto(400, "No se ha especificado el token de la Sesión!    " +
                    "Agregue a la URL el token de 10 posiciones informado al autenticarse: '.../listar?t=xxxxxxxxxx'     " + e.getMessage() + " ****");
        } else {
            errorDto = new ErrorDto(400, "No se ha especificado el parámetro " + e.getParameterName() +
                    "!       Agregue a la URL el parámetro solicitado.        " + e.getMessage() + " ****");
        }
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> argumentoInvalidoException(IllegalArgumentException e) {
        return new ResponseEntity<>(new ErrorDto(400, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
