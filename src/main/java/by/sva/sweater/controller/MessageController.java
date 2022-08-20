package by.sva.sweater.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import by.sva.sweater.entity.Message;
import by.sva.sweater.entity.User;
import by.sva.sweater.entity.dto.MessageDto;
import by.sva.sweater.repository.MessageRepository;
import by.sva.sweater.service.MessageService;
import freemarker.template.utility.StringUtil;

@Controller
public class MessageController {
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private MessageService messageService;
	
	@Value("${upload.path}") // получает значение переменной upload.path из application.properties
	private String uploadPath;
	
	@GetMapping
	public String greeting(Map<String, Object> model) {
		return "greeting";
	}
	
	@GetMapping("/main")
	public String main(@RequestParam(required = false, defaultValue = "") String filter, 
			Model model,
			@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, // сортировать по id по убыванию
			@AuthenticationPrincipal User user) {
		/* В файле registration.ftlh и в файле main.ftlh переменные ${message!""} и ${filter!}
		 * обязательно писать с восклицательным знаком (после него можно указать значение по умолчанию).
		 * Иначе при пустом значении сваливается
		 * или добавить ?ifExists -> ${message?ifExists} и ${filter?ifExists}
		 */
		
		/* получение всех сообщений
		List<Message> messages = messageRepository.findAll();
		if(filter != null && !filter.isEmpty()) {
			messages = messageRepository.findByTag(filter);
		} else {
			messages = messageRepository.findAll();
		}
		model.addAttribute("messages", messages);
		model.addAttribute("filter", filter);
		*/
		// постраничное получение сообщений
		Page<MessageDto> page = messageService.getMessageList(pageable, filter, user);
		
		model.addAttribute("page", page);
		model.addAttribute("url", "/main");
		model.addAttribute("filter", filter);
		return "main";
	}
	
	@PostMapping("/main")
	public String add(@AuthenticationPrincipal User user,
			//@RequestParam String text, 
			//@RequestParam String tag, 
			@Valid Message message,
			BindingResult bindingResult, // всегда должен идти перед Model, иначе все ошибки валидации будут во view без обработки
			//Map<String, Object> model,
			Model model,
			@RequestParam("file") MultipartFile file
		) throws IOException {

		//Message message = new Message(text, tag, user);
		message.setAuthor(user);
		
		if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtil.getErrors(bindingResult);

            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            saveFile(message, file);

            // при успешной валидации нужно очистить все поля сообщения 
            model.addAttribute("message", null);

            messageRepository.save(message);
        }
		
		List<Message> messages = messageRepository.findAll();
		//model.put("messages", messages);
		model.addAttribute("messages", messages);
		return "main";
	}
	
	private void saveFile(Message message, MultipartFile file) throws IOException {
		if(!file.isEmpty() && !file.getOriginalFilename().isEmpty()) { // если файл не выбран
			File uploadDir = new File(uploadPath); // переменная - путь для хранения файлов
			if(!uploadDir.exists()) { // если такой папки нет
				uploadDir.mkdir();    // создать ее !НЕ СОЗДАЕТ! Не работает, пока не создашь папку вручную
			}
			
			// следующий блок нужен для контроля уникальности имен файлов
			String uuidFile = UUID.randomUUID().toString(); // сгенерировать уникальный идентификатор
			String resultFilename = uuidFile + "." + file.getOriginalFilename(); // добавить уникальный идентификатор к имени файла
			
			file.transferTo(new File(uploadPath + "/" + resultFilename));
			
			message.setFilename(resultFilename);
		}
		
	}

	@GetMapping("/user-messages/{author}")
	public String getUserMessages(
			@AuthenticationPrincipal User currentUser,
			@PathVariable User author,
			Model model,
			@RequestParam(required = false) Message message,
			@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
			) {
		//Set<Message> messages = user.getMessages();
		Page<MessageDto> page = messageService.getUsersMessages(pageable, currentUser, author);
		model.addAttribute("userChannel", author);
		model.addAttribute("subscriptionsCount", author.getSubscriptions().size());
		model.addAttribute("subscribersCount", author.getSubscribers().size());
		model.addAttribute("isSubscriber", author.getSubscribers().contains(currentUser));
		//model.addAttribute("messages", messages);
		model.addAttribute("page", page);
		model.addAttribute("message", message);
		model.addAttribute("isCurrentUser", currentUser.equals(author));
		model.addAttribute("url", "/user-messages/" + author.getId());
		return "userMessages";
	}
	
	@PostMapping("/user-messages/{user}")
	public String updateMessage(
			@AuthenticationPrincipal User currentUser,
			@PathVariable User user,
			@RequestParam("id") Message message,
			@RequestParam("text") String text,
			@RequestParam("tag") String tag,
			@RequestParam("file") MultipartFile file
			) throws IOException {
		if (message.getAuthor().equals(currentUser)) {
			if(!StringUtils.isEmpty(text)) {
				message.setText(text);
			}
			if(!StringUtils.isEmpty(tag)) {
				message.setTag(tag);
			}
			
			saveFile(message, file);
			
			messageRepository.save(message);
		}
		return "redirect:/user-messages/" + user.getId();
	}
	
	@GetMapping("/messages/{message}/like")
	public String like(
			@AuthenticationPrincipal User currentUser,
			@PathVariable Message message,
			RedirectAttributes redirectAttributes, // передает параметры в метод, в который будет редирект
			@RequestHeader(required = false) String referer) { // отсюда будем получать стрничку, с которой	был переход в текущий метод
		Set<User> likes = message.getLikes();
		if(likes.contains(currentUser)) {
			likes.remove(currentUser);
		} else {
			likes.add(currentUser);
		}
		
		messageRepository.save(message);
		
		UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
		components.getQueryParams().entrySet()
				.forEach(pair -> redirectAttributes.addAttribute(pair.getKey(), pair.getValue()));
		return "redirect:" + components.getPath();
	}
	
}
