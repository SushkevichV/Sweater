package by.sva.sweater.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import by.sva.sweater.entity.Message;
import by.sva.sweater.entity.User;
import by.sva.sweater.service.UserService;

@Controller
public class RegistrationController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/registration")
	public String registration() {
		return "registration";
	}
	
	@PostMapping("/registration")
	public String addUser(@Valid User user, BindingResult bindingResult,  Model model, @RequestParam(required = false) String password2) { // BindingResult (ошибки валидации) всегда должен идти перед Model
		
		if(!user.getPassword().equals(password2)) {
			//model.addAttribute("password2Error", "Пароли не сопадают");
			//return "registration";
			bindingResult.addError(new FieldError("user", "password2", "Пароли не сопадают")); // добавить ошибку в bindingResult. К password2 автоматом добавиться Error. В форме это будет поле password2Error
		}
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errors = ControllerUtil.getErrors(bindingResult);
			
			model.mergeAttributes(errors);
			
			return "registration"; // если поля заполнены некорректно, возврат на страницу регистрации
		}
		
		if(!userService.addUser(user)) {
			//model.addAttribute("message", "User already exists");
			model.addAttribute("usernameError", "Пользователь с таким именем уже зарегистрирован");
			return "registration";
		}
		
		return "redirect:/login";
	}
	
	@GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found!");
        }

        return "login";
    }

}
