package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.employee;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.*;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CarData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderEmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CarService carService;

    @Autowired
    private EmployeeDetailService employeeDetailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @Autowired
    private CarMakeService carMakeService;

    @Autowired
    private CarModelService carModelService;

    @Value("${user.update.username.successful}")
    private String userUpdateUsernameSuccessful;

    @Value("${user.update.email.successful}")
    private String userUpdateEmailSuccessful;

    @Value("${user.add.car.exception}")
    private String userAddCarException;

    @Value("${user.delete.implementation.order.exception}")
    private String userDeleteImplementationOrderException;

    @Value("${user.update.success}")
    private String userUpdateSuccessful;

    @Value("${user.delete.account.success}")
    private String userDeleteAccountSuccessful;

    @Value("${order.delete.success}")
    private String orderDeleteSuccessful;

    @Value("${order.create.success}")
    private String orderCreateSuccessful;

    @Value("${car.delete.success}")
    private String carDeleteSuccessful;

    @Value("${car.add.success}")
    private String carAddSuccessful;

    @GetMapping("/dashboard")
    public String showEmployeeDashboard(Model model
            , @RequestParam(value = "message", required = false) String message
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("message", message);
        model.addAttribute("employeeDetail", employeeDetailService.findByEmployeeUsername(username));
        return "employee/dashboard";
    }

//    @GetMapping("/showData")
//    public String showData(Model model
//            , @CurrentSecurityContext(expression = "authentication.name") String username) {
//
//            model.addAttribute
//                    ("employeeData", employeeService.getEmployeeDataByUsername(username));
//        return "employee/show-data";
//    }

    @GetMapping("/showUpdateForm")
    public String showUpdateForm(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {
        EmployeeData employeeData = employeeService.getEmployeeDataByUsername(username);
        model.addAttribute("employeeData", employeeData);
        model.addAttribute("oldUsername", employeeData.getUsername());
        model.addAttribute("oldEmail", employeeData.getEmail());

        return "employee/update-form";
    }

    @PostMapping("/processUpdateForm")
    public ModelAndView processUpdateForm(Model model
            , @Valid @ModelAttribute EmployeeData employeeData
            , BindingResult bindingResult
            , @ModelAttribute("oldUsername") String oldUsername
            , @ModelAttribute("oldEmail") String oldEmail) {

        RedirectView redirectView = new RedirectView();

        if (bindingResult.hasErrors()) {
            return new ModelAndView("employee/update-form");
        }
        try {
            employeeService.update(employeeData, oldUsername, oldEmail);
        } catch (UserAlreadyExistException | EmailAlreadyExistException e) {
            model.addAttribute("updateError", e.getMessage());
            return new ModelAndView("employee/update-form").addObject(model);
        }

        if (employeeData.getUsername().equals(oldUsername) && employeeData.getEmail().equals(oldEmail)) {
            redirectView.getAttributesMap().put("message", userUpdateSuccessful);
            redirectView.setUrl("showOption");
            return new ModelAndView(redirectView);
        }
        if(!(employeeData.getUsername().equals(oldUsername) || employeeData.getEmail().equals(oldEmail))) {
            redirectView.getAttributesMap().put("message", userUpdateUsernameSuccessful + userUpdateEmailSuccessful);
        }
        else {
            if (!employeeData.getUsername().equals(oldUsername)) {
                redirectView.getAttributesMap().put("message", userUpdateUsernameSuccessful);
            }
            if (!employeeData.getEmail().equals(oldEmail)) {
                redirectView.getAttributesMap().put("message", userUpdateEmailSuccessful);
            }
        }
        redirectView.setUrl("/login");
        return new ModelAndView(redirectView);
    }
//
//    @GetMapping("/showChangePasswordForm")
//    public String showChangePasswordForm(Model model
//            , @CurrentSecurityContext(expression = "authentication.name") String username) {
//
//        EmployeeData employeeData = employeeService.getEmployeeData(employeeService.findByUsername(username));
//        WrapperString wrapperString = new WrapperString();
//        model.addAttribute("crmEmployee", employeeData);
//        model.addAttribute("wrapperString", wrapperString);
//        return "employee/change-password-form";
//    }

//    @PostMapping("/processChangePasswordForm")
//    public String processChangePasswordForm(
//            @Valid @ModelAttribute("crmEmployee") EmployeeData theEmployeeData,
//            BindingResult theBindingResult,
//            @ModelAttribute("wrapperString") WrapperString wrapperString,
//            @CurrentSecurityContext(expression = "authentication.name") String username,
//            Model theModel) {
//
//        String password = employeeService.findByUserName(username).getPassword();
//        if(employeeService.comparePassword(wrapperString.getOldUsername(), password)){
//            // form validation
//            if (theBindingResult.hasErrors()){
//                theModel.addAttribute("crmEmployee", theEmployeeData);
//                return "employee/change-password-form";
//            }
//            theEmployeeData.setPassword(employeeService.encodePassword(theEmployeeData.getPassword()));
//            employeeService.update(theEmployeeData, username);
//            return "redirect:/";
//        }
//        else {
//            theModel.addAttribute("crmEmployee", theEmployeeData);
//            theModel.addAttribute("registrationError", "Old password error");
//            return "employee/change-password-form";
//        }
//
//    }

    @GetMapping("/deleteAccount")
    public ModelAndView deleteAccount(@CurrentSecurityContext(expression = "authentication.name") String username) {

        RedirectView redirectView = new RedirectView();

        employeeService.deleteByUsername(username);

        redirectView.getAttributesMap().put("message", userDeleteAccountSuccessful);
        redirectView.setUrl("/");
        return new ModelAndView(redirectView);
    }

    @PostMapping("/deleteOrder")
    public ModelAndView deleteOrder(@RequestParam("orderId") Integer orderId
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        RedirectView redirectView = new RedirectView();
        orderService.deleteOrderFromEmployeeByOrderAndEmployeeUsername(orderService.findOrderById(orderId), username);

        redirectView.getAttributesMap().put("message", orderDeleteSuccessful);
        redirectView.setUrl("showOrderList");
        return new ModelAndView(redirectView);
    }

    @PostMapping("/deleteCar")
    public ModelAndView deleteCar(@RequestParam("carId") Integer carId
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        RedirectView redirectView = new RedirectView();
        employeeService.deleteEmployeeCarByCarIdAndUsername(carId, username);
        redirectView.getAttributesMap().put("message", carDeleteSuccessful);
        redirectView.setUrl("showCarList");
        return new ModelAndView(redirectView);
    }

    @GetMapping("/showAddCarForm")
    public String showCarForm(Model model
            , @RequestParam(value = "make", required = false) String make) {

        CarData carData = new CarData();
        if(make != null) {
            carData.setMake(make);
            model.addAttribute("carModelList", carModelService.loadCarModelList(carMakeService.findByMake(make)));
        }

        model.addAttribute("carData", carData);
        model.addAttribute("carMakeList", carMakeService.loadCarMakeList());
        return "employee/add-car-form";
    }

     @PostMapping("/processCarMakeChoose")
     public ModelAndView processCarMakeChoose(
             @RequestParam("make") String make) {

         RedirectView redirectView = new RedirectView();
         redirectView.getAttributesMap().put("make", make);
         redirectView.setUrl("showAddCarForm");
         return new ModelAndView(redirectView);
     }

    @PostMapping("/processAddCar")
    public ModelAndView processAddCar(
              @ModelAttribute("carData") @Valid CarData carData
            , BindingResult bindingResult
            , @ModelAttribute("carModelList") ArrayList<String> carModelList
            , @ModelAttribute("carMakeList") ArrayList<String> carMakeList
            , Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        RedirectView redirectView = new RedirectView();

        if (bindingResult.hasErrors()) {
            carModelList.addAll(carModelService.loadCarModelList(carMakeService.findByMake(carData.getMake())));
            carMakeList.addAll(carMakeService.loadCarMakeList());
            return new ModelAndView("employee/add-car-form");
        }


        carData.setUsername(username);
        try {
            carService.save(carData);
        } catch (MyCarAlreadyExistException e) {
            carModelList.addAll(carModelService.loadCarModelList(carMakeService.findByMake(carData.getMake())));
            carMakeList.addAll(carMakeService.loadCarMakeList());
            model.addAttribute("addCarError", userAddCarException);
            return new ModelAndView("employee/add-car-form").addObject(model);
        }
        redirectView.getAttributesMap().put("message", carAddSuccessful);
        redirectView.setUrl("showCarList");
        return new ModelAndView(redirectView);
    }

    @GetMapping("/showCarList")
    public String showEmployeeCarList(Model model
            , @RequestParam(value = "message",required = false) String message
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("carDataList", carService.getCarDataListForEmployeeUsername(username));
        model.addAttribute("message", message);
        return "employee/car-list";
    }

    //Create ordering
    @GetMapping("/showAddCarToOrderForm")
    public String showAddCarToOrderForm(Model model
            , @RequestParam(value = "make", required = false) String make
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        CarData carData = new CarData();
        if(make != null) {
            carData.setMake(make);
            model.addAttribute("carModelList", carModelService.loadCarModelList(carMakeService.findByMake(make)));
        }

        model.addAttribute("carDataList", carService.getCarDataListForEmployeeUsername(username));
        model.addAttribute("carData", carData);
        model.addAttribute("carMakeList", carMakeService.loadCarMakeList());
        return "employee/add-car-to-order-form";
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
            , @ModelAttribute("carModelList") ArrayList<String> carModelList
            , @ModelAttribute("carMakeList") ArrayList<String> carMakeList
            , RedirectAttributes redirectAttributes) {

        RedirectView redirectView = new RedirectView();

        if (bindingResult.hasErrors()) {
            carModelList.addAll(carModelService.loadCarModelList(carMakeService.findByMake(carData.getMake())));
            carMakeList.addAll(carMakeService.loadCarMakeList());
            return new ModelAndView("employee/add-car-to-order-form");
        }

        redirectAttributes.addFlashAttribute("carData", carData);

        redirectView.setUrl("showCreateOrderForm");
        return new ModelAndView(redirectView);
    }

    @GetMapping("/showCreateOrderForm")
    public String showCreateOrderForm(Model model) {
        OrderEmployeeData orderEmployeeData = new OrderEmployeeData();
        orderEmployeeData.setCarData((CarData) model.getAttribute("carData"));

        model.addAttribute("orderEmployeeData", orderEmployeeData);
        model.addAttribute("cities", cityService.loadCites());
        model.addAttribute("localDateTime", LocalDateTime.now());

        return "employee/create-order-form";
    }

    @PostMapping("/processCreateOrder")
    public ModelAndView processCreateOrder(@Valid OrderEmployeeData orderEmployeeData
            , BindingResult bindingResult
            , @ModelAttribute("cities") ArrayList<String> cities
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        RedirectView redirectView = new RedirectView();

        if (bindingResult.hasErrors()) {
            cities.addAll(cityService.loadCites());
            return new ModelAndView("employee/create-order-form").addObject("localDateTime", LocalDateTime.now());
        }
        try {
            orderService.createOrder(username, orderEmployeeData);
        } catch (NullWorkshopInCityException e) {
            redirectView.getAttributesMap().put("message", e.getMessage());
            redirectView.setUrl("dashboard");
            return new ModelAndView(redirectView);
        }
        redirectView.getAttributesMap().put("message", orderCreateSuccessful);
        redirectView.setUrl("showOrderList");
        return new ModelAndView(redirectView);
    }

    @GetMapping("/showOrderList")
    public String showOrderList(Model model
            , @RequestParam(value = "message",required = false) String message
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<OrderEmployeeData> orderEmployeeDataList;

        orderEmployeeDataList = orderService.getOrderEmployeeDataListByUsernameAndStanIsNotCompleted(username);


        model.addAttribute("orderEmployeeData", new OrderEmployeeData());
        model.addAttribute
                ("orderEmployeeDataList", orderEmployeeDataList);
        model.addAttribute("message", message);

        return "employee/order-list";
    }

    @PostMapping("/processOrderChoose")
    public ModelAndView processOrderChoose(Model model
            ,@RequestParam("orderId") Integer orderId) {

        OrderEmployeeData orderEmployeeData = null;
        try {
            orderEmployeeData = orderService.getOrderEmployeeDataByOrderAndStanEqualsWorkshopAnswer
                            (orderService.findOrderById(orderId));
        } catch (NullOrderAnswerForOrderException e) {
            model.addAttribute("message",e.getMessage());
            return new ModelAndView("employee/workshop-answer-list-for-order").addObject(model);
        }
        model.addAttribute("orderEmployeeData", orderEmployeeData);
        return new ModelAndView("employee/workshop-answer-list-for-order");
    }

    @PostMapping("/processOrderForImplementationChoose")
    public ModelAndView processOrderForImplementationChoose(@RequestParam("orderAnswerId") Integer orderAnswerId) {

        orderAnswerService.chooseOrderAnswerForImplementation(orderAnswerService.findById(orderAnswerId));

        return new ModelAndView(new RedirectView("showOrderList"));
    }

    @GetMapping("/showCompletedOrderList")
    public String showCompletedOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<OrderEmployeeData> orderEmployeeDataList;
        orderEmployeeDataList = orderService.getOrderEmployeeDataListByUsernameAndStanEqualsCompleted(username);

        model.addAttribute
                ("orderEmployeeDataList", orderEmployeeDataList);
        model.addAttribute("orderEmployeeData", new OrderEmployeeData());
        return "employee/completed-order-list";
    }

    @GetMapping("/showOption")
    public String showShowOption(Model model
            , @RequestParam(value = "message",required = false) String message
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("message", message);
        model.addAttribute
                ("employeeData", employeeService.getEmployeeDataByUsername(username));
        return "employee/option";
    }

}
