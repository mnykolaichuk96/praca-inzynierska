package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.workshop;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.WorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CityService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("/register/workshop")
public class WorkshopRegistrationController {

    @Autowired
    private WorkshopService workshopService;

    @Autowired
	private CityService cityService;

	@Value("${user.registration.successful}")
	private String userRegistrationSuccessful;

	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model model) {

		model.addAttribute("workshopData", new WorkshopData());
		model.addAttribute("cities", cityService.loadCites());
		return "workshop/registration-form";
	}

	@PostMapping("/processRegistration")
	public ModelAndView processRegistration(Model model
				, @Valid WorkshopData workshopData
				, BindingResult bindingResult
				, @ModelAttribute("cities") ArrayList<String> cityList) {

		RedirectView redirectView = new RedirectView();

		if (bindingResult.hasErrors()){
			cityList.addAll(cityService.loadCites());
			 return new ModelAndView("workshop/registration-form");
		}
		try {
			workshopService.register(workshopData);
		}
		catch (UserAlreadyExistException | EmailAlreadyExistException e){
			model.addAttribute("registrationError", e.getMessage());
			cityList.addAll(cityService.loadCites());
			return new ModelAndView("workshop/registration-form").addObject(model);
		}
		redirectView.getAttributesMap().put("message", userRegistrationSuccessful);
		redirectView.setUrl("/login");
		return new ModelAndView(redirectView);
	}
}
