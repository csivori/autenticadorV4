package autenticador;

import autenticador.dto.request.TokenDto;
import autenticador.dto.request.UserDtoRequest;
import autenticador.dto.request.UserIdDto;
import autenticador.dto.request.UserLoginDtoRequest;
import autenticador.dto.response.ResponseDto;
import autenticador.entities.Token;
import autenticador.entities.TokenValue;
import autenticador.service.IUserService;
import autenticador.utils.Utils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AutenticadorApplicationTests {

// Para autenticar y guardar el token de la autenticación y luego utilizarlo en el resto de los servicios
	static String userAdminId = "admin";
	static String userAdminPwd = "admin";
	static String userAdminIdErr = "adminX";
	static String userAdminPwdErr = "adminX";
	static TokenDto tokenTest;

//	Para crear un nuevo usuario
	static String userId = "charly2";
	static String userNameCreate = "Carlos Sivori";
	static String userEmailCreate = "carlos.sivori@gmail.com";
	static String userPwdCreate = "charly";
	static String userRolCreate = "SUPER_USUARIO";

//	Para modificar el usuario previamente cargado
	static String userNameUpdate = "Carlos A. Sivori";
	static String userEmailUpdate = "c.a.sivori@gmail.com";
	static String userPwdUpdate = "charly";
	static String userRolUpdate = "USUARIO";

	@Autowired
	IUserService userService;

	@Test
	@Order(1)
	void testCodificarPwd() {
	//	Arrange
		String usr = "0123456789" +
					 "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
					 "abcdefghijklmnopqrstuvwxyz";
		String usrEncriptadoOk = "123456789:" +
								 "BCDEFGHIJKLMNOPQRSTUVWXYZ[" +
								 "bcdefghijklmnopqrstuvwxyz{";

	//	Act
		String usrEncriptadoATestear = Utils.codificarPwd(usr);

	//	Assert
		assertEquals(usrEncriptadoOk, usrEncriptadoATestear);
	}

	@Test
	@Order(2)
	void autenticarUsuarioErrorPwd() {
		//	Arrange
		UserLoginDtoRequest uLoginDto = new UserLoginDtoRequest(this.userAdminId, this.userAdminPwdErr);
		ResponseDto rtaOk = new ResponseDto("El ID de Usuario " + this.userAdminId + " o la Contraseña NO es Válida!. (DEBUG: Prueba con admin/admin)");

		//	Act
		ResponseDto rtaATestear = userService.autenticar(uLoginDto);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(3)
	void autenticarUsuarioErrorUser() {
		//	Arrange
		UserLoginDtoRequest uLoginDto = new UserLoginDtoRequest(this.userAdminIdErr, this.userAdminPwd);
		ResponseDto rtaOk = new ResponseDto("El ID de Usuario " + this.userAdminIdErr + " o la Contraseña NO es Válida!. (DEBUG: Prueba con admin/admin)");

		//	Act
		ResponseDto rtaATestear = userService.autenticar(uLoginDto);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(4)
	void autenticarUsuarioOk() {
		//	Arrange
		UserLoginDtoRequest uLoginDto = new UserLoginDtoRequest(this.userAdminId, this.userAdminPwd);
		ResponseDto rtaOk = new ResponseDto("El ID de Usuario " + this.userAdminId + " tiene el token 0123456789 Asignado! Tome nota!");
		String rtaOk1 = rtaOk.getRespuesta().substring(0, 38);
		String rtaOk2 = rtaOk.getRespuesta().substring(48);

		//	Act
		ResponseDto rtaATestear = userService.autenticar(uLoginDto);
		String rta = rtaATestear.getRespuesta();
		String rtaATestear1 = rta.substring(0, 38);
		this.tokenTest = new TokenDto(rta.substring(38, 48));
		String rtaATestear2 = rta.substring(48);

		//	Assert
		assertEquals(rtaOk1, rtaATestear1);
		assertEquals(rtaOk2, rtaATestear2);
	}

	@Test
	@Order(5)
	void crearUsuarioOk() {
		//	Arrange
		UserDtoRequest uDto = new UserDtoRequest(this.userId, this.userNameCreate, this.userEmailCreate, this.userPwdCreate, this.userRolCreate, null,null,null,null);
		ResponseDto rtaOk = new ResponseDto("El Usuario " + this.userNameCreate + " (" + this.userId + ") fue creado exitosamente!");

		//	Act
		ResponseDto rtaATestear = userService.cargar(uDto, this.tokenTest);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(6)
	void crearUsuarioDuplicado() {
		//	Arrange
		UserDtoRequest uDto = new UserDtoRequest(this.userId, this.userNameCreate, this.userEmailCreate, this.userPwdCreate, this.userRolCreate, null,null,null,null);
		ResponseDto rtaOk = new ResponseDto("El Usuario " + this.userNameCreate + " (" + this.userId + ") YA existe en la Base de Datos!");

		//	Act
		ResponseDto rtaATestear = userService.cargar(uDto, this.tokenTest);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(7)
	void modificarUsuarioOk() {
		//	Arrange
		UserDtoRequest uDto = new UserDtoRequest(this.userId, this.userNameUpdate, this.userEmailUpdate, this.userPwdUpdate, this.userRolUpdate, null,null,null,null);
		ResponseDto rtaOk = new ResponseDto("El Usuario " + this.userNameUpdate + " (" + this.userId + ") fue modificado exitosamente!");

		//	Act
		ResponseDto rtaATestear = userService.modificarXId(uDto, this.tokenTest);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(8)
	void modificarUsuarioErr() {
		//	Arrange
		UserDtoRequest uDto = new UserDtoRequest(this.userId+"x", this.userNameUpdate, this.userEmailUpdate, this.userPwdUpdate, this.userRolUpdate, null,null,null,null);
		ResponseDto rtaOk = new ResponseDto("El Usuario " + this.userNameUpdate + " (" + this.userId+"x" + ") NO pudo ser encontrado!");

		//	Act
		ResponseDto rtaATestear = userService.modificarXId(uDto, this.tokenTest);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(9)
	void borrarUsuarioOk() {
		//	Arrange
		UserIdDto uIdDto = new UserIdDto(this.userId);
		ResponseDto rtaOk = new ResponseDto("El Usuario con ID: " + this.userId + " fue borrado exitosamente!");

		//	Act
		ResponseDto rtaATestear = userService.borrarXId(uIdDto, this.tokenTest);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(10)
	void borrarUsuarioErr() {
		//	Arrange
		UserIdDto uIdDto = new UserIdDto(this.userId);
		ResponseDto rtaOk = new ResponseDto("El Usuario con ID: " + this.userId + " NO pudo ser encontrado!");

		//	Act
		ResponseDto rtaATestear = userService.borrarXId(uIdDto, this.tokenTest);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(11)
	void ayuda() {
		//	Arrange
		String linea1Ok = "***********************************";
		String linea2Ok = "***  SERVICIO DE AUTENTICACION  ***";
		String linea3Ok = "***********************************";

		//	Act
		List<String> linea1ATestear = userService.ayuda();

		//	Assert
		assertEquals(linea1Ok, linea1ATestear.get(0));
		assertEquals(linea2Ok, linea1ATestear.get(1));
		assertEquals(linea3Ok, linea1ATestear.get(2));
	}

	@Test
	@Order(12)
	void validarSesionOk() {
		//	Arrange
		String rtaOk = "El token " + this.tokenTest.getTokenValue() + " pertenece al ID de Usuario " + this.userAdminId + " y es Válido hasta ";

		//	Act
		ResponseDto respATestear = userService.validarSesion(this.tokenTest);
		String rtaATestear = respATestear.getRespuesta();
		rtaATestear = rtaATestear.substring(0, rtaOk.length());

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(13)
	void validarSesionErr() {
		//	Arrange
		TokenDto t = new TokenDto(this.tokenTest.getTokenValue().substring(0,9)+"x");
		ResponseDto rtaOk = new ResponseDto("El token " + t.getTokenValue() + " NO es Válido!");

		//	Act
		ResponseDto rtaATestear = userService.validarSesion(t);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}
}
