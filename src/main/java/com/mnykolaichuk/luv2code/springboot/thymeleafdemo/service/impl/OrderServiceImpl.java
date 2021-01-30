package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.OrderRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.NullOrderAnswerForOrderException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.NullWorkshopInCityException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.CreateOrderEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.InformationAboutAccountDeletingEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.OrderAnswer;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CarData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderAnswerData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderEmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderWorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.Stan;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private EmployeeDetailService employeeDetailService;

    @Autowired
    private CarService carService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @Autowired
    private CityService cityService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Value("http://localhost:8080/workshop/showCreatedOrderList")
    private String SHOW_CREATED_ORDER_LIST_URL;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Override
    public void createOrder(String username, OrderEmployeeData orderEmployeeData) throws NullWorkshopInCityException {
        if(workshopService.findAllByCity(cityService.findByCityName(orderEmployeeData.getCityName())).size() == 0) {
            throw new NullWorkshopInCityException("Sorry but in this moment in chosen city aren't workshops");
        }
        else {
            Order order = new Order();
            order.setDescription(orderEmployeeData.getDescription());
            order.setCreationDate(orderEmployeeData.getCreationDate());
            order.setEmployeeDetail(employeeDetailService.findByEmployeeUsername(username));
            order.setCity(cityService.findByCityName(orderEmployeeData.getCityName()));
            order.setCar(carService.saveForOrder(orderEmployeeData.getCarData()));

            orderRepository.save(order);
            for (Workshop workshop : workshopService.findAllByCity(order.getCity())) {
                OrderAnswer tempOrderAnswer = new OrderAnswer();
                tempOrderAnswer.setStan(Stan.CREATED);
                tempOrderAnswer.setWorkshop(workshop);
                tempOrderAnswer.setOrder(order);
                orderAnswerService.save(tempOrderAnswer);
                sendToWorkshopListCreateOrderInformationEmail(tempOrderAnswer);
            }
        }
    }

    @Override
    public void sendToWorkshopListCreateOrderInformationEmail(OrderAnswer orderAnswer) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { createOrderInformationEmail(orderAnswer);

            }
        });
    }

    private void createOrderInformationEmail(OrderAnswer orderAnswer) {
        CreateOrderEmailContext emailContext = new CreateOrderEmailContext();
        emailContext.init(orderAnswer);
        emailContext.setInformationUrl(SHOW_CREATED_ORDER_LIST_URL);
        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
                e.printStackTrace();
        }
    }

    private OrderEmployeeData getOrderEmployeeDataByOrder(Order order) {
        OrderEmployeeData orderEmployeeData = new OrderEmployeeData();
        orderEmployeeData.setOrderId(order.getId());
        orderEmployeeData.setDescription(order.getDescription());
        orderEmployeeData.setCreationDate(order.getCreationDate());
        orderEmployeeData.setCityName(order.getCity().getCityName());
        if(order.getOrderAnswers() == null) {
            order.setOrderAnswers(null);
        } else {
            orderEmployeeData.setOrderAnswerDataList
                    (orderAnswerService.getOrderAnswerDataListByOrderAnswerList(order.getOrderAnswers()));
        }
        orderEmployeeData.setCarData(carService.getCarData(order.getCar()));

        return orderEmployeeData;
    }

    @Override
    public void createOrderFromUnregisteredUser(OrderWorkshopData orderWorkshopData) throws EmailAlreadyExistException, NullWorkshopInCityException {
        boolean notNew = false;

        if(workshopService.findAllByCity(cityService.findByCityName(orderWorkshopData.getCityName())).size() == 0) {
            throw new NullWorkshopInCityException("Sorry but in this moment in chosen city aren't workshops");
        }
        if(employeeDetailService.isEmployeeInEmployeeDetail(employeeDetailService.findByEmail(orderWorkshopData.getEmployeeDetailData().getEmail()))) {
            throw new EmailAlreadyExistException("You already have account. Pls login and create order");
        }
        EmployeeDetail employeeDetail = new EmployeeDetail();
        employeeDetail.setFirstName(orderWorkshopData.getEmployeeDetailData().getFirstName());
        employeeDetail.setLastName(orderWorkshopData.getEmployeeDetailData().getLastName());
        employeeDetail.setEmail(orderWorkshopData.getEmployeeDetailData().getEmail());
        employeeDetail.setPhoneNumber(orderWorkshopData.getEmployeeDetailData().getPhoneNumber());

        CarData carData = new CarData();
        carData.setMake(orderWorkshopData.getCarData().getMake());
        carData.setModel(orderWorkshopData.getCarData().getModel());
        carData.setYear(orderWorkshopData.getCarData().getYear());
        carData.setEngineType(orderWorkshopData.getCarData().getEngineType());
        carData.setRegistrationNumber(orderWorkshopData.getCarData().getRegistrationNumber());
        carData.setVin(orderWorkshopData.getCarData().getVin());

        carService.saveForOrder(carData);

        Order order = new Order();
        order.setDescription(orderWorkshopData.getDescription());
        order.setCreationDate(orderWorkshopData.getCreationDate());
        order.setEmployeeDetail(employeeDetail);
        order.setCity(cityService.findByCityName(orderWorkshopData.getCityName()));
        order.setCar(carService.findCarByVinAndRegistrationNumberAndEmployeesIsNull(carData.getVin(), carData.getRegistrationNumber()));

        if(employeeDetailService.findByEmail(orderWorkshopData.getEmployeeDetailData().getEmail()) != null) {
            notNew = true;
        }

        employeeDetail.setOrders(Arrays.asList(order));
        employeeDetailService.save(employeeDetail);
        orderRepository.save(order);

        if(notNew) {
            employeeDetailService.sendNotNewEmployeeDetailEmailVerificationEmail(employeeDetail);
        } else {
            employeeDetailService.sendEmployeeDetailEmailVerificationEmail(employeeDetail);
        }
        for(Workshop workshop : workshopService.findAllByCity(order.getCity())) {
            OrderAnswer tempOrderAnswer = new OrderAnswer();
            tempOrderAnswer.setStan(Stan.UNREGISTERED);
            tempOrderAnswer.setWorkshop(workshop);
            tempOrderAnswer.setOrder(order);
            orderAnswerService.save(tempOrderAnswer);
        }
    }

    @Override
    public List<OrderEmployeeData> getOrderEmployeeDataListByUsernameAndStanIsNotCompleted(String username) {
        return getOrderEmployeeDataListByUsernameAndStan(username, false);
    }

    @Override
    public OrderEmployeeData getOrderEmployeeDataByOrderAndStanEqualsWorkshopAnswer(Order order) throws NullOrderAnswerForOrderException {
        if(!isOrderAnswerInOrder(order)) {
            throw new NullOrderAnswerForOrderException("Żaden warsztat nie zaproponował oferty");
        }
        OrderEmployeeData orderEmployeeData = getOrderEmployeeDataByOrder(order);
        List<OrderAnswerData> orderAnswerDataList = new ArrayList<>();
        for(OrderAnswerData orderAnswerData : orderEmployeeData.getOrderAnswerDataList()){
            if(orderAnswerData.getStan() == Stan.WORKSHOP_ANSWER) {
                orderAnswerDataList.add(orderAnswerData);
            }
        }
        orderEmployeeData.setOrderAnswerDataList(orderAnswerDataList);
        return orderEmployeeData;
    }

    @Override
    public List<OrderEmployeeData> getOrderEmployeeDataListByUsernameAndStanEqualsCompleted(String username) {
        return getOrderEmployeeDataListByUsernameAndStan(username, true);
    }

    private List<OrderEmployeeData>
    getOrderEmployeeDataListByUsernameAndStan(String username, boolean ifOrderCompleted) {
        EmployeeDetail employeeDetail =
                employeeDetailService.findByEmployeeUsername(username);
        List<Order> orders = orderRepository.findAllByEmployeeDetail(employeeDetail);
        List<OrderEmployeeData> orderEmployeeDataList = new ArrayList<>();
        for(Order order : orders) {
            if(!(ifOrderCompleted ^ checkIfOrderCompleted(order))) {
                orderEmployeeDataList.add(getOrderEmployeeDataByOrder(order));
            }
        }
        return orderEmployeeDataList;
    }

    private boolean checkIfOrderCompleted(Order order) {
        if(!isOrderAnswerInOrder(order)){
            return false;
        }
        for(OrderAnswer orderAnswer : order.getOrderAnswers()) {
            if(orderAnswer.getStan() == Stan.COMPLETED){
                return true;
            }
        }
        return false;
    }

    @Override
    public OrderEmployeeData getOrderEmployeeDataByOrderId(Integer id) {
        OrderEmployeeData orderEmployeeData = new OrderEmployeeData();
        Order order = findOrderById(id);
        OrderAnswer orderAnswer = order.getOrderAnswers().get(0);
        orderEmployeeData.setDescription(order.getDescription());
        orderEmployeeData.setCreationDate(order.getCreationDate());
        orderEmployeeData.setCityName(order.getCity().getCityName());
        orderEmployeeData.setOrderId(id);
        orderEmployeeData.setOrderAnswerDataList(Arrays.asList(orderAnswerService.getOrderAnswerData(orderAnswer)));
        orderEmployeeData.setCarData(carService.getCarData(order.getCar()));

        return orderEmployeeData;
    }

    @Override
    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCreated(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.CREATED);
    }

    @Override
    public List<OrderWorkshopData>
    getOrderWorkshopDataListByUsernameAndStanEqualsImplementation(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.IMPLEMENTATION);
    }

    @Override
    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCompleted(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.COMPLETED);
    }

    private List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStan(String username, Stan stan) {
        boolean isUnregistered = false;
        List<OrderAnswer> orderAnswers = orderAnswerService.findAllByWorkshopUsername(username);
        List<OrderWorkshopData> orderWorkshopDataList = new ArrayList<>();
        OrderWorkshopData orderWorkshopData;
        for(OrderAnswer orderAnswer : orderAnswers) {
            if(stan == Stan.CREATED && orderAnswer.getStan() == Stan.UNREGISTERED) {
                isUnregistered = true;
            }
            if(isEmployeeDetailInOrder(orderAnswer.getOrder())) {
                if (orderAnswer.getStan() == stan || (isUnregistered && orderAnswer.getOrder().getEmployeeDetail().isAccountVerified())) {
                    orderWorkshopData = new OrderWorkshopData();
                    orderWorkshopData.setOrderAnswerId(orderAnswer.getId());
                    orderWorkshopData.setDescription(orderAnswer.getOrder().getDescription());
                    orderWorkshopData.setCreationDate(orderAnswer.getOrder().getCreationDate());
                    orderWorkshopData.setCityName(orderAnswer.getOrder().getCity().getCityName());
                    orderWorkshopData.setCarData(carService.getCarData(orderAnswer.getOrder().getCar()));
                    orderWorkshopData.setEmployeeDetailData(employeeDetailService
                            .getEmployeeDetailData(orderAnswer.getOrder().getEmployeeDetail()));
                    orderWorkshopData.setOrderAnswerData(orderAnswerService.getOrderAnswerData(orderAnswer));

                    orderWorkshopDataList.add(orderWorkshopData);
                }
            }
            else {
                if (orderAnswer.getStan() == stan || isUnregistered) {
                    orderWorkshopData = new OrderWorkshopData();
                    orderWorkshopData.setOrderAnswerId(orderAnswer.getId());
                    orderWorkshopData.setDescription(orderAnswer.getOrder().getDescription());
                    orderWorkshopData.setCreationDate(orderAnswer.getOrder().getCreationDate());
                    orderWorkshopData.setCityName(orderAnswer.getOrder().getCity().getCityName());
                    orderWorkshopData.setCarData(carService.getCarData(orderAnswer.getOrder().getCar()));
                    orderWorkshopData.setOrderAnswerData(orderAnswerService.getOrderAnswerData(orderAnswer));

                    orderWorkshopDataList.add(orderWorkshopData);
                }
            }
            isUnregistered = false;
        }
        return orderWorkshopDataList;
    }

    @Override
    public OrderWorkshopData getOrderWorkshopDataByOrderAnswerId(Integer id) {
        OrderWorkshopData orderWorkshopData = new OrderWorkshopData();
        OrderAnswer orderAnswer = orderAnswerService.findById(id);
        Order order = orderAnswer.getOrder();
        orderWorkshopData.setDescription(order.getDescription());
        orderWorkshopData.setCreationDate(order.getCreationDate());
        orderWorkshopData.setOrderAnswerId(id);
        orderWorkshopData.setCityName(order.getCity().getCityName());
        orderWorkshopData.setEmployeeDetailData(employeeDetailService.getEmployeeDetailData(order.getEmployeeDetail()));
        orderWorkshopData.setCarData(carService.getCarData(order.getCar()));
        orderWorkshopData.setOrderAnswerData(orderAnswerService.getOrderAnswerData(orderAnswer));
        return orderWorkshopData;
    }

    @Override
    public Order findOrderById(Integer id) {
        try {
            return orderRepository.findOrderById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void deleteOrderFromEmployeeByOrderAndEmployeeUsername(Order order, String username) {

        //jeżeli wszystkie warsztaty usuną swoje odpowiedzi to liczba OrderAnswer będzie równa null oraz jest to możliwe tylko w przypadku 'CREATED' oraz 'WORKSHOP_ANSWER'
        // nie będzie przypisanych warsztatów i takie zlecenie usuwa się z bazy danych
        if(!isOrderAnswerInOrder(order)) {
        carService.deleteByOrder(order);
        orderRepository.deleteOrderById(order.getId());
        } else if(order.getOrderAnswers().size() == 0) {
            carService.deleteByOrder(order);
            orderRepository.deleteOrderById(order.getId());
        }

        else {
            //Jeżeli stan zlecenia jest 'IMPLEMENTATION' lub 'CREATED' on ma jeden przypisany objekt OrderAnswer
            if (order.getOrderAnswers().size() == 1
                    && order.getOrderAnswers().get(0).getStan() != Stan.CREATED && order.getOrderAnswers().get(0).getStan() != Stan.WORKSHOP_ANSWER) {
                switch (order.getOrderAnswers().get(0).getStan()) {
                    case IMPLEMENTATION:
                        //admin może usunąć warsztat, chociaż on ma zlecenie w stanie 'IMPLEMENTATION'
                        //sprawdzenie czy zlecenie nie zostało w taki sposób usunięte
                        if (!orderAnswerService.isWorkshopInOrderAnswer(order.getOrderAnswers().get(0))) {
                            carService.deleteByOrder(order);
                            orderAnswerService.delete(order.getOrderAnswers().get(0));
                            orderRepository.deleteOrderById(order.getId());
                        }
                        //jeżeli jest przypisany warsztat, wysyłamy na jego adres elektroniczny informację o usunięcu konta klienta
                        //zerujemy relację Zlecenie-Klient
                        else {
                            sendInformationAboutAccountDeletingEmail(order, order.getEmployeeDetail());
                            order.setEmployeeDetail(null);
                            orderRepository.save(order);
                        }
                        break;
                    case COMPLETED:
                        //jeżeli nie ma przypisanego warsztatu usuwamy zlecenie z bazy danych
                        if (!orderAnswerService.isWorkshopInOrderAnswer(order.getOrderAnswers().get(0))) {
                            carService.deleteByOrder(order);
                            orderAnswerService.delete(order.getOrderAnswers().get(0));
                            orderRepository.deleteOrderById(order.getId());
                        }
                        //jeżeli jest przypisany warsztat, zeruje relacje Klient-Zlecenie
                        else {
                            order.setEmployeeDetail(null);
                            orderRepository.save(order);
                        }
                        break;
                }

            }
            //jeżeli zlecenie ma więcej jednego przypisanego OrderAnser stan równy 'CREATED' lub 'WORKSHOP_ANSWER' lub 'UNREGISTERED'
            //jeżeli wszystkie warsztaty usuną swoje odpowiedzi to nie będzie przypisanych warsztatów i takie zlecenie usuwa się z bazy danych
            else {
                //jezeli zlecenie ma stan 'UNREGISTERED' to wszystkie OrderAnswer mają taki stan
                if (order.getOrderAnswers().get(0).getStan() == Stan.UNREGISTERED) {
                    order.setEmployeeDetail(null);
                    orderRepository.save(order);
                }
                //zostały zlecenie o stanie równym 'CREATED' lub 'WORKSHOP_ANSWER'. Oni są usuwane z bazy danych
                else {
                    carService.deleteByOrder(order);
                    for (OrderAnswer orderAnswer : order.getOrderAnswers()) {
                        orderAnswerService.delete(orderAnswer);
                    }
                    orderRepository.deleteOrderById(order.getId());
                }
            }
        }
    }

    private void sendInformationAboutAccountDeletingEmail(Order order, EmployeeDetail employeeDetail) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { informationAboutAccountDeletingEmail(order, employeeDetail);

            }
        });
    }

    private void informationAboutAccountDeletingEmail(Order order, EmployeeDetail employeeDetail) {
        InformationAboutAccountDeletingEmailContext emailContext = new InformationAboutAccountDeletingEmailContext();
        emailContext.init(order, employeeDetail);
        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCarInOrder(Order order) {
        try {
            return order.getCar() != null ? true : false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean isOrderAnswerInOrder(Order order) {
        try {
            return order.getOrderAnswers() != null ? true : false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean isEmployeeDetailInOrder(Order order) {
        try {
            return order.getEmployeeDetail() != null ? true : false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
