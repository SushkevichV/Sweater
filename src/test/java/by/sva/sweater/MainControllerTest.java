package by.sva.sweater;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import by.sva.sweater.controller.MessageController;

// видео с уроком https://www.youtube.com/watch?v=Lnc3o8cCwZY&list=PLU2ftbIeotGpAYRP9Iv2KLIwK36-o_qYk&index=26

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("user") // использовать для авторизации пользователя
public class MainControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private MessageController controller;
	
	// для выполнения этого теста все теги view-файлов должны быть закрыты, в т.ч. <meta>, <input> и <br>
	@Test
	public void mainPageTest() throws Exception {
		this.mockMvc.perform(get("/main"))	// выполнить GET-запрос на страницу /main
			.andDo(print())					// вывести полученный результат в консоль
			.andExpect(authenticated())		// ожидается, что у пользователя открыта сессия
			.andExpect(xpath("//*[@id='navbarSupportedContent']/div").string("user")); // ожидается элемент (расположенный где угодно и с любым именем) с атрибутом id и значением user
	}
	
	@Test
	public void messageListTest() throws Exception {
		this.mockMvc.perform(get("/main"))
			.andDo(print())
			.andExpect(authenticated()) // ожидается, что пользователь аутентифицирован
			.andExpect(xpath("//div[@id='message-list']/div").nodeCount(5)); // ожидается, что будет получено 5 элементов (всего 5 сообщений)
	}
	
	@Test
	public void filterMessageTest() throws Exception {
		this.mockMvc.perform(get("/main").param("filter", "Terminator"))
			.andDo(print())
			.andExpect(authenticated())
			.andExpect(xpath("//div[@id='message-list']/div").nodeCount(2)) // ожидается, что будет получено 2 элемента (всего 2 сообщения)
			.andExpect(xpath("//div[@id='message-list']/div[@data-id=10]").exists()) // в т.ч. элемент с id = 10
			.andExpect(xpath("//div[@id='message-list']/div[@data-id=13]").exists()); // и элемент с id = 13
	}
	
	// тест на добавление сообщения
	/* не проверял
	@Test
	public void addMessageToListTest() throws Exception {
		MockHttpServletRequestBuilder multipart = MockMvcRequestBuilders.multipart("/main")
				.file("file", "123".getBytes())
				.param("text", "fifth")
				.param("tag", "new one")
				.with(csrf()); // csrf токен
		
		this.mockMvc.perform(multipart)
			.andDo(print())
			.andExpect(authenticated())
			.andExpect(xpath("//div[@id='message-list']/div").nodeCount(6)); // после добавления сообщения ожидается, что количество сообщений будет 6
	}
	*/
}
