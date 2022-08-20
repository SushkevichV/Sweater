package by.sva.sweater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* Аналог twitter
 * 
 * ссылка на урок https://www.youtube.com/watch?v=jH17YkBTpI4&list=PLU2ftbIeotGpAYRP9Iv2KLIwK36-o_qYk&index=1
 * !!! ОБЯЗАТЕЛЬНО ЧИТАТЬ КОММЕНТАРИИ ПОД ВИДЕО !!!
 * в качестве шаблонизатора вместо таймлиф используется mustache, затем его сменили на freemarker
 * 
 * Поумолчанию пользователь - user, пароль генерируется в консоль при запуске
 */

@SpringBootApplication
public class SweaterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SweaterApplication.class, args);
	}

}
