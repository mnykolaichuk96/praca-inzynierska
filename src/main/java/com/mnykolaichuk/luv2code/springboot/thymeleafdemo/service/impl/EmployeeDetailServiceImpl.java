package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeDetailRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.AccountVerificationEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.OrderAnswer;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.SecureToken;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeDetailData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmailService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeDetailService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.OrderService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.SecureTokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import javax.persistence.NonUniqueResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeDetailServiceImpl implements EmployeeDetailService {

    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Override
    public EmployeeDetail findByEmail(String email) {
        EmployeeDetail employeeDetail;
        try {
            employeeDetail = employeeDetailRepository.findEmployeeDetailByEmail(email);
            return employeeDetail;
        } catch (NullPointerException e) {
            return null;
            //jeżeli więcej niż jeden wynik wracamy EmployeeDetail danych dodanych przy wcześniejszym tworzeniu zlecenia
        } catch (IncorrectResultSizeDataAccessException | NonUniqueResultException e) {
            List <EmployeeDetail> employeeDetails = employeeDetailRepository.findAllByEmail(email);
            for(EmployeeDetail employeeDetailWithUnregisteredStan : employeeDetails) {
                if(isOrderInEmployeeDetail(employeeDetailWithUnregisteredStan) && (!isEmployeeInEmployeeDetail(employeeDetailWithUnregisteredStan))) {
                    return employeeDetailWithUnregisteredStan;
                }
            }
        }
        return null;
    }

    @Override
    public EmployeeDetail findByEmployeeUsername(String username) {
        try {
            return employeeDetailRepository.findEmployeeDetailByEmployeeUsername(username);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public EmployeeDetail findById(Integer id) {
        try {
            return employeeDetailRepository.findEmployeeDetailById(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void delete(EmployeeDetail employeeDetail) {
        SecureToken secureToken = secureTokenService.findByEmployeeDetail(employeeDetail);
        if(secureToken != null) {
            secureToken.setEmployeeDetail(null);
            secureTokenService.saveSecureToken(secureToken);
        }
        employeeDetailRepository.deleteEmployeeDetailById(employeeDetail.getId());
    }

    @Override
    public void save(EmployeeDetail employeeDetail) {
        employeeDetailRepository.save(employeeDetail);
    }

    @Override
    public EmployeeDetailData getEmployeeDetailData(EmployeeDetail employeeDetail) {
        EmployeeDetailData employeeDetailData = new EmployeeDetailData();
        if(employeeDetail == null) {
            return null;
        }
        BeanUtils.copyProperties(employeeDetail, employeeDetailData);
        return employeeDetailData;
    }

    @Override
    public void sendEmployeeDetailEmailVerificationEmail(EmployeeDetail employeeDetail) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { employeeDetailEmailVerificationEmail(employeeDetail);

            }
        });
    }

    private void employeeDetailEmailVerificationEmail(EmployeeDetail employeeDetail) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        //przypisuje do SecurityToken nowe objekt EmployeeDetail
        secureToken.setEmployeeDetail( employeeDetail);
        secureToken.setNotNewEmployeeDetail(false);
        if(isOrderInEmployeeDetail(employeeDetail)) {
            if (employeeDetail.getOrders().size() != 0) {
                //w pole carId zapisany orderId zeby wiedzieć jaki order będzie stworzony po veryfikacji email
                secureToken.setVerificationCarId(employeeDetail.getOrders().get(0).getId());
            }
        }
        secureTokenService.saveSecureToken(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(employeeDetail);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotNewEmployeeDetailEmailVerificationEmail(EmployeeDetail employeeDetail) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { notNewEmployeeDetailEmailVerificationEmail(employeeDetail);

            }
        });
    }

    private void notNewEmployeeDetailEmailVerificationEmail(EmployeeDetail employeeDetail) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        //przypisuje do SecurityToken nowe objekt EmployeeDetail
        secureToken.setEmployeeDetail( employeeDetail);
        secureToken.setNotNewEmployeeDetail(true);
        if(isOrderInEmployeeDetail(employeeDetail)) {
            //w pole carId zapisany orderId zeby wiedzieć jaki order będzie stworzony po veryfikacji email
            secureToken.setVerificationCarId(employeeDetail.getOrders().get(0).getId());
        }
        secureTokenService.saveSecureToken(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(employeeDetail);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyEmployee(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            throw new InvalidTokenException("Token is not valid");
        }
        EmployeeDetail employeeDetail = findById(secureToken.getEmployeeDetail().getId());
        if(Objects.isNull(employeeDetail)){
            return false;
        }
        //jeżeli tokien zawiera orderId, zlecenie stworzone przez niezarejestrowanego klienta. Ono teraz będzie dostępne
        //dla warsztatów oraz na ich pocztę będzie wysłana wiadomość o nowym zleceniu.
        Integer orderId = secureToken.getVerificationCarId();

        if(orderId != null) {
            if(orderService.isOrderAnswerInOrder(orderService.findOrderById(orderId))) {
                for (OrderAnswer orderAnswer : orderService.findOrderById(orderId).getOrderAnswers()) {
                    orderService.sendToWorkshopListCreateOrderInformationEmail(orderAnswer);
                }
            }
        }
        if(secureToken.isNotNewEmployeeDetail()) {
            //przepisanie dotych czas stworzonych zleceń do aktualnego EmployeeDetail
            List<Order> orderList = new ArrayList<>();
            EmployeeDetail oldEmployeeDetail = findByEmail(employeeDetail.getEmail());
            if(isOrderInEmployeeDetail(oldEmployeeDetail)) {
                for (Order order : oldEmployeeDetail.getOrders()) {
                    order.setEmployeeDetail(null);
                    orderService.save(order);
                    orderList.add(order);
                }
            }
            delete(oldEmployeeDetail);
            save(employeeDetail);
            for (Order order : orderList) {
                order.setEmployeeDetail(employeeDetail);
                orderService.save(order);
            }
        }
        employeeDetail.setAccountVerified(true);

        save(employeeDetail);

        secureTokenService.removeToken(secureToken);
        return true;
    }

    @Override
    public boolean isOrderInEmployeeDetail(EmployeeDetail employeeDetail) {
       try {
           return employeeDetail.getOrders() != null ? true : false;
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
