package by.sva.sweater.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import by.sva.sweater.entity.Role;
import by.sva.sweater.entity.User;
import by.sva.sweater.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
	// @Autowired
	private final UserRepository userRepository;
	// или использовать @Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	// для работы с почтой отключить антивирус
	@Autowired
	private MailSenderService mailSender;

	@Override
	//public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	public UserDetails loadUserByUsername(String username) throws BadCredentialsException {
		
		if(username.isEmpty()) {
			throw new BadCredentialsException("Введите имя пользователя");
		}
		
		User user = userRepository.findByUsername(username);
		
		if(user == null) {
			throw new BadCredentialsException("Пользователь не найден");
		}
		
		return user;
	}
	
	public boolean addUser(User user) {
		User userFromDb = userRepository.findByUsername(user.getUsername());
		
		if(userFromDb != null) {
			return false;
		}
		
		user.setActive(false);
		user.setRoles(Collections.singleton(Role.USER));
		user.setActivationCode(UUID.randomUUID().toString());
		
		userRepository.save(user);
		
		sendMessage(user);
		
		return true;
	}

	private void sendMessage(User user) {
		if (!StringUtils.isEmpty(user.getEmail())) {
			String message = String.format("Hello, %s! \n" + "Wellcome to Sweater. Please, visit http://localhost:8080/activate/%s\"", 
					user.getUsername(),
					user.getActivationCode());
			mailSender.send(user.getEmail(), "Activation code", message);
		}
	}

	public boolean activateUser(String code) {
		User user = userRepository.findByActivationCode(code);
		if(user == null) {
			return false;
		}
		
		user.setActivationCode(null);
		user.setActive(true);
		userRepository.save(user);
		
		return true;
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public void saveUser(User user, String username, Map<String, String> form) {
		user.setUsername(username);
		
		Set<String> roles = Arrays.stream(Role.values()) // преобразовать enum в стрим
				.map(Role::name) // получить имена ролей
				.collect(Collectors.toSet()); // сложить полученный список в set
		
		user.getRoles().clear(); // очистить роли пользователя
		
		for (String key : form.keySet()) { //обходим список полей, пришедших от пользователя
			if(roles.contains(key)) { //проверяем, если имя какого-то поля является именем роли, то
				user.getRoles().add(Role.valueOf(key)); // ищем такую роль в enum Role и устанавливаем эти роли пользователю
			}
		}
		
		userRepository.save(user);		
	}

	public void updateProfile(User user, String password, String email) {
		String userEmail = user.getEmail();
		boolean isEmailChanged = (email != null && !email.equals(userEmail));
			
		if(isEmailChanged) {
			user.setEmail(email);
			if(!StringUtils.isEmpty(email)) { // если пользователь установил новил новый email
				user.setActivationCode(UUID.randomUUID().toString()); // сгенерировать новый код активации
				sendMessage(user); // отправить новый код активации на email
			}
		}
		
		if(!StringUtils.isEmpty(password)) { // если пользователь сменил пароль
			user.setPassword(password);
		}
		
		userRepository.save(user);
	}

	public void subscribe(User currentUser, User user) {
		user.getSubscribers().add(currentUser);
		userRepository.save(user);
	}

	public void unsubscribe(User currentUser, User user) {
		user.getSubscribers().remove(currentUser);
		userRepository.save(user);
	}

	public void removeUser(Long id) {
		userRepository.deleteById(id);
	}

}
