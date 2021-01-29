package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller.admin;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.CantDeleteCarModelException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.CantDeleteCityException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.CantDeleteWorkshopWhileImplementationExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeDetailService employeeDetailService;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CarModelService carModelService;

    @GetMapping("/dashboard")
    public String showEmployeeDashboard() {

        return "admin/dashboard";
    }

    @GetMapping("/showAllEmployee")
    public String showAllEmployee(Model model) {

        model.addAttribute("employeeDataList", employeeService.getAllEmployeeDataList());

        return "admin/all-employee";
    }

    @PostMapping("/deleteEmployee")
    public ModelAndView deleteEmployee(
            @RequestParam("employeeId") Integer employeeId) {

        employeeService.deleteByUsername(employeeService.findById(employeeId).getUsername());

        return new ModelAndView(new RedirectView("showAllEmployee"));
    }

    @GetMapping("/showAllWorkshop")
    public String showAllWorkshop(Model model) {

        model.addAttribute("workshopDataList", workshopService.getAllWorkshopDataList());

        return "admin/all-workshop";
    }

    @PostMapping("/deleteWorkshop")
    public ModelAndView deleteWorkshop(
            @RequestParam("workshopId") Integer workshopId) {

        try {
            workshopService.deleteByUsername(workshopService.findById(workshopId).getUsername());
        } catch (CantDeleteWorkshopWhileImplementationExistException e) {
            e.printStackTrace();
        }

        return new ModelAndView(new RedirectView("showAllWorkshop"));
    }

    @GetMapping("/showAllCity")
    public String showAllCity(Model model
            , @RequestParam(value = "errorMessage", required = false) String errorMessage) {

        model.addAttribute("cityDataList", cityService.getAllCityDataList());
        if(errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }

        return "admin/all-city";
    }

    @PostMapping("/deleteCity")
    public ModelAndView deleteCity(
            @RequestParam("cityId") Integer cityId) {

        RedirectView redirectView = new RedirectView();
        try {
           cityService.deleteCityById(cityId);
        } catch (CantDeleteCityException e) {
            redirectView.getAttributesMap().put("errorMessage", e.getMessage());
            redirectView.setUrl("showAllCity");
            return new ModelAndView(redirectView);
        }

        return new ModelAndView(new RedirectView("showAllCity"));
    }

    @PostMapping("/processAddCity")
    public ModelAndView deleteCity(
            @RequestParam("cityName") String cityName) {

        City city = new City(cityName);
        cityService.addCity(city);

        return new ModelAndView(new RedirectView("showAllCity"));
    }

    @GetMapping("/showAllCarModel")
    public String showAllCarModel(Model model
            , @RequestParam(value = "errorMessage", required = false) String errorMessage) {

        model.addAttribute("carModelDataList", carModelService.getAllCarModelDataList());
        if(errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        return "admin/all-car-model";
    }

    @PostMapping("/deleteCarModel")
    public ModelAndView deleteCarModel(
            @RequestParam("modelId") Integer modelId) {

        RedirectView redirectView = new RedirectView();
        try {
            carModelService.deleteByCarModelId(modelId);
        } catch (CantDeleteCarModelException e) {
            redirectView.getAttributesMap().put("errorMessage", e.getMessage());
            redirectView.setUrl("showAllCarModel");
            return new ModelAndView(redirectView);
        }

        return new ModelAndView(new RedirectView("showAllCarModel"));
    }

    @PostMapping("/processAddCarModel")
    public ModelAndView deleteAddCarModel(
              @RequestParam("make") String make
            , @RequestParam("model") String model) {

       carModelService.addCarModel(make, model);
       return new ModelAndView(new RedirectView("showAllCarModel"));
    }
}
