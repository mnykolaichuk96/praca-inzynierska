package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.CarModelRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.CantDeleteCarModelException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.CarMake;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.CarModel;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CarModelData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CarMakeService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CarModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarModelServiceImpl implements CarModelService {

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CarMakeService carMakeService;

    @Override
    public List<String> loadCarModelList(CarMake carMake) {
        List<String> carModelList = new ArrayList<>();
        for(CarModel carModel : carModelRepository.findAllByCarMakeOrderByModelAsc(carMake)) {
            carModelList.add(carModel.getModel());
        }
        return carModelList;
    }

    @Override
    public CarModel findByMakeAndModel(String make, String model) {
        Integer carMakeId = carMakeService.findByMake(make).getId();
        return carModelRepository.findCarModelByCarMakeIdAndModel(carMakeId, model);
    }

    @Override
    public List<CarModelData> getAllCarModelDataList() {
        List<CarModelData> allCarModelDataList = new ArrayList<>();
        CarModelData carModelData;
        for(CarModel carModel : carModelRepository.findAll()) {
            carModelData = new CarModelData();
            carModelData.setModelId(carModel.getId());
            carModelData.setMake(carModel.getCarMake().getMake());
            carModelData.setModel(carModel.getModel());
            if(isCarInCarModel(carModel)) {
                carModelData.setCarInSystemCount(carModel.getCars().size());
            }
            else {
                carModelData.setCarInSystemCount(0);
            }
            allCarModelDataList.add(carModelData);
        }
        return allCarModelDataList;
    }

    @Override
    public void deleteByCarModelId(Integer id) throws CantDeleteCarModelException {
        CarModel carModel = carModelRepository.findCarModelById(id);
        if(isCarInCarModel(carModel)) {
            if(carModel.getCars().size() != 0)
            throw new CantDeleteCarModelException("Nie można usunąć model samochodu dopóki w systemie są takie samochody");
        }
        CarMake carMake = carModel.getCarMake();
        if(carMake.getCarModels().size() == 1) {
            carModelRepository.deleteCarModelById(id);
            carMakeService.delete(carMake);
        }
        else {
            carModelRepository.deleteCarModelById(id);
        }
    }

    @Override
    public boolean isCarInCarModel(CarModel carModel) {
        try {
            return carModel.getCars() != null ? true : false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public void addCarModel(String make, String model) {
        if(carMakeService.findByMake(make) == null) {
            carMakeService.save(new CarMake(make));
        }
        CarModel carModel = new CarModel(model);
        carModel.setCarMake(carMakeService.findByMake(make));
        carModelRepository.save(carModel);
    }
}
