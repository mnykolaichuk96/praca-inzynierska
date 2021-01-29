package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.workshop;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.CantDeleteWorkshopWhileImplementationExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderAnswerData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderWorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.WorkshopData;
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

    @Value("${user.update.username.successful}")
    private String userUpdateUsernameSuccessful;

    @Value("${user.update.email.successful}")
    private String userUpdateEmailSuccessful;

    @Value("${user.add.car.exception}")
    private String userAddCarException;

    @Value("user.delete.implementation.order.exception")
    private String userDeleteImplementationOrderException;

    @Value("${user.delete.account.success}")
    private String userDeleteAccountSuccessful;

    @Value("${order.delete.success}")
    private String orderDeleteSuccessful;

    @GetMapping("/dashboard")
    public String showWorkshopDashboard(Model model
                                    , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("workshopData",workshopService.getWorkshopDataByUsername(username));
        return "workshop/dashboard";
    }

    @GetMapping("/showUpdateForm")
    public String showUpdateForm(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("workshopData", workshopService.getWorkshopDataByUsername(username));
        model.addAttribute("cities", cityService.loadCites());
        return "workshop/update-form";
    }

    @PostMapping("/processUpdateForm")
    public ModelAndView processUpdateForm(Model model
            , @Valid WorkshopData workshopData
            , BindingResult bindingResult
            , @RequestParam("oldUsername") String oldUsername
            , @RequestParam("oldEmail") String oldEmail) {

        RedirectView redirectView = new RedirectView();

        if (bindingResult.hasErrors()) {
            return new ModelAndView("workshop/update-form");
        }

        try {
            workshopService.update(workshopData, oldUsername, oldEmail);
        }
        catch (UserAlreadyExistException | EmailAlreadyExistException e){
            model.addAttribute("updateError", e.getMessage());
            return new ModelAndView("workshop/update-form").addObject(model);
        }

        if (workshopData.getUsername().equals(oldUsername) && workshopData.getEmail().equals(oldEmail)) {
            redirectView.setUrl("showOption");
            return new ModelAndView(redirectView);
        }
        if(!(workshopData.getUsername().equals(oldUsername) || workshopData.getEmail().equals(oldEmail))) {
            redirectView.getAttributesMap().put("message", userUpdateUsernameSuccessful + userUpdateEmailSuccessful);
        }
        else {
            if (!workshopData.getUsername().equals(oldUsername)) {
                redirectView.getAttributesMap().put("message", userUpdateUsernameSuccessful);
            }
            if (!workshopData.getEmail().equals(oldEmail)) {
                redirectView.getAttributesMap().put("message", userUpdateEmailSuccessful);
            }
        }
        redirectView.setUrl("/login");
        return new ModelAndView(redirectView);
    }

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
        try {
            workshopService.deleteByUsername(username);
        } catch (CantDeleteWorkshopWhileImplementationExistException e) {
            redirectView.getAttributesMap().put("message", userDeleteImplementationOrderException);
            redirectView.setUrl("showImplementationOrderList");
            return new ModelAndView(redirectView);
        }

        redirectView.getAttributesMap().put("message", userDeleteAccountSuccessful);
        redirectView.setUrl("/");
        return new ModelAndView(redirectView);
    }

    @PostMapping("/deleteOrder")
    public ModelAndView deleteOrder(@RequestParam("orderAnswerId") Integer orderAnswerId,
                              @CurrentSecurityContext(expression = "authentication.name") String username) {

        RedirectView redirectView = new RedirectView();
        try {
            orderAnswerService.deleteOrderAnswerFromWorkshopByOrderAnswerAndWorkshopUsername
                    (orderAnswerService.findById(orderAnswerId), username);
        } catch (CantDeleteWorkshopWhileImplementationExistException e) {
            e.getStackTrace();
        }
        redirectView.getAttributesMap().put("message", orderDeleteSuccessful);
        redirectView.setUrl("showCreatedOrderList");
        return new ModelAndView(redirectView);
    }

    @GetMapping("/showCreatedOrderList")
    public String showCreatedOrderList(Model model
            , @RequestParam(value = "message", required = false) String message
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsCreated(username);
        if (model.asMap().size() == 0) {
            model.addAttribute("orderAnswerData", new OrderAnswerData());
        }
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);
        if(message != null) {
            model.addAttribute("message", message);
        }
        return "workshop/created-order-list";
    }

    @PostMapping("/processOrderChoose")
    public ModelAndView processOrderChoose(
            @Valid @ModelAttribute("orderAnswerData") OrderAnswerData orderAnswerData
            , BindingResult bindingResult
            , RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderAnswerData", bindingResult);
            redirectAttributes.addFlashAttribute("orderAnswerData", orderAnswerData);
            return new ModelAndView(new RedirectView("showCreatedOrderList"));
        }
        orderAnswerService.createWorkshopAnswerByOrderAnswerData(orderAnswerData);
        return new ModelAndView(new RedirectView("showCreatedOrderList"));
    }

    @GetMapping("/showImplementationOrderList")
    public String showImplementationOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsImplementation(username);
        model.addAttribute("orderAnswerData", new OrderAnswerData());
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);
        model.addAttribute("message", null);
        return "workshop/implementation-order-list";
    }

    @PostMapping("/processOrderCompleted")
    public ModelAndView processOrderCompleted(@RequestParam("orderAnswerId") Integer orderAnswerId) {

        orderAnswerService.chooseOrderAnswerForCompleted(orderAnswerService.findById(orderAnswerId));
        return new ModelAndView(new RedirectView("showCreatedOrderList"));
    }

    @GetMapping("/showCompletedOrderList")
    public String showCompletedOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsCompleted(username);
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);
        return "workshop/completed-order-list";
    }

    @GetMapping("/showOption")
    public String showShowOption(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute
                ("workshopData", workshopService.getWorkshopDataByUsername(username));
        return "workshop/option";
    }

}
