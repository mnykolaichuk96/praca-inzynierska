package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.NullWorkshopInCityException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CarData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeDetailData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderWorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CarMakeService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CarModelService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CityService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class DemoController {

	@Autowired
	private CityService cityService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CarModelService carModelService;

	@Autowired
	private CarMakeService carMakeService;

	@GetMapping("/")
	public String showHome(Model model
			, @RequestParam(name = "message", required = false) String message
			, @RequestParam(name = "errorMessage", required = false) String errorMessage) {

		model.addAttribute("cityList", cityService.loadCites());
		if(message != null) {
			model.addAttribute("message", message);

		}
		if(errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
		}
		return "home";
	}

	@GetMapping("/showAddCarToOrderForm")
	public String showAddCarToOrderForm(Model model
			, @RequestParam(value = "make", required = false) String make) {

		CarData carData = new CarData();
		if(make != null) {
			carData.setMake(make);
			model.addAttribute("carModelList", carModelService.loadCarModelList(carMakeService.findByMake(make)));
		}

		model.addAttribute("carData", carData);
		model.addAttribute("carMakeList", carMakeService.loadCarMakeList());
		return "unregistered/add-car-to-order-form";
	}

	@PostMapping("/processCarMakeChooseToOrder")
	public ModelAndView processCarMakeChooseToOrder(
			@RequestParam("make") String make) {

		RedirectView redirectView = new RedirectView();
		redirectView.getAttributesMap().put("make", make);
		redirectView.setUrl("showAddCarToOrderForm");
		return new ModelAndView(redirectView);
	}

	@PostMapping("/processAddCarToOrder")
	public ModelAndView processAddCarToOrder(
			@ModelAttribute("carData") @Valid CarData carData
			, BindingResult bindingResult
			, @ModelAttribute("carMakeList") ArrayList<String> carMakeList
			, @ModelAttribute("carModelList") ArrayList<String> carModelList
			, RedirectAttributes redirectAttributes) {

		RedirectView redirectView = new RedirectView();

		if (bindingResult.hasErrors()) {
			carMakeList.addAll(carMakeService.loadCarMakeList());
			carModelList.addAll(carModelService.loadCarModelList(carMakeService.findByMake(carData.getMake())));
			return new ModelAndView("unregistered/add-car-to-order-form");
		}

		redirectAttributes.addFlashAttribute("carData", carData);

		redirectView.setUrl("showCreateOrderForm");
		return new ModelAndView(redirectView);
	}

	@GetMapping("/showCreateOrderForm")
	public String showCreateOrderForm(Model model) {
		OrderWorkshopData orderWorkshopData = new OrderWorkshopData();
		orderWorkshopData.setCarData((CarData) model.getAttribute("carData"));

		model.addAttribute("orderWorkshopData", orderWorkshopData);
		model.addAttribute("employeeDetailData", new EmployeeDetailData());
		model.addAttribute("cities", cityService.loadCites());
		model.addAttribute("localDateTime", LocalDateTime.now());

		return "unregistered/create-order-form";
	}

	@PostMapping("/processCreateOrder")
	public ModelAndView processCreateOrder(@Valid OrderWorkshopData orderWorkshopData
			, BindingResult bindingResultOrder
			, @Valid EmployeeDetailData employeeDetailData
			, BindingResult bindingResultEmployee
			, @ModelAttribute("cities") ArrayList<String> cities) {

		RedirectView redirectView = new RedirectView();

		if (bindingResultOrder.hasErrors() || bindingResultEmployee.hasErrors()) {
			cities.addAll(cityService.loadCites());
			return new ModelAndView("unregistered/create-order-form").addObject("localDateTime", LocalDateTime.now());
		}
		try {
			orderWorkshopData.setEmployeeDetailData(employeeDetailData);
			orderService.createOrderFromUnregisteredUser(orderWorkshopData);
		} catch (EmailAlreadyExistException | NullWorkshopInCityException e) {
			redirectView.setUrl("/");
			redirectView.getAttributesMap().put("errorMessage", e.getMessage());
			return new ModelAndView(redirectView);
		}

		redirectView.getAttributesMap().put("message", "order successfully created pls verify your email adres");
		redirectView.setUrl("/");
		return new ModelAndView(redirectView);
	}
}










