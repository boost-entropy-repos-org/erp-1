/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package erp.dao;

import erp.entities.Company;
import erp.entities.Role;
import erp.entities.Usr;
import erp.util.EJBUtil;
import erp.util.PersistenceHelper;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author peukianm
 */
@Stateless
public class UsrDAO implements Serializable {

    private static final Logger logger = LogManager.getLogger(UsrDAO.class);
    
    @PersistenceContext(unitName = "erp")
    private EntityManager entityManager;

// PersistenceHelper persistenceHelper = EJBUtil.lookupPersistenceHelperBean();
// EntityManager entityManager = persistenceHelper.getEntityManager();
    
    public Usr get(long id) {
        return entityManager.find(Usr.class, id);
    }

    public List<Usr> getAll() {
        Query query = entityManager.createQuery("SELECT e FROM Usr e");
        return query.getResultList();
    }

    public void save(Usr user) {
        entityManager.persist(user);   
    }

    public void update(Usr user) {
        entityManager.merge(user);
    }

    public void delete(Usr user) {
        entityManager.remove(user);
    }

    private void executeInsideTransaction(Consumer<EntityManager> action) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            action.accept(entityManager);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Usr> findByProperty(String propertyName, final Object value, final int... rowStartIdxAndCount) {

        try {
            final String queryString = "select model from Usr model where model." + propertyName + "= :propertyValue";
            System.out.println("EntityMgr=" + entityManager);
            Query query = entityManager.createQuery(queryString);
            query.setParameter("propertyValue", value);
            if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
                int rowStartIdx = Math.max(0, rowStartIdxAndCount[0]);
                if (rowStartIdx > 0) {
                    query.setFirstResult(rowStartIdx);
                }

                if (rowStartIdxAndCount.length > 1) {
                    int rowCount = Math.max(0, rowStartIdxAndCount[1]);
                    if (rowCount > 0) {
                        query.setMaxResults(rowCount);
                    }
                }
            }
            return query.getResultList();
        } catch (RuntimeException re) {
            logger.error("Error on updating entity", re);
            throw re;
        }
    }

    public Usr findUser(String username, String password) {
        try {
            String sql = "Select u from Usr u where "
                    + " u.username like '" + username + "'  "
                    + " AND u.password like '" + password + "' ";

            Query query = entityManager.createQuery(sql);
            return (Usr) query.getSingleResult();

        } catch (NoResultException | NonUniqueResultException nre) {
            return null;
        } catch (RuntimeException re) {
            throw re;

        }
    }

    public List<Usr> fetchUserAutoCompleteUsername(String name) {
        try {
            name = name.trim();

            String queryString = "Select user from Usr  user  "
                    + " where (LOWER(user.username) like '" + ((String) name).toLowerCase() + "%'"
                    + " OR UPPER(user.username)  like '" + ((String) name).toUpperCase() + "%') "
                    + " order by user.username";

            Query query = entityManager.createQuery(queryString);

            query.setMaxResults(20);
            return query.getResultList();
        } catch (RuntimeException re) {
            logger.error("Error on finding entity", re);
            throw re;
        }
    }

    public List<Usr> searchUser(Role role, Company company, String username, String surname) {
        try {

            String queryString = "Select u from Usr u "
                    + (role != null ? " JOIN u.userrole userrole " : " ")
                    + " where u.userid IS NOT NULL "
                    + (company != null ? " and u.company=:company " : " ")
                    + (role != null ? " and userrole.role=:role " : " ")
                    + (username != null ? " and (LOWER(u.username) like '" + ((String) username).toLowerCase() + "%'"
                            + " OR UPPER(u.username)  like '" + ((String) username).toUpperCase() + "%') " : " ")
                    + (surname != null ? " and (LOWER(u.surname) like '" + ((String) surname).toLowerCase() + "%'"
                            + " OR UPPER(u.surname)  like '" + ((String) surname).toUpperCase() + "%') " : " ")
                    + " order by u.username DESC";

            Query query = entityManager.createQuery(queryString);
            if (company != null) {
                query.setParameter("company", company);
            }
            if (role != null) {
                query.setParameter("role", role);
            }

            return query.getResultList();
        } catch (RuntimeException re) {
            throw re;
        }
    }

}