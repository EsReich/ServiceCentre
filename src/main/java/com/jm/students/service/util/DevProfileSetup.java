package com.jm.students.service.util;

import com.jm.students.model.*;
import com.jm.students.model.organization.AbstractOrganization;
import com.jm.students.model.organization.ClientOrganization;
import com.jm.students.model.organization.ServiceCenterOrganization;
import com.jm.students.service.AbstractOrganizationService;
import com.jm.students.service.ServiceRequestService;
import com.jm.students.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Для инициализации БД тестовыми данными необходимо в application.properties
 * проставить свойства spring.jpa.hibernate.ddl-auto=create
 * и spring.profiles.active=dev
 */
@ConditionalOnExpression("'${spring.jpa.hibernate.ddl-auto}'.equals('create')")
//@ConditionalOnProperty(prefix = "spring.jpa.hibernate", value = "ddl-auto", havingValue = "create")
@Profile("dev")
@Component
public class DevProfileSetup {

    private final UserService userService;
    private final ServiceRequestService serviceRequestService;
    private final AbstractOrganizationService organizationService;

    @Autowired
    public DevProfileSetup(UserService userService
            , ServiceRequestService serviceRequestService
            , AbstractOrganizationService organizationService) {

        this.userService = userService;
        this.serviceRequestService = serviceRequestService;
        this.organizationService = organizationService;
    }

    @PostConstruct
    public void initDatabase() {

        AbstractOrganization serviceCenterOrganization =
                saveOrganization(new ServiceCenterOrganization(), "ServiceCenterOrganization");

        AbstractOrganization clientOrganization =
                saveOrganization(new ClientOrganization(), "ClientOrganization");


        saveUser("directorName", "directorLastName"
                , "director","director@mail.ru", serviceCenterOrganization, Role.DIRECTOR);

        saveUser("managerName", "managerLastName"
                , "manager","manager@mail.ru", serviceCenterOrganization, Role.MANAGER);

        User engineer = saveUser("engineerName", "engineerLastName"
                , "engineer","engineer@mail.ru", serviceCenterOrganization, Role.ENGINEER);
//        User engineer = new User();
//        engineer.setFirstName("engineerName");
//        engineer.setLastName("engineerLastName");
//        engineer.setPassword("engineer");
//        engineer.setEmail("engineer@mail.ru");
//        engineer.setOrganization(serviceCenterOrganization);
//        engineer.setRole(Role.ENGINEER);
//
//        ServiceRequest request = new ServiceRequest();
//        request.setVehicleNumber("vehicleNumber12");
//        request.setDateOfCreate(LocalDate.now());
//        request.setRequestType(RequestType.REQUEST_TYPE_2);
//        request.setStatusRequestType(StatusRequestType.NEW);
//        request.setProblem("problem1");
//        request.setCustomer(null);
//        request.setOrders(null);
//        request.addEngineer(engineer);
//
//        engineer.addNewServiceRequest(request);
//
//        userService.save(engineer);
//        serviceRequestService.save(request);
        //====================================

        User clientDirector = saveUser("clientDirectorName", "clientDirectorLastName"
                , "clientDirector","client.director@mail.ru", clientOrganization, Role.CLIENT_DIRECTOR);

        User clientEmployee = saveUser("clientEmployeeName", "clientEmployeeLastName"
                , "clientEmployee","client.employee@mail.ru", clientOrganization, Role.CLIENT_EMPLOYEE);


        saveServiceRequest("vehicleNumber1", LocalDate.now()
                , RequestType.REQUEST_TYPE_1, StatusRequestType.NEW
                , "problem1", clientDirector, engineer);

        saveServiceRequest("vehicleNumber2", LocalDate.now()
                , RequestType.REQUEST_TYPE_2, StatusRequestType.IN_WORK
                , "problem2", clientEmployee, engineer);
    }

    private AbstractOrganization saveOrganization(AbstractOrganization organization, String name) {

        organization.setName(name);

        organizationService.save(organization);
        return organization;
    }

    private User saveUser(String firstName, String lastName
            , String password, String email, AbstractOrganization organization, Role role) {

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.setOrganization(organization);
        user.setRole(role);

        userService.save(user);
        return user;
    }

    private void saveServiceRequest(String vehicleNumber
            , LocalDate date, RequestType requestType, StatusRequestType statusRequestType
            , String problem, User customer, User... engineers) {

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setVehicleNumber(vehicleNumber);
        serviceRequest.setDateOfCreate(date);
        serviceRequest.setRequestType(requestType);
        serviceRequest.setStatusRequestType(statusRequestType);
        serviceRequest.setProblem(problem);
        serviceRequest.setCustomer(customer);

        Arrays.stream(engineers).forEach(serviceRequest::addEngineer);

        serviceRequestService.save(serviceRequest);
    }
}
