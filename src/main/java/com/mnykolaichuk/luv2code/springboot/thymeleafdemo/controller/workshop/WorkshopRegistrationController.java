package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.workshop;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.WorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CityService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/register/workshop")
public class WorkshopRegistrationController {

    @Autowired
    private WorkshopService workshopService;

    @Autowired
	private CityService cityService;

	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model model) {

		model.addAttribute("workshopData", new WorkshopData());
		model.addAttribute("cities", cityService.loadCites());
		return "workshop/registration-form";
	}

	@PostMapping("/processRegistration")
	public String processRegistration(
				@Valid @ModelAttribute("workshopData") WorkshopData workshopData,
				BindingResult bindingResult,
				Model model) {

		if (bindingResult.hasErrors()){
			 model.addAttribute("cities", cityService.loadCites());
			 return "workshop/registration-form";
	        }
		try {
			workshopService.register(workshopData);
		}
		catch (UserAlreadyExistException | EmailAlreadyExistException e){
			model.addAttribute("cities", cityService.loadCites());
			model.addAttribute("registrationError", e.getMessage());
			return "workshop/registration-form";
		}
		model.addAttribute("username", workshopData.getUsername());
		model.addAttribute("email", workshopData.getEmail());
		return "registration-successful";
	}
}
