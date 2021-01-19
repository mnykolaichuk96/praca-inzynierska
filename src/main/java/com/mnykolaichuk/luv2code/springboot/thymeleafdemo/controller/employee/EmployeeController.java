package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.employee;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.MyCarAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.WrapperString;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Car;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CarData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderEmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;
    @Autowired
    CityService cityService;
    @Autowired
    CarService carService;

    @Autowired
    private EmployeeDetailService employeeDetailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAnswerService orderAnswerService;


    @GetMapping("/dashboard")
    public String showEmployeeDashboard(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {
        model.addAttribute("loggedEmployeeDetail", employeeDetailService.findByUsername(username));
        return "employee/dashboard";
    }

    @GetMapping("/showData")
    public String showData(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute
                ("employeeData", employeeService.getEmployeeDataByUsername(username));
        return "employee/show-data";
    }

    @GetMapping("/showUpdateForm")
    public String showUpdateForm(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        EmployeeData employeeData = employeeService.getEmployeeDataByUsername(username);
        WrapperString wrapperString = new WrapperString();
        wrapperString.setOldUsername(employeeData.getUsername());
        wrapperString.setOldEmail(employeeData.getEmail());
        model.addAttribute("employeeData", employeeData);
        model.addAttribute("wrapperString", wrapperString);
        return "employee/update-form";
    }

    @PostMapping("/processUpdateForm")
    public String processUpdateForm(
            @Valid @ModelAttribute("employeeData") EmployeeData employeeData,
            BindingResult bindingResult,
            @ModelAttribute("wrapperString") WrapperString wrapperString,
            Model model) {
        // form validation
        if (bindingResult.hasErrors()) {
            System.out.println(employeeData);
            model.addAttribute("employeeData", employeeData);
            model.addAttribute("wrapperString", wrapperString);
            return "employee/update-form";
        }

        try {
            employeeService.update(employeeData, wrapperString);
        } catch (UserAlreadyExistException | EmailAlreadyExistException e) {
            model.addAttribute("employeeData", employeeData);
            model.addAttribute("wrapperString", wrapperString);
            model.addAttribute("updateError", e.getMessage());
            return "employee/update-form";
        }

        if (employeeData.getUsername().equals(wrapperString.getOldUsername())) {
            return "redirect:/";
        }
        return "redirect:/employee/showData";
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

    @GetMapping("/delete")
    public String delete(
            @CurrentSecurityContext(expression = "authentication.name") String username
    ) {
        Integer id = employeeDetailService.findByUsername(username).getId();
        employeeService.deleteById(id);

        return "redirect:/";
    }


    @GetMapping("/showAddCarForm")
    public String showFormForAddCar(Model model) {

        CarData carData = new CarData();
        model.addAttribute("carData", carData);
        return "employee/add-car-form";
    }

    @PostMapping("/processAddCar")
    public String processAddCar(@ModelAttribute("carData") CarData carData
            , BindingResult bindingResult
            , Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {


        //form validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("carData", carData);
            return "employee/add-car-form";
        }

        carData.setUsername(username);
        try {
            carService.save(carData);
        } catch (MyCarAlreadyExistException e) {
            e.printStackTrace();
        }

        return "redirect:/employee/dashboard";
    }

    @GetMapping("/showCarList")
    public String showEmployeeCarList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<Car> cars = carService.findAllCarsByUsername(username);
        model.addAttribute("cars", cars);
        return "employee/show-car-list";
    }

    //Create ordering
    @GetMapping("/showCreateOrderForm")
    public String showCreateOrderForm(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<Car> cars = carService.findAllCarsByUsername(username);
        List<CarData> carDataList = new ArrayList<>();
        for(Car car : cars) {
            carDataList.add(carService.getCarData(car));
        }
        model.addAttribute("carDataList", carDataList);
        model.addAttribute("orderEmployeeData", new OrderEmployeeData());
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("cities", cityService.loadCites());

        return "employee/show-create-order-form";
    }

    @PostMapping(value = "/addCarToOrder")
    public String addCarToOrder(@ModelAttribute("orderEmployeeData") OrderEmployeeData orderEmployeeData,
                                Model model) {
        System.out.println(orderEmployeeData.getCarData());
        model.addAttribute("orderEmployeeData", orderEmployeeData);
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("cities", cityService.loadCites());
        return "employee/show-create-order-form";
    }

    @PostMapping("/processCreateOrder")
    public String processCreateOrder(@ModelAttribute("orderEmployeeData") OrderEmployeeData orderEmployeeData
            , BindingResult bindingResult
            , Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        //form validation
        if (bindingResult.hasErrors()) {
            return "employee/show-create-order-form";
        }
        orderService.createOrder(username, orderEmployeeData);
        return "redirect:/employee/showOrderList";
    }

    @GetMapping("/showOrderList")
    public String showOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {
        model.addAttribute("orderEmployeeData", new OrderEmployeeData());
        model.addAttribute
                ("orderEmployeeDataList", orderService.getOrderEmployeeDataListByUsernameAndStanIsNotCompleted(username));

        return "employee/show-order-list";
    }

    @PostMapping("/processOrderChoose")
    public String processOrderChoose(@RequestParam("orderId") Integer orderId,
                                     Model model) {
        OrderEmployeeData orderEmployeeData = orderService.getOrderEmployeeDataByOrderAndStanEqualsWorkshopAnswer
                        (orderService.findOrderById(orderId));
        model.addAttribute("orderEmployeeData", orderEmployeeData);
        return "employee/show-workshop-answer-list-for-order";
    }

    @PostMapping("/processOrderForImplementationChoose")
    public String processOrderForImplementationChoose(@RequestParam("orderAnswerId") Integer orderAnswerId,
                                     Model model) {

        orderAnswerService.chooseOrderAnswerForImplementation(orderAnswerService.findById(orderAnswerId));
        return "redirect:/employee/dashboard";
    }

    @GetMapping("/showCompletedOrderList")
    public String showCompletedOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {
        List<OrderEmployeeData> orderEmployeeDataList
                = orderService.getOrderEmployeeDataListByUsernameAndStanEqualsCompleted(username);
        model.addAttribute
                ("orderEmployeeDataList", orderEmployeeDataList);
        return "employee/show-completed-order-list";
    }

    @GetMapping("/showOption")
    public String showShowOption(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {
        model.addAttribute
                ("employeeData", employeeService.getEmployeeDataByUsername(username));
        return "employee/show-option";
    }

}
