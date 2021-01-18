package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.AuthorityRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.WorkshopRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.WrapperString;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.SecureToken;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.WorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.AuthorityEnum;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.AbstractService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CityService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.SecureTokenService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.WorkshopService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WorkshopServiceImpl extends AbstractService implements WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private CityService cityService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SecureTokenService secureTokenService;

    @Override
    public void register(WorkshopData workshopData) throws UserAlreadyExistException, EmailAlreadyExistException {
        if(checkIfUsernameExist(workshopData.getUsername())) {
            workshopData.setUsername(null);
            throw new UserAlreadyExistException("User already exist");
        }
        if(checkIfEmailExist(workshopData.getEmail())) {
            workshopData.setEmail(null);
            throw new EmailAlreadyExistException("Email address already used");
        }
        Workshop workshop = new Workshop();
        BeanUtils.copyProperties(workshopData, workshop);
        encodePassword(workshopData, workshop);
        workshop.setAuthorities(Stream.of
                (authorityRepository.findByAuthority
                (AuthorityEnum.ROLE_WORKSHOP)).collect(Collectors.toSet()));
        workshop.setCity(cityService.findByCityName(workshopData.getCityName()));
        workshopRepository.save(workshop);
        sendRegistrationConfirmationEmail(workshop);

    }

    private void encodePassword(WorkshopData source, Workshop target) {
        target.setPassword(passwordEncoder.encode(source.getPassword()));
    }

    @Override
    public boolean verifyWorkshop(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            throw new InvalidTokenException("Token is not valid");
        }
        Workshop workshop = workshopRepository.getOne(secureToken.getWorkshop().getId());
        if(Objects.isNull(workshop)){
            return false;
        }
        workshop.setAccountVerified(true);
        workshopRepository.save(workshop); // let's same user details

        // we don't need invalid password now
        secureTokenService.removeToken(secureToken);
        return true;
    }

    @Override
    public void update(WorkshopData workshopData, WrapperString wrapperString) throws UserAlreadyExistException, EmailAlreadyExistException {
        boolean isEmailChange = false;
        if (!workshopData.getUsername().equals(wrapperString.getOldUsername())) {
            if (checkIfUsernameExist(workshopData.getUsername())) {
                workshopData.setUsername(null);
                throw new UserAlreadyExistException("Workshop already exist");
            }
        }
        if (!workshopData.getEmail().equals(wrapperString.getOldEmail())) {
            isEmailChange = true;
            if (checkIfEmailExist(workshopData.getEmail())) {
                workshopData.setEmail(null);
                throw new EmailAlreadyExistException("Email address already used");
            }
        }

        Workshop workshop = new Workshop();
        workshop.setId(workshopRepository.findWorkshopByUsername(wrapperString.getOldUsername()).getId());
        BeanUtils.copyProperties(workshopData, workshop);
        workshop.setCity(cityService.findByCityName(workshopData.getCityName()));
        if (isEmailChange) {
            workshop.setAccountVerified(false);
            workshopRepository.save(workshop);
            sendRegistrationConfirmationEmail(workshop);
        } else {
            workshopRepository.save(workshop);
        }
    }

    @Override
    public WorkshopData getWorkshopDataByUsername(String username) {
        WorkshopData workshopData = new WorkshopData();
        Workshop workshop = workshopRepository.findWorkshopByUsername(username);
        BeanUtils.copyProperties(workshop, workshopData);
        workshopData.setMatchingPassword(workshop.getPassword());

        return workshopData;
    }

    @Override
    public void deleteById(int id) {
        workshopRepository.deleteById(id);
    }

    @Override
    public Workshop findByUsername(String username) {
        return workshopRepository.findWorkshopByUsername(username);
    }

    @Override
    public List<Workshop> findAllByCity(City city) {
        return workshopRepository.findAllByCity(city);
    }

    private boolean checkIfUsernameExist(String username) {
        return workshopRepository.findWorkshopByUsername(username) != null ? true : false;
    }

    private boolean checkIfEmailExist(String email) {
        return workshopRepository.findWorkshopByEmail(email) != null ? true : false;
    }
}
