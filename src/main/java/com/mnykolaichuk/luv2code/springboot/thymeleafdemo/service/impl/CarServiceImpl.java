package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.CarRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.MyCarAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.CarVerificationEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.*;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CarData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private EmployeeCarService employeeCarService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CarModelService carModelService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Override
    public Car findById(Integer id) {
        try {
            return carRepository.findCarById(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public CarData getCarData(Car car) {
        CarData carData = new CarData();
        BeanUtils.copyProperties(car,carData);
        carData.setMake(car.getCarModel().getCarMake().getMake());
        carData.setModel(car.getCarModel().getModel());
        carData.setYear(car.getYear().toString());
        return carData;
    }

    @Override
    public List<CarData> getCarDataListForEmployeeUsername(String username) {
        List<CarData> carDataList = new ArrayList<>();
        if(employeeService.isCarInEmployee(employeeService.findByUsername(username))) {
            for (Car car : employeeService.findByUsername(username).getCars()) {
                carDataList.add(getCarData(car));
            }
            return carDataList;
        }
        return null;
    }

    @Override
    public void save(CarData carData) throws MyCarAlreadyExistException {
        List<Employee> employees = new ArrayList<>();
        //zapisuje id klienta aktualnie dodającego samochód
        Integer currentUserId = employeeService.findByUsername(carData.getUsername()).getId();
        //ustawia boolean flagę czy istnieje w BD dodawany samochód, z już przypisamym klientem.
        boolean isCarExist = checkIfVinAndRegistrationNumberExistEmployeeIsNotNull(carData.getVin(), carData.getRegistrationNumber());

        //liczba przypisanych do samochodu klientów
        int count = 1;

        if(isCarExist){
            //zwraca samochód z przypisanym klientem oraz podanym numerem VIN, numerem rejestracyjnym z bazy danych
            Car car = findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(carData.getVin(), carData.getRegistrationNumber());
            //wszystkie Entity objekty z polami id samochodu, id klienta zawierające id aktualnie dodawanego samochodu
            for(EmployeeCar employeeCar : employeeCarService.findAllByCarId(car.getId())) {
                //jeżeli aktualnie zalogowany klient już jest przypisany do samochodu
                if(employeeCar.getEmployeeId() == currentUserId) {
                    //rzuca wyjątek że samochód już jest w liście samochodów klienta
                    throw new MyCarAlreadyExistException("This car already exist in my cars list");
                }
                //jeżeli samochód nie ma przypisanego aktualnie zalogowanego klienta
                else {
                    //dodaje do listy wszystrkich przypisanych do samochodu klientów
                    employees.add(employeeService.findById(employeeCar.getEmployeeId()));
                    //liczniek ilości przypisanych klientów
                    count++;
                }
            }
            //tworzy Entity object służący do związku @ManyToMany Klient-Samochód
            // @param currentUserId id aktualnie zalogowanego klienta
            //@param carId id samochodu
            //@param carVerified liczba przypisanych do samochodu klientów. Kiedy liczba będzie równa '1' samochód będzie dostępny dla
            //  aktualnie zalogowanego klienta
            EmployeeCar employeeCar = new EmployeeCar(currentUserId, car.getId(), count);

            for(Employee employee : employees){
                //wysylanie do wszystkich klientów przypisanych do samochody pisem z informacją o dodaniu ich samochodu do listy
                // własnych samochodów przez klienta oraz możliwością weryfikacji tego dodania
                sendCarConfirmationEmail(carData.getUsername(), employee.getEmployeeDetail(), car);
            }
            //zapisanie do bazy danych nowego przypisanego do samochodu klienta
            employeeCarService.save(employeeCar);
        }

        //jeżeli samochód nie isnieje w bazie danych
        if(!isCarExist) {
            //Entity object model samochodu z referencją na producenta samochodu
            CarModel carModel = carModelService.findByMakeAndModel(carData.getMake(), carData.getModel());
            Car car = new Car();
            //kopiowanie wszystkich danych z formy do Entity objektu
            BeanUtils.copyProperties(carData, car);
            //przypisanie producenta oraz modeli samochodu
            car.setCarModel(carModel);
            car.setYear(Year.of(Integer.parseInt(carData.getYear())));
            //przypisanie do samochodu aktualnie zalogowanego klienta
            car.setEmployees(Arrays.asList(employeeService.findByUsername(carData.getUsername())));

            //zapisanie samochodu do bazy danych
            carRepository.save(car);
            //zapisanie do bazy danych związku Klient-Samochód
            EmployeeCar employeeCar;
            employeeCar = employeeCarService.findByEmployeeIdAndCarId
                    (currentUserId,
                            findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(car.getVin(), car.getRegistrationNumber())
                            .getId());
            employeeCar.setCarVerified(1);
            employeeCarService.save(employeeCar);
        }
    }

    /**
     * Wysyłanie pisem na pocztę elektroniczną wykonuje się w nowym wątku.
     *
     * @param fromEmployee username aktualnie zalogowanego klienta
     * @param toEmployeeDetail Entity objekt klienta do którego będzie wysłane pismo
     * @param car Entity object samochód
     */
    private void sendCarConfirmationEmail(String fromEmployee, EmployeeDetail toEmployeeDetail, Car car) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { carConfirmationEmail(fromEmployee, toEmployeeDetail, car);

            }
        });
    }

    private void carConfirmationEmail(String fromEmployee, EmployeeDetail toEmployeeDetail, Car car) {
        SecureToken secureToken = secureTokenService.createSecureTokenForCar(car, fromEmployee);
        secureToken.setEmployeeDetail(toEmployeeDetail);
        secureTokenService.saveSecureToken(secureToken);
        CarVerificationEmailContext emailContext = new CarVerificationEmailContext();
        emailContext.init(toEmployeeDetail, car);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Car saveForOrder(CarData carData) {
        //ustawia boolean flagę czy istnieje w BD dodawany samochód, z już przypisamym zleceniem
        boolean isCarExist = checkIfVinAndRegistrationNumberExistEmployeeIsNull(carData.getVin(), carData.getRegistrationNumber());
        //jeżeli takie samochody są w bazie danych
        if(isCarExist){
            //zwraca samochód z przypisanym zleceniem oraz podanym numerem VIN, numerem rejestracyjnym z bazy danych
            Car car = findCarByVinAndRegistrationNumberAndEmployeesIsNull(carData.getVin(), carData.getRegistrationNumber());
            //ponieważ samochód już jest w barzie danych, zwracamy Entity objekt samochodu
            return car;
        }
        //tworzony nowy samochód
        CarModel carModel = carModelService.findByMakeAndModel(carData.getMake(), carData.getModel());
        Car car = new Car();
        car.setCarModel(carModel);
        BeanUtils.copyProperties(carData, car);
        try {
            car.setYear(Year.of(Integer.parseInt(carData.getYear())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carRepository.save(car);

    }

    private boolean checkIfVinAndRegistrationNumberExistEmployeeIsNotNull(String vin, String registrationNumber) {
        return carRepository.findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(vin, registrationNumber) != null ? true : false;
    }

    private boolean checkIfVinAndRegistrationNumberExistEmployeeIsNull(String vin, String registrationNumber) {
        return carRepository.findCarByVinAndRegistrationNumberAndEmployeesIsNull(vin, registrationNumber) != null ? true : false;
    }

    @Override
    public Car findCarByVinAndRegistrationNumberAndEmployeesIsNull(String vin, String registrationNumber) {
        try {
            return carRepository.findCarByVinAndRegistrationNumberAndEmployeesIsNull(vin, registrationNumber);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Car findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(String vin, String registrationNumber) {
        try {
            return carRepository
                    .findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(vin, registrationNumber);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<Car> findAllCarsByUsername(String username) {
        List<Car> cars = new ArrayList<>();
        List<EmployeeCar> employeeCars = employeeCarService.findAllByEmployeeId(employeeService.findByUsername(username).getId());
        for(EmployeeCar employeeCar : employeeCars) {
            if(employeeCar.getCarVerified() == 1) {
                cars.add(carRepository.findCarById(employeeCar.getCarId()));
            }
        }
        return cars;
    }

    @Override
    public boolean verifyCar(String token) throws InvalidTokenException {
        //Zwraca z bazy danych Entity objekt token
        SecureToken secureToken = secureTokenService.findByToken(token);
        //jeżeli token jest równy null albo nie jest równy do żądanego tokiena albo nie jest aktualny
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            //rzyca wyjątek o nie poprawności tokiena
            throw new InvalidTokenException("Token is not valid");
        }
        //zwraca Entity objekt klienta do którego było wysłane pismo na podstawie zapisanych do tokiena danych klienta
        Employee employee = employeeService.findById(secureToken.getEmployeeDetail().getEmployee().getId());
        //jeżeli takiego klienta nie istnieje zwraca false
        if(Objects.isNull(employee)){
            return false;
        }
        //zwraca id samochodu na podstawie zapisanych do tokiena danych
        Integer carId = secureToken.getVerificationCarId();
        //zwraca username klienta chcącego dodać samochód do swojej listy
        String username = secureToken.getVerificationUsername();
        //zwraca Entity objekt odpowiadający za relację ManyToMany Klient-Samochód, zawierający pole weryfikacji samochodu
        EmployeeCar employeeCar = employeeCarService.findByEmployeeIdAndCarId(employeeService.findByUsername(username).getId(), carId);
        //kiedy wszystkie już przypisane i zweryfikowane do samochodu klienty zweryfikują dodanie samochodu przez nowego klienta,
        //liczba w polu carVerified będzie równa '1' i samochód będzie dostępny do korzystania przez nowego klienta
        employeeCar.setCarVerified(employeeCar.getCarVerified() - 1);
        employeeCarService.save(employeeCar);

        // usuwa wykorzystany tokien z bazy danych
        secureTokenService.removeToken(secureToken);
        return true;
    }

    @Override
    public void deleteByCarAndEmployee(Car car, Employee sourceEmployee) {
        //jeżeli samochód ma przypisanego jednego klienta, usuwa z bazy danych
        if(isEmployeeInCar(car)) {
            if (car.getEmployees().size() == 1) {
                carRepository.deleteCarById(car.getId());
            }
            //jeżeli samochód ma listę przypisanych klientów, usuwa relację Samochód-sourceEmployee
            else {
                for (EmployeeCar employeeCar : employeeCarService.findAllByCarId(car.getId())) {
                    if (employeeCar.getEmployeeId() == sourceEmployee.getId()) {
                        employeeCarService.delete(employeeCar);
                    }
                }
            }
        }
    }

    @Override
    public void deleteByOrder(Order sourceOrder) {
        //jeżeli samochód ma przypisane jedno zlecenie, usuwa z bazy danych
        if(orderService.isCarInOrder(sourceOrder)) {
            Car car = sourceOrder.getCar();
            if (car.getOrders().size() == 1) {
                sourceOrder.setCar(null);
                orderService.save(sourceOrder);
                carRepository.deleteCarById(car.getId());
            }
            //jeżeli samochód ma listę przypisanych zleceń, usuwa relację Samochód-sourceEmployee
            else {
                for (Order order : car.getOrders()) {
                    if (order.getId() == sourceOrder.getId()) {
                        order.setCar(null);
                        orderService.save(order);
                    }
                }
            }
        }
    }

    @Override
    public boolean isEmployeeInCar(Car car) {
        try {
            return car.getEmployees() != null ? true : false;
        } catch (NullPointerException e) {
            return false;
        }
    }

}
