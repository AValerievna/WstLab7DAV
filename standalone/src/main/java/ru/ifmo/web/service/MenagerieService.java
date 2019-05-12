package ru.ifmo.web.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.ifmo.web.database.dao.MenagerieDAO;
import ru.ifmo.web.database.entity.Menagerie;
import ru.ifmo.web.service.exception.MenagerieServiceException;
import ru.ifmo.web.service.exception.MenagerieServiceFault;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebService(serviceName = "menagerie", targetNamespace = "menagerie_namespace")
@AllArgsConstructor
@NoArgsConstructor
public class MenagerieService {
    private MenagerieDAO menagerieDAO;

    @WebMethod
    public List<Menagerie> findAll() throws MenagerieServiceException {
        try {
            return menagerieDAO.findAll();
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new MenagerieServiceException(message, e, new MenagerieServiceFault(message));
        }
    }

    @WebMethod
    public List<Menagerie> findWithFilters(@WebParam(name = "id") Long id, @WebParam(name = "animal") String animal,
                                           @WebParam(name = "name") String name, @WebParam(name = "breed") String breed,
                                           @WebParam(name = "health") String health, @WebParam(name = "arrival") Date arrival) throws MenagerieServiceException {
        try {
            return menagerieDAO.findWithFilters(id, animal, name, breed, health, arrival);
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new MenagerieServiceException(message, e, new MenagerieServiceFault(message));
        }
    }

    @WebMethod
    public int update(@WebParam(name = "id") Long id, @WebParam(name = "animal") String animal,
                      @WebParam(name = "name") String name, @WebParam(name = "breed") String breed,
                      @WebParam(name = "health") String health, @WebParam(name = "arrival") Date arrival) throws MenagerieServiceException {
        try {
            int count = menagerieDAO.update(id, animal, name, breed, health, arrival);
            if (count == 0) {
                throw new MenagerieServiceException("Запись с id=" + id + " не существует", new MenagerieServiceFault("Запись с id=" + id + " не существует"));
            }
            return count;
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new MenagerieServiceException(message, e, new MenagerieServiceFault(message));
        }
    }

    @WebMethod
    public int delete(@WebParam(name = "id") Long id) throws MenagerieServiceException {
        try {
            int count = menagerieDAO.delete(id);
            if (count == 0) {
                throw new MenagerieServiceException("Запись с id=" + id + " не существует", new MenagerieServiceFault("Запись с id=" + id + " не существует"));
            }
            return count;
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new MenagerieServiceException(message, e, new MenagerieServiceFault(message));
        }
    }

    @WebMethod
    public Long create(@WebParam(name = "animal") String animal,
                       @WebParam(name = "name") String name, @WebParam(name = "breed") String breed,
                       @WebParam(name = "health") String health, @WebParam(name = "arrival") Date arrival) throws MenagerieServiceException {
        try {
            return menagerieDAO.create(animal, name, breed, health, arrival);
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new MenagerieServiceException(message, e, new MenagerieServiceFault(message));
        }
    }

    @WebMethod
    public Long createWithObject(@WebParam(name = "menagerie") MenagerieRequestObject menagerie) throws MenagerieServiceException {
        try {
            return menagerieDAO.create(menagerie.getAnimal(), menagerie.getName(), menagerie.getBreed(), menagerie.getHealth(), menagerie.getArrival());
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new MenagerieServiceException(message, e, new MenagerieServiceFault(message));
        }
    }
}
