package autenticador;

import autenticador.dto.request.*;
import autenticador.dto.response.ResponseDto;
import autenticador.dto.response.UserDtoResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static autenticador.utils.Utils.date2YYYYMMAA;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AutenticadorApplicationTests {

// Para autenticar y guardar el token de la autenticación y luego utilizarlo en el resto de los servicios
	static TokenDto TOKEN_TEST;
	static String USER_ADMIN_ID = "admin";
	static String USER_ADMIN_PWD = "admin";
	static String USER_ADMIN_IDErr = "adminX";
	static String USER_ADMIN_PWDErr = "adminX";
	static String USER_ADMIN_NAME = "Administrador del Sistema";
	static String USER_ADMIN_ROL = "ADMINISTRADOR";
	static String USER_ADMIN_EMAIL = "carlos.sivori@gmail.com";
	static LocalDateTime USER_ADMIN_CREATED_DATE = LocalDateTime.of(2022, 12, 1, 0, 0);
	static LocalDate USER_ADMIN_VALID_UNTIL = LocalDate.of(2122, 12, 23);

//	Para crear un nuevo usuario
	static String TEST_USER_ID = "charly2";
	static String TEST_USER_NAMECreate = "Carlos Sivori";
	static String TEST_USER_EMAILCreate = "carlos.sivori@gmail.com";
	static String TEST_USER_PWDCreate = "charly";
	static String TEST_USER_ROLCreate = "SUPER_USUARIO";

//	Para modificar el usuario previamente cargado
	static String TEST_USER_NAMEUpdate = "Carlos A. Sivori";
	static String TEST_USER_EMAILUpdate = "c.a.sivori@gmail.com";
	static String TEST_USER_PWDUpdate = "charly";
	static String TEST_USER_ROLUpdate = "USUARIO";

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
		UserLoginDtoRequest uLoginDto = new UserLoginDtoRequest(USER_ADMIN_ID, USER_ADMIN_PWDErr);
		ResponseDto rtaOk = new ResponseDto("El ID de Usuario " + USER_ADMIN_ID + " o la Contraseña NO es Válida!. (DEBUG: Prueba con admin/admin)");

		//	Act
		ResponseDto rtaATestear = userService.autenticar(uLoginDto);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(3)
	void autenticarUsuarioErrorUser() {
		//	Arrange
		UserLoginDtoRequest uLoginDto = new UserLoginDtoRequest(USER_ADMIN_IDErr, USER_ADMIN_PWD);
		ResponseDto rtaOk = new ResponseDto("El ID de Usuario " + USER_ADMIN_IDErr + " o la Contraseña NO es Válida!. (DEBUG: Prueba con admin/admin)");

		//	Act
		ResponseDto rtaATestear = userService.autenticar(uLoginDto);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(4)
	void autenticarUsuarioOk() {
		//	Arrange
		UserLoginDtoRequest uLoginDto = new UserLoginDtoRequest(USER_ADMIN_ID, USER_ADMIN_PWD);
		ResponseDto rtaOk = new ResponseDto("El ID de Usuario " + USER_ADMIN_ID + " tiene el token 0123456789 Asignado! Tome nota!");
		String rtaOk1 = rtaOk.getRespuesta().substring(0, 38);
		String rtaOk2 = rtaOk.getRespuesta().substring(48);

		//	Act
		ResponseDto rtaATestear = userService.autenticar(uLoginDto);
		String rta = rtaATestear.getRespuesta();
		String rtaATestear1 = rta.substring(0, 38);
		TOKEN_TEST = new TokenDto(rta.substring(38, 48));
		String rtaATestear2 = rta.substring(48);

		//	Assert
		assertEquals(rtaOk1, rtaATestear1);
		assertEquals(rtaOk2, rtaATestear2);
	}

	@Test
	@Order(5)
	void crearUsuarioOk() {
		//	Arrange
		UserDtoRequest uDto = new UserDtoRequest(TEST_USER_ID, TEST_USER_NAMECreate, TEST_USER_EMAILCreate, TEST_USER_PWDCreate, TEST_USER_ROLCreate, null,null,null,null);
		ResponseDto rtaOk = new ResponseDto("El Usuario " + TEST_USER_NAMECreate + " (" + TEST_USER_ID + ") fue creado exitosamente!");

		//	Act
		ResponseDto rtaATestear = userService.cargar(uDto, TOKEN_TEST);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(6)
	void crearUsuarioDuplicado() {
		//	Arrange
		UserDtoRequest uDto = new UserDtoRequest(TEST_USER_ID, TEST_USER_NAMECreate, TEST_USER_EMAILCreate, TEST_USER_PWDCreate, TEST_USER_ROLCreate, null,null,null,null);
		ResponseDto rtaOk = new ResponseDto("El Usuario " + TEST_USER_NAMECreate + " (" + TEST_USER_ID + ") YA existe en la Base de Datos!");

		//	Act
		ResponseDto rtaATestear = userService.cargar(uDto, TOKEN_TEST);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(7)
	void modificarUsuarioOk() {
		//	Arrange
		UserDtoRequest uDto = new UserDtoRequest(TEST_USER_ID, TEST_USER_NAMEUpdate, TEST_USER_EMAILUpdate, TEST_USER_PWDUpdate, TEST_USER_ROLUpdate, null,null,null,null);
		ResponseDto rtaOk = new ResponseDto("El Usuario " + TEST_USER_NAMEUpdate + " (" + TEST_USER_ID + ") fue modificado exitosamente!");

		//	Act
		ResponseDto rtaATestear = userService.modificarXId(uDto, TOKEN_TEST);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(8)
	void modificarUsuarioErr() {
		//	Arrange
		UserDtoRequest uDto = new UserDtoRequest(TEST_USER_ID+"x", TEST_USER_NAMEUpdate, TEST_USER_EMAILUpdate, TEST_USER_PWDUpdate, TEST_USER_ROLUpdate, null,null,null,null);
		ResponseDto rtaOk = new ResponseDto("El Usuario " + TEST_USER_NAMEUpdate + " (" + TEST_USER_ID+"x" + ") NO pudo ser encontrado!");

		//	Act
		ResponseDto rtaATestear = userService.modificarXId(uDto, TOKEN_TEST);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(9)
	void borrarUsuarioOk() {
		//	Arrange
		UserIdDto uIdDto = new UserIdDto(TEST_USER_ID);
		ResponseDto rtaOk = new ResponseDto("El Usuario con ID: " + TEST_USER_ID + " fue borrado exitosamente!");

		//	Act
		ResponseDto rtaATestear = userService.borrarXId(uIdDto, TOKEN_TEST);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(10)
	void borrarUsuarioErr() {
		//	Arrange
		UserIdDto uIdDto = new UserIdDto(TEST_USER_ID);
		ResponseDto rtaOk = new ResponseDto("El Usuario con ID: " + TEST_USER_ID + " NO pudo ser encontrado!");

		//	Act
		ResponseDto rtaATestear = userService.borrarXId(uIdDto, TOKEN_TEST);

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
		String rtaOk = "El token " + TOKEN_TEST.getTokenValue() + " pertenece al ID de Usuario " + USER_ADMIN_ID + " y es Válido hasta ";

		//	Act
		ResponseDto respATestear = userService.validarSesion(TOKEN_TEST);
		String rtaATestear = respATestear.getRespuesta();
		rtaATestear = rtaATestear.substring(0, rtaOk.length());

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(13)
	void validarSesionErr() {
		//	Arrange
		TokenDto t = new TokenDto(TOKEN_TEST.getTokenValue().substring(0,9)+"x");
		ResponseDto rtaOk = new ResponseDto("El token " + t.getTokenValue() + " NO es Válido!");

		//	Act
		ResponseDto rtaATestear = userService.validarSesion(t);

		//	Assert
		assertEquals(rtaOk, rtaATestear);
	}

	@Test
	@Order(14)
	void listarOk() {
		//	Arrange
		List<UserDtoResponse> listaOk = new ArrayList<>();
		UserDtoResponse userDtoResponse = new UserDtoResponse(USER_ADMIN_ID, USER_ADMIN_NAME, USER_ADMIN_EMAIL,
				USER_ADMIN_ROL, USER_ADMIN_CREATED_DATE, null, null, USER_ADMIN_VALID_UNTIL);
		listaOk.add(userDtoResponse);

		//	Act
		List<UserDtoResponse> listaATestear = userService.listar(new UserNameDto(USER_ADMIN_NAME), TOKEN_TEST);

		//	Assert
		assertEquals(listaOk.size(), listaATestear.size());
		assertEquals(listaOk.get(0).getId(), listaATestear.get(0).getId());
		assertEquals(listaOk.get(0).getName(), listaATestear.get(0).getName());
		assertEquals(listaOk.get(0).getEmail(), listaATestear.get(0).getEmail());
		assertEquals(listaOk.get(0).getRol(), listaATestear.get(0).getRol());
		assertEquals(listaOk.get(0).getCreatedDate(), listaATestear.get(0).getCreatedDate());
	}

	@Test
	@Order(15)
	void listarErr() {
		//	Arrange

		//	Act: No debe encontrar ningún usuario
		List<UserDtoResponse> listaATestear = userService.listar(new UserNameDto(USER_ADMIN_NAME+"x"), TOKEN_TEST);

		//	Assert
		assertEquals(0, listaATestear.size());
	}

	@Test
	@Order(16)
	void listarXFechaOk() {
		//	Arrange
		List<UserDtoResponse> listaOk = new ArrayList<>();
		UserDtoResponse userDtoResponse = new UserDtoResponse(USER_ADMIN_ID, USER_ADMIN_NAME, USER_ADMIN_EMAIL,
				USER_ADMIN_ROL, USER_ADMIN_CREATED_DATE, null, null, USER_ADMIN_VALID_UNTIL);
		listaOk.add(userDtoResponse);

		//	Act
		String fecDesde = date2YYYYMMAA(USER_ADMIN_CREATED_DATE.minusDays(1));
		String fecHasta = date2YYYYMMAA(USER_ADMIN_CREATED_DATE.plusDays(1));
		List<UserDtoResponse> listaATestear = userService.listarCreadosEntreFechas(fecDesde, fecHasta, TOKEN_TEST);

		//	Assert
		assertEquals(listaOk.size(), listaATestear.size());
		assertEquals(listaOk.get(0).getId(), listaATestear.get(0).getId());
		assertEquals(listaOk.get(0).getName(), listaATestear.get(0).getName());
		assertEquals(listaOk.get(0).getEmail(), listaATestear.get(0).getEmail());
		assertEquals(listaOk.get(0).getRol(), listaATestear.get(0).getRol());
		assertEquals(listaOk.get(0).getCreatedDate(), listaATestear.get(0).getCreatedDate());
	}
}
