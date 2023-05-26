package com.projectx.pay.service;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Definition of crud service utilities.
 *
 * @author Moha
 */
@Component
public interface CrudService {


    int DEFAULT_PAGE_SIZE = 1000000;

    <T> T findEntity(Serializable primaryKey, Class<T> clazz) throws HibernateException;


    int executeHibernateQuery(String queryString, Map<String, Object> params) throws HibernateException;


    <T> List<T> fetchWithNamedQuery(String queryName, Map<String, Object> params) throws HibernateException;

    <T> List<T> fetchWithHibernateQuery(String query, Map<String, Object> params) throws HibernateException;

    <T> T fetchWithHibernateQuerySingleResult(String query, Map<String, Object> params) throws HibernateException;

    <T> List<T> fetchWithNativeQuery(String query, Map<String, Object> params, int start, int end) throws HibernateException;

    <T> List<T> fetchWithNamedQuery(String query, Map<String, Object> params, int start, int end) throws HibernateException;

    <T> List<T> fetchWithHibernateQuery(String query, Map<String, Object> params, int start, int end) throws HibernateException;

    <T> Object save(T entity) throws HibernateException;

    <T> void saveOrUpdate(List<T> entities) throws HibernateException;


    <T> void remove(T entity) throws HibernateException;

    int executeNativeQuery(String queryString, Map<String, Object> params);

    SessionFactory getSessionFactory();
}