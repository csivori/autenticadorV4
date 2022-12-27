package autenticador.service;

import autenticador.dto.request.*;
import autenticador.dto.response.ResponseDto;
import autenticador.dto.response.UserDtoResponse;
import autenticador.entities.Token;
import autenticador.entities.User;

import java.time.LocalDateTime;
import java.util.List;

public interface IUserService {

    public List<String> ayuda();

// Solo para debugging
    public List<Token> listarTokens();

    public ResponseDto autenticar(UserLoginDtoRequest uLoginDto);

    ResponseDto validarSesion(TokenDto tokenDto);

    public ResponseDto cargar(UserDtoRequest uDto, TokenDto tokenDto);

    ResponseDto modificarXId(UserDtoRequest uDto, TokenDto tokenDto);

    ResponseDto borrarXId(UserIdDto idDto, TokenDto tokenDto);

    List<UserDtoResponse> listar(TokenDto tokenDto);

    List<UserDtoResponse> listar(UserNameDto nombreDto, TokenDto tokenDto);

    List<UserDtoResponse> listarCreadosEntreFechas(String desde, String hasta, TokenDto tokenDto);

    List<UserDtoResponse> listarInactivos(int dias, TokenDto tokenDto);
}
