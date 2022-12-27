package autenticador.controllers;

import autenticador.dto.request.*;
import autenticador.dto.response.ResponseDto;
import autenticador.dto.response.UserDtoResponse;
import autenticador.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/user/crear")
    public ResponseEntity<ResponseDto> crear(@Valid @RequestBody UserDtoRequest uDto, @RequestParam TokenDto t) {
        return new ResponseEntity<>(userService.cargar(uDto, t), HttpStatus.OK);
    }

    @PutMapping("/user/modificar")
    public ResponseEntity<ResponseDto> modificar(@Valid @RequestBody UserDtoRequest uDto, @RequestParam TokenDto t) {
        return new ResponseEntity<>(userService.modificarXId(uDto, t), HttpStatus.OK);
    }

    @DeleteMapping("/user/borrar")
    public ResponseEntity<ResponseDto> borrar(@RequestBody UserIdDto UserId, @RequestParam TokenDto t) {
        return new ResponseEntity<>(userService.borrarXId(UserId, t), HttpStatus.OK);
    }

    @GetMapping("/user/listar")
    public ResponseEntity<List<UserDtoResponse>> listar(@RequestParam TokenDto t) {
        return new ResponseEntity<>(userService.listar(t), HttpStatus.OK);
    }

    @GetMapping("/user/listar/{name}")
    public ResponseEntity<List<UserDtoResponse>> listar(@PathVariable("name") String name, @RequestParam TokenDto t) {
        UserNameDto uName = new UserNameDto(name);
        return new ResponseEntity<>(userService.listar(uName, t), HttpStatus.OK);
    }

    @GetMapping("/user/listarInactivos/{dias}")
    public ResponseEntity<List<UserDtoResponse>> listar(@PathVariable("dias") int dias,
                                                        @RequestParam TokenDto t) {
        return new ResponseEntity<>(userService.listarInactivos(dias, t), HttpStatus.OK);
    }

    @GetMapping("/user/listarCreadosEntre")
    public ResponseEntity<List<UserDtoResponse>> listar(@RequestParam String desde,
                                                        @RequestParam String hasta,
                                                        @RequestParam TokenDto t) {
        return new ResponseEntity<>(userService.listarCreadosEntreFechas(desde, hasta, t), HttpStatus.OK);
    }

    @PostMapping("/autenticar")
    public ResponseEntity<ResponseDto> autenticar(@Valid @RequestBody UserLoginDtoRequest uLoginDto) {
        return new ResponseEntity<>(userService.autenticar(uLoginDto), HttpStatus.OK);
    }

    @GetMapping("/validarSesion")
    public ResponseEntity<ResponseDto> validarSesionGet(@RequestParam TokenDto t) {
        return validarSesion(t);
    }

    @PostMapping("/validarSesion")
    public ResponseEntity<ResponseDto> validarSesionPost(@RequestParam TokenDto t) {
        return validarSesion(t);
    }

    @GetMapping("*")
    public ResponseEntity<List<String>> ayudaGet() {
        return ayuda();
    }

    @PostMapping("*")
    public ResponseEntity<List<String>> ayudaPost() {
        return ayuda();
    }

// *****************************************************
// *********  M é t o d o s   P r i v a d o s  *********
// *****************************************************

    private ResponseEntity<ResponseDto> validarSesion(@RequestParam TokenDto t) {
        return new ResponseEntity<>(userService.validarSesion(t), HttpStatus.OK);
    }

    private ResponseEntity<List<String>> ayuda() {
        return new ResponseEntity<>(userService.ayuda(), HttpStatus.OK);
    }

}
