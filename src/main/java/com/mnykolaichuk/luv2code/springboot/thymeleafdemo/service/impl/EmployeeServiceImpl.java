package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.AuthorityRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Car;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Employee;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.AuthorityEnum;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private CarService carService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeDetailService employeeDetailService;

    @Autowired
    private OrderService orderService;

   @Override
    public void register(EmployeeData employeeData) throws UserAlreadyExistException, EmailAlreadyExistException {
       boolean isEmployeeDetailNotNew = false;
       //sprawdza czy istnieje klient albo warsztat z takim username
       if(checkIfUsernameExist(employeeData.getUsername())) {
           //zeruje pole username objektu który będzie wysłany na Front End
            employeeData.setUsername(null);
            throw new UserAlreadyExistException("Podany username jest już zajęty");
        }
       //sprawdza czy istnieje klient z takim email
       if(checkIfEmailExist(employeeData.getEmail())) {
           if (employeeDetailService.isEmployeeInEmployeeDetail(employeeDetailService.findByEmail(employeeData.getEmail()))) {
               employeeData.setEmail(null);
               throw new EmailAlreadyExistException("Podany adres e-mail jest już zajęty");
           } else {
               isEmployeeDetailNotNew = true;
           }
       }
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeData, employee);
        EmployeeDetail employeeDetail = new EmployeeDetail();
        BeanUtils.copyProperties(employeeData, employeeDetail);
        encodePassword(employeeData, employee);
        employee.setAuthorities
                (Stream.of(authorityRepository.findByAuthority(AuthorityEnum.ROLE_EMPLOYEE))
                .collect(Collectors.toSet()));
        employee.setEmployeeDetail(employeeDetail);
        employeeDetailService.save(employeeDetail);
        employeeRepository.save(employee);
        if(isEmployeeDetailNotNew) {
            employeeDetailService.sendNotNewEmployeeDetailEmailVerificationEmail(employeeDetail);
        }
        else {
            employeeDetailService.sendEmployeeDetailEmailVerificationEmail(employeeDetail);
        }
    }

    private void encodePassword(EmployeeData source, Employee target) {
        target.setPassword(passwordEncoder.encode(source.getPassword()));
    }

    @Override
    public void update(EmployeeData employeeData, String oldUsername, String oldEmail)
            throws UserAlreadyExistException, EmailAlreadyExistException {
        boolean isEmailChange = false;
        boolean isUsernameChange = false;
        //jeżeli username został zmieniony
        if(!employeeData.getUsername().equals(oldUsername)) {
            isUsernameChange = true;
            //sprawdza czy nowy username nie jest zajęty przez innego użytkownika
            if(checkIfUsernameExist(employeeData.getUsername())) {
                employeeData.setUsername(null);
                throw new UserAlreadyExistException("User already exist");
            }
        }
        //jeżeli email został zmieniony
        if(!employeeData.getEmail().equals(oldEmail)) {
            isEmailChange = true;
            //sprawdza czy email adres nie jest wykorzystany przez innego klienta
            if (checkIfEmailExist(employeeData.getEmail())) {
                employeeData.setEmail(null);
                throw new EmailAlreadyExistException("Email address already used");
            }
        }
        //jeżeli username został zmieniony aktualizuje nowy username w bazie danych
        if(isUsernameChange) {
            Employee employee = employeeRepository.findEmployeeByUsername(oldUsername);
            employee.setUsername(employeeData.getUsername());
            employeeRepository.save(employee);
        }
        //aktualizuje wszystkie dane klienta
        EmployeeDetail employeeDetail =
                employeeRepository.findEmployeeByUsername(employeeData.getUsername()).getEmployeeDetail();
        BeanUtils.copyProperties(employeeData, employeeDetail);
        //jeżeli email został zmieniony, konto nie będzie ważne do weryfikacji nowego adresu email
        if(isEmailChange) {
            employeeDetail.setAccountVerified(false);
            employeeDetailService.save(employeeDetail);
            employeeDetailService.sendEmployeeDetailEmailVerificationEmail(employeeDetail);
        }
        else {
            employeeDetailService.save(employeeDetail);
        }
    }

    @Override
    public EmployeeData getEmployeeDataByUsername(String username) {
        EmployeeData employeeData = new EmployeeData();
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        BeanUtils.copyProperties(employee, employeeData);
        BeanUtils.copyProperties(employeeDetailService.findByEmployeeUsername(username), employeeData);
        employeeData.setMatchingPassword(employee.getPassword());
        employeeData.setEmployeeId(employee.getId());
        return employeeData;
    }

    @Override
    public List<EmployeeData> getAllEmployeeDataList() {
       List<EmployeeData> allEmployeeDataList = new ArrayList<>();
       List<Employee> allEmployeeList = employeeRepository.findAll();
       allEmployeeList.remove(employeeRepository.findEmployeeById(1));
       for(Employee employee : allEmployeeList) {
           allEmployeeDataList.add(getEmployeeDataByUsername(employee.getUsername()));
       }
       return allEmployeeDataList;
    }

    @Override
    public void deleteByUsername(String username) {
       Employee employee = employeeRepository.findEmployeeByUsername(username);
       //usuwa wszystkie samochody przypisane do klienta
       if(isCarInEmployee(employee)) {
           for (Car car : employee.getCars()) {
               carService.deleteByCarAndEmployee(car, employee);
           }
       }

       //usuwa wszystkie zlecenia klienta
        if(employeeDetailService.isOrderInEmployeeDetail(employee.getEmployeeDetail())) {
            for (Order order : employee.getEmployeeDetail().getOrders()) {
                orderService.deleteOrderFromEmployeeByOrderAndEmployeeUsername(order, username);
            }
        }
        EmployeeDetail employeeDetail = employee.getEmployeeDetail();
        employeeRepository.save(employee);
        employeeRepository.deleteEmployeeById(employee.getId());
        employeeDetailService.delete(employeeDetail);

    }

    @Override
    public void deleteEmployeeCarByCarIdAndUsername(Integer carId, String username) {
        Car car = carService.findById(carId);
        carService.deleteByCarAndEmployee(car, findByUsername(username));
    }

    @Override
    public Employee findByUsername(String username) {
        Employee employee = employeeRepository.findEmployeeByUsername(username);

        return employee;
    }

    @Override
    public Employee findById(Integer id) {
        Employee employee = employeeRepository.findEmployeeById(id);

        return employee;
    }

    @Override
    public boolean comparePassword(String password, String encodePassword) {
        passwordEncoder = new BCryptPasswordEncoder();
        boolean isPasswordMatches = passwordEncoder.matches(password, encodePassword);
        if (isPasswordMatches)
            return true;
        return false;
    }

    private boolean checkIfUsernameExist(String username) {
        return employeeRepository.findEmployeeByUsername(username) != null || workshopService.findByUsername(username) != null ? true : false;
    }

    private boolean checkIfEmailExist(String email) {
        return employeeDetailService.findByEmail(email) != null ? true : false;
    }

    @Override
    public boolean isCarInEmployee(Employee employee) {
        try {
            return employee.getCars() != null ? true : false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean isEmployeeInEmployeeDetail(EmployeeDetail employeeDetail) {
        try {
            return employeeDetail.getEmployee() != null ? true : false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
