package by.sva.sweater;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import by.sva.sweater.controller.MessageController;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private MessageController controller;
	
	@Test
	public void test() throws Exception {
		assertThat(controller).isNotNull(); // проверяет, что контроллер подключен
	}
	
	@Test
	public void test2() throws Exception {
		this.mockMvc.perform(get("/")) 	// выполнить GET-запрос на главную страницу сервера
			.andDo(print())				// вывести полученный результат в консоль
			.andExpect(status().isOk())	// ожидается ответ сервера: статус 200
			.andExpect(content().string(containsString("Hello, guest")));	// ожидается контент, содержащий строку
	}
	
	@Test
	public void accessDeniedTest() throws Exception {
		this.mockMvc.perform(get("/main"))	// выполнить GET-запрос на страницу /main
		.andDo(print())						// вывести полученный результат в консоль
		.andExpect(status().is3xxRedirection())	// ожидается ответ сервера: статус 3xx (переадресация)
		.andExpect(redirectedUrl("http://localhost/login"));	// ожидается переход на страницу
	}
	
	@Test
	public void correctLoginTest() throws Exception {
		this.mockMvc.perform(formLogin().user("user").password("user"))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}
	
	@Test
	public void badCredentialsTest() throws Exception {
		this.mockMvc.perform(post("/login").param("user", "u")) // выполнить POST-запрос на страницу /login с неверным паролем
			.andDo(print())
			.andExpect(status().isForbidden()); // ожидается ответ сервера: статус 403 (доступ запрещен)
	}

}
