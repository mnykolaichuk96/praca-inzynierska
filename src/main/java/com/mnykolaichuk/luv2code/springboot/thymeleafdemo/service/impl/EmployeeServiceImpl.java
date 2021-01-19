package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.*;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.WrapperString;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.*;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.AuthorityEnum;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.Stan;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.AbstractService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.SecureTokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmployeeServiceImpl extends AbstractService implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;

    @Autowired
    private EmployeeCarRepository employeeCarRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderAnswerRepository orderAnswerRepository;

   @Override
    public void register(EmployeeData employeeData) throws UserAlreadyExistException, EmailAlreadyExistException {
        if(checkIfUserExist(employeeData.getUsername())) {
            employeeData.setUsername(null);
            throw new UserAlreadyExistException("Podany username jest już zajęty");
        }
        if(checkIfEmailExist(employeeData.getEmail())) {
            //перевірка для того якщо юзер створив ордер не запеєстрований, він є в базі. хоче зареєструватися. щоб міг.
            if(employeeDetailRepository.findEmployeeDetailByEmail(employeeData.getEmail()).getEmployee() != null) {
                employeeData.setEmail(null);
                throw new EmailAlreadyExistException("Podany adres e-mail jest już zajęty");
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
        employeeRepository.save(employee);
        sendRegistrationConfirmationEmail(employeeDetail);
    }

    private void encodePassword(EmployeeData source, Employee target) {
        target.setPassword(passwordEncoder.encode(source.getPassword()));
    }

    @Override
    public boolean verifyEmployee(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            throw new InvalidTokenException("Token is not valid");
        }
        EmployeeDetail employeeDetail =
                employeeDetailRepository.getOne(secureToken.getEmployeeDetail().getId());
        if(Objects.isNull(employeeDetail)){
            return false;
        }
        employeeDetail.setAccountVerified(true);
        employeeDetailRepository.save(employeeDetail); // let's same user details

        // we don't need invalid password now
        secureTokenService.removeToken(secureToken);
        return true;
    }

    @Override
    public void update(EmployeeData employeeData, WrapperString wrapperString)
            throws UserAlreadyExistException, EmailAlreadyExistException {
        boolean isEmailChange = false;
        boolean isUsernameChange = false;
        if(!employeeData.getUsername().equals(wrapperString.getOldUsername())) {
            isUsernameChange = true;
            if (checkIfUserExist(employeeData.getUsername())) {
                employeeData.setUsername(null);
                throw new UserAlreadyExistException("User already exist");
            }
        }
        if(!employeeData.getEmail().equals(wrapperString.getOldEmail())) {
            isEmailChange = true;
            if (checkIfEmailExist(employeeData.getEmail())) {
                employeeData.setEmail(null);
                throw new EmailAlreadyExistException("Email address already used");
            }
        }
        if(isUsernameChange) {
            Employee employee = employeeRepository.findEmployeeByUsername(wrapperString.getOldUsername());
            employee.setUsername(employeeData.getUsername());
            employeeRepository.save(employee);
        }
        EmployeeDetail employeeDetail =
                employeeRepository.findEmployeeByUsername(wrapperString.getOldUsername()).getEmployeeDetail();
        BeanUtils.copyProperties(employeeData, employeeDetail);
        if(isEmailChange) {
            employeeDetail.setAccountVerified(false);
            employeeDetailRepository.save(employeeDetail);
            sendRegistrationConfirmationEmail(employeeDetail);
        }
        else {
            employeeDetailRepository.save(employeeDetail);
        }
    }

    @Override
    public EmployeeData getEmployeeDataByUsername(String username) {
        EmployeeData employeeData = new EmployeeData();
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        BeanUtils.copyProperties(employee, employeeData);
        BeanUtils.copyProperties(employeeDetailRepository.findEmployeeDetailByEmployeeUsername(username), employeeData);
        employeeData.setMatchingPassword(employee.getPassword());
        return employeeData;
    }

    @Override
    public void deleteById(Integer id) {

       for(EmployeeCar employeeCar : employeeCarRepository.findAllByEmployeeId(id)) {
           carRepository.deleteById(employeeCar.getCarId());
           employeeCarRepository.delete(employeeCar);
       }
       for(Order order : orderRepository.findAllByEmployeeDetail(employeeDetailRepository.findEmployeeDetailById(id))) {
           if(order.getOrderAnswers().get(0).getStan()!= Stan.COMPLETED) {
               orderRepository.deleteById(order.getId());
           }
           if(order.getOrderAnswers().get(0).getWorkshop() == null){
               orderRepository.deleteById(order.getId());
           }
       }
       employeeRepository.deleteById(id);
    }

    @Override
    public Employee findByUsername(String username) {
        return employeeRepository.findEmployeeByUsername(username);
    }

    @Override
    public Employee findById(Integer id) {
        return employeeRepository.findEmployeeById(id);
    }

    @Override
    public boolean comparePassword(String password, String encodePassword) {
        passwordEncoder = new BCryptPasswordEncoder();
        boolean isPasswordMatches = passwordEncoder.matches(password, encodePassword);
        if (isPasswordMatches)
            return true;
        return false;
    }

    private EmployeeData copyProperties(Employee employee, EmployeeDetail employeeDetail) {
        System.out.println(employee.getUsername());
        EmployeeData employeeData = new EmployeeData();
        employeeData.setUsername(employee.getUsername());
        System.out.println(employeeData.getUsername());
        employeeData.setPassword(employee.getPassword());
        employeeData.setMatchingPassword(employee.getPassword());
        employeeData.setFirstName(employeeDetail.getFirstName());
        employeeData.setLastName(employeeDetail.getLastName());
        employeeData.setEmail(employeeDetail.getEmail());
        employeeData.setPhoneNumber(employeeDetail.getPhoneNumber());

        return employeeData;
    }

    private boolean checkIfUserExist(String username) {
        return employeeRepository.findEmployeeByUsername(username) != null ? true : false;
    }

    private boolean checkIfEmailExist(String email) {
        return employeeRepository.findEmployeeByEmployeeDetailEmail(email) != null ? true : false;
    }
}
