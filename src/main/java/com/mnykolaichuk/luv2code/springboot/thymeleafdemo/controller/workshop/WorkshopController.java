package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.workshop;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.WrapperString;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderAnswerData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderWorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.WorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/workshop")
public class WorkshopController {
    @Autowired
    WorkshopService workshopService;

    @Autowired
    CityService cityService;

    @Autowired
    CarService carService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @GetMapping("/dashboard")
    public String showWorkshopDashboard(Model model
                                    , @CurrentSecurityContext(expression = "authentication.name") String username) {
        Workshop workshop = new Workshop();
        workshop = workshopService.findByUsername(username);
        model.addAttribute("loginedWorkshop",workshop);
        return "workshop/dashboard";
    }

    @GetMapping("/showData")
    public String showData(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        Workshop workshop = workshopService.findByUsername(username);
        model.addAttribute("workshop", workshop);
        return "workshop/show-data";
    }

    @GetMapping("/showUpdateForm")
    public String showUpdateForm(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        WorkshopData workshopData = workshopService.getWorkshopDataByUsername(username);
        WrapperString wrapperString = getWrapperString();
        wrapperString.setOldUsername(workshopData.getUsername());    // Old Username
        wrapperString.setOldEmail(workshopData.getEmail());         //Old Email
        model.addAttribute("workshopData", workshopData);
        model.addAttribute("wrapperString", wrapperString);
        model.addAttribute("cities", cityService.loadCites());
        return "workshop/update-form";
    }

    @PostMapping("/processUpdateForm")
    public String processUpdateForm(
            @Valid @ModelAttribute("workshopData") WorkshopData workshopData,
            BindingResult bindingResult,
            @ModelAttribute("wrapperString") WrapperString wrapperString,
            Model model) {
        // form validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("wrapperString", wrapperString);
            model.addAttribute("cities", cityService.loadCites());
            model.addAttribute("workshopData", workshopData);
            return "workshop/update-form";
        }

        try {
            workshopService.update(workshopData, wrapperString);
        }
        catch (UserAlreadyExistException | EmailAlreadyExistException e){
            model.addAttribute("workshopData", workshopData);
            model.addAttribute("updateError", e.getMessage());
            return "workshop/update-form";
        }

        if(workshopData.getUsername().equals(wrapperString.getOldEmail())){
            return "redirect:/";
        }
        return "redirect:/workshop/showData";
    }

    private WrapperString getWrapperString() {return new WrapperString();}

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

    @GetMapping("/processDelete")
    public String processDelete(
            @CurrentSecurityContext(expression = "authentication.name") String username
            ) {

        int id = workshopService.findByUsername(username).getId();
        workshopService.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/showCreatedOrders")
    public String showCreatedOrders(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {
        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsCreated(username);
        model.addAttribute("orderAnswerData", new OrderAnswerData());
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);
        return "workshop/show-created-orders";
    }

//    @GetMapping("/showOrderCreated")
//    public String showOrderCreated(Model model
//            , @CurrentSecurityContext(expression = "authentication.name") String username) {
//        List<OrderEmployeeData> orderDataList =
//                orderService.getOrderDataListByUsernameAndStanEqualsCreated(username);
//        model.addAttribute("orderDataList", orderDataList);
//        model.addAttribute("orderDataAnswer", new OrderWorkshopData());
//        return "workshop/show-order-created";
//    }
//
//
    @PostMapping("/processOrderChoose")
    public String processOrderChoose(
            @Valid @ModelAttribute("orderAnswerData") OrderAnswerData orderAnswerData,
            BindingResult bindingResult,
            Model model) {
       orderAnswerService.createWorkshopAnswerByOrderAnswerData(orderAnswerData);
//        return "redirect:/";
//         form validation
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("wrapperString", wrapperString);
//            model.addAttribute("cities", cityService.loadCites());
//            model.addAttribute("workshopData", workshopData);
//            return "workshop/update-form";
//        }
//
//        try {
//            workshopService.update(workshopData, wrapperString);
//        }
//        catch (UserAlreadyExistException | EmailAlreadyExistException e){
//            model.addAttribute("workshopData", workshopData);
//            model.addAttribute("updateError", e.getMessage());
//            return "workshop/update-form";
//        }
//
//        if(workshopData.getUsername().equals(wrapperString.getOldEmail())){
//            return "redirect:/";
//        }
        return "redirect:/workshop/showData";
    }

    @GetMapping("/showImplementationOrders")
    public String showImplementationOrders(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {
        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsImplementation(username);
        model.addAttribute("orderAnswerData", new OrderAnswerData());
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);
        return "workshop/show-implementation-orders";
    }

    @PostMapping("/processOrderCompleted")
    public String processOrderCompleted(@RequestParam("orderAnswerId") Integer orderAnswerId) {

        orderAnswerService.chooseOrderAnswerForCompleted(orderAnswerService.findById(orderAnswerId));
        return "redirect:/workshop/showData";
    }

    @GetMapping("/showCompletedOrders")
    public String showCompletedOrders(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {
        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsCompleted(username);
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);
        return "workshop/show-completed-orders";
    }

}



















//    @GetMapping("/employees")
//    public String getAllEmployees(Model model) {
//        List<Employee> employees = employeeService.findAll();
//        model.addAttribute("employees", employees);
//
//        return "user/all-employees";
//    }

//    @GetMapping("/showFormForAdd")
//    public String showFormForAdd(Model model) {
//        Employee tempEmployee = new Employee();
//        model.addAttribute("employee", tempEmployee);
//
//        return "user/employee-form";
//    }

//    @PostMapping("/save")
//    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
//        employeeService.save(employee);
//        return "redirect:/user/employees";
//    }
//
//    @GetMapping("/showFormForUpdate")
//    public String showFormForUpdate(@RequestParam("employeeId") int id, Model model) {
//        Employee employee = employeeService.findById(id);
//        model.addAttribute("employee", employee);
//
//        return "user/employee-form";
//    }
//
//    @GetMapping("/delete")
//    public String deleteEmployee(@RequestParam("employeeId") int id) {
//        employeeService.deleteById(id);
//
//        return "redirect:/user/employees";
//    }

//    @GetMapping("/repairTypes")
//    public String getAllRepairTypes(Model model) {
//        List<RepairType> repairTypes = repairTypeService.findAll();
//        model.addAttribute("repairTypes", repairTypes);
//
//        return "user/all-employees";
//    }

//    @GetMapping("/cities")
//    public String getAllCities(Model model) {
//        Employee employee = new Employee();
//        List<City> cities = cityService.findAll();
//        model.addAttribute("cities", cities);
//        model.addAttribute("employee", employee);
//
//        return "user/all-employees";
//    }











