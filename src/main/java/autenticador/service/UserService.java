package autenticador.service;

import autenticador.dto.request.*;
import autenticador.dto.response.ResponseDto;
import autenticador.dto.response.UserDtoResponse;
import autenticador.entities.Token;
import autenticador.entities.TokenValue;
import autenticador.entities.User;
import autenticador.entities.UserRol;
import autenticador.exceptions.TokenCanNotBeCreatedException;
import autenticador.exceptions.TokenExpiredException;
import autenticador.exceptions.UserNotFoundException;
import autenticador.repository.ITokenRepository;
import autenticador.repository.IUserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static autenticador.utils.Utils.*;

@Service
public class UserService implements IUserService {

    //    @Autowired
    private IUserRepository userRepository;
    private ITokenRepository tokenRepository;

    public UserService(IUserRepository userRepository, ITokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public List<String> ayuda() {
        List<String> msg = new ArrayList<>();
        ayudaHeader(msg);
        ayudaComando(msg, "/auth/", true, true, false, false,
                getList("Información como utilizar el autenticador"));
        ayudaComando(msg, "/auth/autenticar", false, true, false, false,
                getList("Se loguea con Usuario y Pwd en un JSON. Ej:{'id':<userId>, 'pwd':<pwd>}",
                        "Inicialmente: admin/admin {'id':'admin', 'pwd':'admin'}",
                        "Devuelve un Token de 10 caracteres que se debe incluir en el resto de los comandos.", ""));
        ayudaComando(msg, "/auth/validarSesion?t={token}", true, false, false, false,
                getList("Informa datos del Token."));
        ayudaComando(msg, "/auth/user/listar?t={token}", true, false, false, false,
                getList("Lista todos los Usuarios."));
        ayudaComando(msg, "/auth/user/listar/{nombre}?t={token}", true, false, false, false,
                getList("Lista todos los Usuarios que coinciden con el 'nombre'",
                        "Permite utilizar comodines como % para frase y ? para caracter."));
        ayudaComando(msg, "/auth/user/listarInactivos/{dias}?t={token}", true, false, false, false,
                getList("Lista todos los Usuarios que no se han logueado en los últimos 'dias'."));
        ayudaComando(msg, "/auth/user/listarCreadosEntre?t={token}&desde=AAAAMMDD&hasta=AAAAMMDD", true, false, false, false,
                getList("Lista todos los Usuarios creados entre las fechas especificadas.",
                        "Se puede usar '*' para abrir el rango."));
        ayudaComando(msg, "/auth/user/crear?t={token}", false, true, false, false,
                getList("Crea un nuevo usuario según el JSON especificado.",
                        "{'id': 'charly', 'name': 'Carlos', 'email': 'carlos.sivori@gmail.com', 'pwd': 'martin',",
                        "'rol': ['ADMINISTRADOR' / 'SUPER_USUARIO' / 'USUARIO'], 'validUntil': '2022-12-10'}",
                        "validUntil debe otorgar al menos 24hrs de validez, caso contrario se otorgará 2 meses de validez"));
        ayudaComando(msg, "/auth/user/modificar?t={token}", false, false, true, false,
                getList("Modifica nombre, email y rol de un usuario en base al JSON provisto.",
                        "{'id': 'charly', 'name': 'Carlos', 'email': 'carlos.sivori@gmail.com',",
                        "'rol': ['ADMINISTRADOR' / 'USUARIO_1' / 'USUARIO_2']}", ""));
        ayudaComando(msg, "/auth/user/borrar?t={token}", false, false, false, true,
                getList("Borra un usuario en base al Id provisto en un JSON.",
                        "{'id': 'charly'}"));
        return msg;
    }

    @Override
    public ResponseDto autenticar(UserLoginDtoRequest uLoginDto) {
        User user = autenticarLogin(uLoginDto.getId(), codificarPwd(uLoginDto.getPwd()));
        if (user == null) {
            return new ResponseDto("El ID de Usuario " + uLoginDto.getId() + " o la Contraseña NO es Válida!. (DEBUG: Prueba con admin/admin)");
        }

        Token token = crearNuevoToken(user);
        if (token == null) {
            return new ResponseDto("No se pudo crear un token para el ID de Usuario " + uLoginDto.getId() + "!");
        } else {
            tokenRepository.save(token);
            return new ResponseDto("El ID de Usuario " + token.getIdUser() + " tiene el token " + token.getTokenValue().toString() + " Asignado! Tome nota!");
        }
    }

    @Override
    public List<Token> listarTokens() {
        return tokenRepository.findAll();
    }

    @Override
    public ResponseDto validarSesion(TokenDto tokenDto) {
        String msg = "El token " + tokenDto.getTokenValue() + " ";
        Optional<Token> token = tokenRepository.findById(tokenDto.getTokenValue());
        if (token.isPresent()) {
            msg += "pertenece al ID de Usuario " + token.get().getIdUser() + " y ";
            if (token.get().getValidUntil().isAfter(LocalDateTime.now())) {
                return new ResponseDto(msg + "es Válido hasta " + token.get().getValidUntil() + "!");
            } else {
                return new ResponseDto(msg + "VENCIÓ el " + token.get().getValidUntil() + "!");
            }
        } else {
            return new ResponseDto(msg + "NO es Válido!");
        }
    }

    @Override
    public ResponseDto cargar(UserDtoRequest uDto, TokenDto tokenDto) {

        // Verifico que el Token aún es válido
        if (esValidoElToken(tokenDto)) {

            // Convierto UserDTO -> User
            User u = mapUserDto2User(uDto);
            u.setNewUser();
            String msg = "El Usuario " + u.getName() + " (" + u.getId() + ") ";

            // Verifico Usuario duplicado
            Optional<User> uOld = userRepository.findById(u.getId());
            if (uOld.isPresent()) {
                return new ResponseDto(msg + "YA existe en la Base de Datos!");
            }
            // Cargo Usuario Nuevo y Creo la respuesta
            if (userRepository.save(u) == null) {
                return new ResponseDto(msg + "NO pudo ser creado!");
            } else {
                return new ResponseDto(msg + "fue creado exitosamente!");
            }
        } else {
            throw new TokenExpiredException();
        }
    }

    @Override
    public ResponseDto modificarXId(UserDtoRequest uDto, TokenDto tokenDto) {

        // Verifico que el Token aún es válido
        if (esValidoElToken(tokenDto)) {

            // Convierto UserDTO -> User
            User u = mapUserDto2User(uDto);

            // Modifico Usuario por ID y Creo la respuesta
            String msg = "El Usuario " + u.getName() + " (" + u.getId() + ") ";
            Optional<User> uOld = userRepository.findById(u.getId());
            if (uOld.isPresent()) {
                u.setExistingUser(uOld.get());
                try {
                    userRepository.save(u);
                    return new ResponseDto(msg + "fue modificado exitosamente!");
                } catch (Exception e) {
                    return new ResponseDto(msg + "NO pudo ser modificado!");
                }
            } else {
                return new ResponseDto(msg + "NO pudo ser encontrado!");
            }
        } else {
            throw new TokenExpiredException();
        }
    }

    @Override
    public ResponseDto borrarXId(UserIdDto id, TokenDto tokenDto) {

        // Verifico que el Token aún es válido
        if (esValidoElToken(tokenDto)) {

            // Borro Usuario por ID y Creo la respuesta
            String msg = "El Usuario con ID: " + id.getId() + " ";
            if (userRepository.existsById(id.getId())) {
                try {
                    userRepository.deleteById(id.getId());
                    return new ResponseDto(msg + "fue borrado exitosamente!");
                } catch (UserNotFoundException e) {
                    return new ResponseDto(msg + "NO pudo ser borrado!");
                }
            } else {
                return new ResponseDto(msg + "NO pudo ser encontrado!");
            }
        } else {
            throw new TokenExpiredException();
        }
    }

    @Override
    public List<UserDtoResponse> listar(TokenDto tokenDto) {

        // Verifico que el Token aún es válido
        if (esValidoElToken(tokenDto)) {

            // Recupero todos los Usuarios
            List<User> p = userRepository.findAll();

            // Convierto Users -> UsersDTO
            return mapUsers2UsersDto(p);
        } else {
            throw new TokenExpiredException();
        }
    }

    @Override
    public List<UserDtoResponse> listar(UserNameDto nombreDto, TokenDto tokenDto) {

        // Verifico que el Token aún es válido
        if (esValidoElToken(tokenDto)) {

            // Recupero todos los Usuarios por Nombre
            List<User> p;
            String nombreABuscar = nombreDto.getName();
            if ((nombreABuscar.contains("%")) || (nombreABuscar.contains("?"))) {
                p = userRepository.findAllByNameLike(nombreABuscar);
            } else {
                p = userRepository.findAllByName(nombreABuscar);
            }

            // Convierto Users -> UsersDTO
            return mapUsers2UsersDto(p);
        } else {
            throw new TokenExpiredException();
        }
    }

    @Override
    public List<UserDtoResponse> listarCreadosEntreFechas(String desde, String hasta, TokenDto tokenDto) {

        // Verifico que el Token aún es válido
        if (esValidoElToken(tokenDto)) {

            // Valido las fechas
            LocalDateTime dtDesde, dtHasta;
            if (desde == null) {
                dtDesde = LocalDateTime.now().minusYears(10);
            } else {
                dtDesde = formatearFecha(desde);
                if (dtDesde == null) {
                    throw new IllegalArgumentException("La fecha Desde '" + desde + "' es INVALIDA !!.  El formato correcto es: AAAAMMDD");
                }
            }

            if (hasta == null) {
                dtHasta = LocalDateTime.now();
            } else {
                dtHasta = formatearFecha(hasta);
                if (dtHasta == null) {
                    throw new IllegalArgumentException("La fecha Hasta '" + hasta + "' es INVALIDA !!.  El formato correcto es: AAAAMMDD");
                }
            }

            if (dtDesde.isAfter(dtHasta)) {
                System.out.println("Fecha Desde " + dtDesde.toString() + " mayor que Fecha Hasta" + dtHasta.toString() + ". Corregido!!");
                LocalDateTime dtAux = dtHasta;
                dtHasta = dtDesde;
                dtDesde = dtAux;
            }

            // Recupero todos los Usuarios que no se han logueado en los últimos 'dias'
            List<User> p = userRepository.findAllByCreatedDateBetween(dtDesde, dtHasta);

            // Convierto Users -> UsersDTO
            return mapUsers2UsersDto(p);
        } else {
            throw new TokenExpiredException();
        }
    }

    @Override
    public List<UserDtoResponse> listarInactivos(int dias, TokenDto tokenDto) {

        // Verifico que el Token aún es válido
        if (esValidoElToken(tokenDto)) {

            // Recupero todos los Usuarios que no se han logueado en los últimos 'dias'
            List<User> p = userRepository.findAllInactiveUsers(dias);

            // Convierto Users -> UsersDTO
            return mapUsers2UsersDto(p);
        } else {
            throw new TokenExpiredException();
        }
    }

// *****************************************************
// *********  M é t o d o s   P r i v a d o s  *********
// *****************************************************

    private User autenticarLogin(String id, String pwd) {
        Optional<User> u = userRepository.findById(id);
        if (u.isPresent()) {
            User user = u.get();
            if (user.getPwd().equals(pwd)) {
                if (!user.isExpired()) {
                    user.logAccess();
                    return user;
                }
            }
        }
        return null;
    }

    private boolean esValidoElToken(TokenDto tokenDto) {
        Optional<Token> t = tokenRepository.findById(tokenDto.getTokenValue());
        if (t.isPresent()) {
            return (!t.get().isExpired());
        }
        return false;
    }

    private Token crearNuevoToken(User u) {

        // Valido que el Usuario esta vigente
        if (u.getValidUntil().isBefore(LocalDate.now())) {
            throw new TokenCanNotBeCreatedException("No se pudo generar el nuevo Token, El Usuario ha expirado. Renueve el Usuario");
        }

        // Creo un nuevo Token y valido que el generador random no lo haya duplicado
        Token t = new Token(u.getId());

        int intentos = 5;
        while ((intentos > 0) && (tokenRepository.existsById(t.getTokenValue()))) {
            t.setTokenValue(TokenValue.getNewTokenValue());
            intentos--;
        }

        if (intentos > 0) {
            return t;
        }
        throw new TokenCanNotBeCreatedException("No se pudo generar un nuevo Token " + t.getTokenValue() + " distinto a los existentes en el repositorio");
    }

    // Métodos Mapper de Conversión

    private User mapUserDto2User(UserDtoRequest uDto) {
        if (uDto == null) {
            return null;
        }

        UserRol userRol = null;
        try {
            userRol = UserRol.valueOf(uDto.getRol());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El rol '" + uDto.getRol() + "' es INVALIDO !!");
        } catch (NullPointerException e) {
            userRol = UserRol.USUARIO;
        }
        return new User(uDto.getId(), uDto.getName(), uDto.getEmail(),
                codificarPwd(uDto.getPwd()), userRol, uDto.getCreatedDate(), uDto.getUpdatedDate(),
                uDto.getLastLogged(), uDto.getValidUntil());
    }

    private UserDtoResponse mapUser2UserDto(User u) {
        if (u == null) {
            return null;
        }

        return new UserDtoResponse(u.getId(), u.getName(), u.getEmail(), u.getRol().name(),
                u.getCreatedDate(), u.getUpdatedDate(), u.getLastLogged(), u.getValidUntil());
    }

    private List<UserDtoResponse> mapUsers2UsersDto(List<User> p) {
        if (p == null) {
            return null;
        }

        return p.stream().map(u -> mapUser2UserDto(u)).collect(Collectors.toList());
    }

    public void ayudaHeader(List<String> msg) {
        msg.add("***********************************");
        msg.add("***  SERVICIO DE AUTENTICACION  ***");
        msg.add("***********************************");
        msg.add("");
    }

    public void ayudaComando(List<String> msg, String comando, boolean get, boolean post, boolean put, boolean del, List<String> ayuda) {
        String acciones = "";
        if (get) {
            acciones += "GET";
        }
        if (post) {
            acciones += (acciones == "" ? "" : ", ") + "POST";
        }
        if (put) {
            acciones += (acciones == "" ? "" : ", ") + "PUT";
        }
        if (del) {
            acciones += (acciones == "" ? "" : ", ") + "DEL";
        }
        msg.add("Comando: [" + acciones + "] " + comando);

        for (String s : ayuda) {
            msg.add("         " + s);
        }
        msg.add("");
    }
}