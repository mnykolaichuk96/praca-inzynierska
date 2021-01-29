package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.SecureToken;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/verify")
public class VerifyController {

    @Autowired
    private CarService carService;

    @Autowired
    private MessageSource messageSource;

    private static final String REDIRECT_LOGIN= "redirect:/login";

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmployeeDetailService employeeDetailService;


    @GetMapping("/user")
    public String verifyUser(@RequestParam(required = false) String token, final Model model, RedirectAttributes redirAttr){
        SecureToken secureToken = null;
        if(StringUtils.isEmpty(token)){
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.missing.token"
                    , null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }
        EmployeeDetail employeeDetail = null;
        try {
            employeeDetail = secureTokenService.findByToken(token).getEmployeeDetail();
        } catch (NullPointerException e) {
        }
        try {
            if(employeeDetail != null) {
                employeeDetailService.verifyEmployee(token);
            }
            else {
                workshopService.verifyWorkshop(token);
            }
        } catch (InvalidTokenException e) {
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.invalid.token"
                    , null,LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }

        redirAttr.addFlashAttribute("verifiedAccountMsg", messageSource.getMessage("user.registration.verification.success"
                , null,LocaleContextHolder.getLocale()));
        return REDIRECT_LOGIN;
    }

    @GetMapping("/car")
    public String verifyCar(@RequestParam(required = false) String token, final Model model, RedirectAttributes redirAttr){
        SecureToken secureToken = null;
        if(StringUtils.isEmpty(token)){
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.missing.token"
                    , null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }
        try {
            carService.verifyCar(token);
        } catch (InvalidTokenException e) {
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.invalid.token"
                    , null,LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }

        redirAttr.addFlashAttribute("verifiedAccountMsg", messageSource.getMessage("user.registration.verification.success"
                , null,LocaleContextHolder.getLocale()));
        return REDIRECT_LOGIN;
    }

}
