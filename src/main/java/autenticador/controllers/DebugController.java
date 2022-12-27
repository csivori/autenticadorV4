package autenticador.controllers;

import autenticador.dto.request.*;
import autenticador.dto.response.ResponseDto;
import autenticador.dto.response.UserDtoResponse;
import autenticador.entities.Token;
import autenticador.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/auth-debug")
public class DebugController {
    @Autowired
    private IUserService userService;

    @GetMapping("/listarTokens")
    public ResponseEntity<List<Token>> listar() {
        return new ResponseEntity<>(userService.listarTokens(), HttpStatus.OK);
    }
}
