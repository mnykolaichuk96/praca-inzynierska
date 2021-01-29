package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.employee;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
@RequestMapping("/register/employee")
public class EmployeeRegistrationController {

    @Autowired
    private EmployeeService employeeService;

    @Value("${user.registration.successful}")
    private String userRegistrationSuccessful;

	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model model) {

		model.addAttribute("employeeData", new EmployeeData());
		return "employee/registration-form";
	}

	@PostMapping("/processRegistration")
	public ModelAndView processRegistration(
				@Valid EmployeeData employeeData,
				BindingResult bindingResult,
				Model model) {

		RedirectView redirectView = new RedirectView();
		if (bindingResult.hasErrors()){
		 	return new ModelAndView("employee/registration-form");
		}
        try {
		    employeeService.register(employeeData);
	    }
        catch (UserAlreadyExistException | EmailAlreadyExistException e){
		    model.addAttribute("registrationError", e.getMessage());
		    return new ModelAndView("employee/registration-form").addObject(model);
       }
		redirectView.getAttributesMap().put("message", userRegistrationSuccessful);
        redirectView.setUrl("/login");
       return new ModelAndView(redirectView);
	}
}
