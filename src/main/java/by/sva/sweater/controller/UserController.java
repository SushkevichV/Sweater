package by.sva.sweater.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import by.sva.sweater.entity.Role;
import by.sva.sweater.entity.User;
import by.sva.sweater.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping
	public String userList(Model model) {
		model.addAttribute("users", userService.findAll());
		return "userList";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("{user}")
	/* получаем id пользователя
	public String userEditForm(@PathVariable Long user) {
	но можно сделать лучше - получить сразу пользователя
	*/
	public String userEditForm(@PathVariable User user, Model model) {
		model.addAttribute("user", user);
		model.addAttribute("roles", Role.values());
		return "userEdit";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	// получение пользователя по id из параметра userId
	public String userSave(@RequestParam String username,
							@RequestParam Map<String, String> form, // список полей формы и их значения
							@RequestParam("userId") User user) {
		
		userService.saveUser(user, username, form);
		
		return "redirect:/user";
	}
	
	@GetMapping("profile")
	public String getProfile(Model model, @AuthenticationPrincipal User user) {
		model.addAttribute("username", user.getUsername());
		model.addAttribute("email", user.getEmail());
		
		return "profile";
	}
	
	@PostMapping("profile")
	public String updateProfile(
					@AuthenticationPrincipal User user,
					@RequestParam String password,
					@RequestParam String email)
		{
		userService.updateProfile(user, password, email);
		
		return "redirect:/user/profile";
	}
	
	@GetMapping("subscribe/{user}")
	public String subscribe(
			@AuthenticationPrincipal User currentUser,
			@PathVariable User user
			) {
		userService.subscribe(currentUser, user);
		return "redirect:/user-messages/" + user.getId();
	}
	
	@GetMapping("unsubscribe/{user}")
	public String unsubscribe(
			@AuthenticationPrincipal User currentUser,
			@PathVariable User user
			) {
		userService.unsubscribe(currentUser, user);
		return "redirect:/user-messages/" + user.getId();
	}
	
	@GetMapping("{type}/{user}/list")
	public String userList(
			Model model,
			@PathVariable User user,
			@PathVariable String type
			) {
		model.addAttribute("userChannel", user);
		model.addAttribute("type", type);
		
		if(type.equals("subscriptions")) {
			model.addAttribute("users", user.getSubscriptions());
		} else {
			model.addAttribute("users", user.getSubscribers());
		}
		
		return "subscriptions";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/remove/{user}")
	public String removeUser(@PathVariable User user) {
		userService.removeUser(user.getId());
		return "redirect:/user";
	}

}
