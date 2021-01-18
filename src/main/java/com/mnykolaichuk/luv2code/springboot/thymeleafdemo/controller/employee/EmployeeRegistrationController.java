package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.employee;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeService;
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
@RequestMapping("/register/employee")
public class EmployeeRegistrationController {

    @Autowired
    private EmployeeService employeeService;

	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model model) {

		model.addAttribute("employeeData", new EmployeeData());
		return "employee/registration-form";
	}

	@PostMapping("/processRegistration")
	public String processRegistration(
				@Valid @ModelAttribute("employeeData") EmployeeData employeeData,
				BindingResult bindingResult,
				Model model) {

		if (bindingResult.hasErrors()){
		 	return "employee/registration-form";
		}

        try {
		    employeeService.register(employeeData);
	    }
        catch (UserAlreadyExistException | EmailAlreadyExistException e){
		    model.addAttribute("registrationError", e.getMessage());
		    return "employee/registration-form";
       }
       model.addAttribute("username", employeeData.getUsername());
       model.addAttribute("email", employeeData.getEmail());
       return "registration-successful";
	}
}
