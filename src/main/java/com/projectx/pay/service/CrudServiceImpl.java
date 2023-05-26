package com.projectx.pay.service;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.RollbackException;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/***
 *
 * @author moha
 */
@Service
public class CrudServiceImpl implements CrudService {

    private static final Logger LOG = LoggerFactory.getLogger(CrudServiceImpl.class);

   @Autowired
    EntityManagerFactory entityManagerFactory;

    private SessionFactory sessionFactory;

    @PostConstruct
    protected void init() {
        LOG.info("Initializing crud service...");
        sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        LOG.info("Crud service initialized. Sessionfactory properties {}", entityManagerFactory.getProperties());
    }

    @Override
    public <T> T findEntity(Serializable primaryKey, Class<T> clazz) throws HibernateException {
        try (Session session = sessionFactory.openSession()) {
            IdentifierLoadAccess loadAccess = session.byId(clazz);
            return (T) loadAccess.load(primaryKey);
        } catch (HibernateException e) {
            LOG.error("Failed to find entity {} by id {}. {}", clazz, primaryKey, e.getMessage());
            throw e;
        }
    }

    @Override
    public <T> List<T> fetchWithNamedQuery(String queryName, Map<String, Object> params) {
        return fetchWithNamedQuery(queryName, params, 0, DEFAULT_PAGE_SIZE);
    }

    @Override
    public <T> List fetchWithHibernateQuery(String query, Map<String, Object> params) throws HibernateException {
        return fetchWithHibernateQuery(query, params, 0, DEFAULT_PAGE_SIZE);
    }

    @Override
    public <T> T fetchWithHibernateQuerySingleResult(String query, Map<String, Object> params) throws HibernateException {
        List<T> results = fetchWithHibernateQuery(query, params, 0, 1);
        return results.isEmpty() ? null : results.iterator().next();
    }

    @Override
    public <T> List fetchWithHibernateQuery(String query, Map<String, Object> params, int start, int end) throws HibernateException {
        LOG.debug("Executing Hibernate={}, start={},end={} params=[{}]", query, start, end, params);
        try (Session session = sessionFactory.openSession()) {
            Query q = session.createQuery(query);
            params.forEach((key, value) -> {
                if (value instanceof Collection) {
                    q.setParameterList(key, (Collection) value);
                } else {
                    q.setParameter(key, value);
                }
            });
            start = Math.max(start, 0);
            if (end >= start && start >= 0) {
                q.setFirstResult(start);
                q.setMaxResults(end - start);
            }
            return q.list();
        } catch (HibernateException e) {
            LOG.error("Failed in executing hibernate query {} with params [{}]. {}.", query, params, e.getMessage());
            throw e;
        }
    }

    public <T> Object fetchWithHibernateQuerySingleResult(String query, Map<String, Object> params, int start, int end) throws HibernateException {
        LOG.debug("Executing Hibernate={}, start={},end={} params=[{}]", query, start, end, params);
        try (Session session = sessionFactory.openSession()) {

            Query q = session.createQuery(query);
            params.entrySet().forEach((param) -> {
                if (param.getValue() instanceof Collection) {
                    q.setParameterList(param.getKey(), (Collection) param.getValue());
                } else {
                    q.setParameter(param.getKey(), param.getValue());
                }
            });

            return q.getFirstResult();
        } catch (HibernateException e) {
            LOG.error("Failed in executing hibernate query {} with params [{}]. {}.", query, params, e.getMessage());
            throw e;
        }
    }

    @Override
    public int executeHibernateQuery(String queryString, Map<String, Object> params) throws HibernateException {
        LOG.debug("Executing Hibernate={}, params=[{}]", queryString, params);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Query q = session.createQuery(queryString);
            params.forEach((key, value) -> {
                if (value instanceof Collection) {
                    q.setParameterList(key, (Collection) value);
                } else {
                    q.setParameter(key, value);
                }
            });

            int executeUpdate = q.executeUpdate();
            tx.commit();
            return executeUpdate;
        } catch (Exception e) {
            LOG.error("Failed in executing hibernate query {} with params [{}]. {}.", queryString, params, e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public int executeNativeQuery(String queryString, Map<String, Object> params) throws HibernateException {
        LOG.info("Polycarp", queryString);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Query q = session.createNativeQuery(queryString);
            params.forEach((key, value) -> {
                if (value instanceof Collection) {
                    q.setParameterList(key, (Collection) value);
                } else {
                    q.setParameter(key, value);
                }
            });

            int executeUpdate = q.executeUpdate();
            tx.commit();
            return executeUpdate;
        } catch (Exception e) {
            LOG.error("Failed in executing hibernate query {} with params [{}]. {}.", queryString, params, e.getMessage());
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    /**
     * @param query
     * @param start
     * @param end
     * @return
     * @throws HibernateException
     */
    @Override
    public <T> List<T> fetchWithNativeQuery(String query, Map<String, Object> params, int start, int end) throws HibernateException {
        LOG.debug("Executing nativeQuery={}, start={},end={} params=[{}]", query, start, end, params);
        try (Session session = sessionFactory.openSession()) {
            Query q = session.createNativeQuery(query);
            params.entrySet().forEach((param) -> {
                if (param.getValue() instanceof Collection) {
                    q.setParameterList(param.getKey(), (Collection) param.getValue());
                } else {
                    q.setParameter(param.getKey(), param.getValue());
                }
            });
            start = start < 0 ? 0 : start;
            if (end >= start) {
                q.setFirstResult(start);
                q.setMaxResults(end - start);
            }
            return q.list();
        } catch (HibernateException e) {
            LOG.error("Failed in executing hibernate query {} with params [{}]. {}.", query, e.getMessage());
            throw e;
        }
    }

    @Override
    public <T> List<T> fetchWithNamedQuery(String queryName, Map<String, Object> params, int start, int end) {
        LOG.debug("Executing NamedQuery={}, start={},end={} params=[{}]", queryName, start, end, params);
        try (Session session = sessionFactory.openSession()) {
            Query query = session.getNamedQuery(queryName);
            params.entrySet().forEach((param) -> {
                if (param.getValue() instanceof Collection) {
                    query.setParameterList(param.getKey(), (Collection) param.getValue());
                } else {
                    query.setParameter(param.getKey(), param.getValue());
                }
            });
            start = start < 0 ? 0 : start;
            if (end >= start && start >= 0) {
                query.setFirstResult(start);
                query.setMaxResults(end - start);
            }
            return query.list();
        } catch (HibernateException e) {
            LOG.error("Failed in executing named query {} with params [{}]. {}.", queryName, params, e.getMessage());
            throw e;
        }
    }

    @Override
    public <T> void saveOrUpdate(List<T> entities) throws HibernateException {
        LOG.debug("Perist or merge {} entities.", entities.size());
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.getTransaction();
            tx.begin();
            entities.forEach((entity) -> {
                session.saveOrUpdate(entity);
            });
            tx.commit();
        } catch (Exception e) {
            LOG.error("Failed to persist or merge {} entities. {}", entities.size(), e.getMessage(), e);
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public <T> Object save(T entity) throws HibernateException, ConstraintViolationException {
        LOG.debug("Perist or merge {}", entity);
        Transaction tx = null;
        Object savedEntity = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.getTransaction();
            tx.begin();
            savedEntity = session.save(entity);
            tx.commit();
        } catch (ConstraintViolationException e) {
            LOG.error("Constraint validation failed {}. {}", entity, e.getMessage(), e);
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } catch (RollbackException e) {
            LOG.error("RollbackException validation failed {}. {}", entity, e.getMessage(), e);
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } catch (Exception e) {
            LOG.error("Failed to persist or merge {}. {}", entity, e.getMessage(), e);
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }

        return savedEntity;
    }

    @Override
    public <T> void remove(T entity) throws HibernateException {
        LOG.debug("Deleting {}", entity);
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.getTransaction();
            tx.begin();
            session.delete(entity);
            tx.commit();
        } catch (Exception e) {
            LOG.error("Failed to delete {}. {}", entity, e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}