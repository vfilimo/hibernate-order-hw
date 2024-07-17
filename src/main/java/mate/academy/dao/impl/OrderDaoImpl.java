package mate.academy.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import mate.academy.dao.OrderDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Order;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;

import java.util.List;

@Dao
public class OrderDaoImpl implements OrderDao {
    @Override
    public Order add(Order order) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(order);
            transaction.commit();
            return order;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't add order: " + order, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Order> getHistoryByUser(User user) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Order o " +
                    "LEFT JOIN FETCH o.tickets t " +
                    "LEFT JOIN FETCH o.user " +
                    "LEFT JOIN FETCH t.movieSession ms " +
                    "LEFT JOIN FETCH ms.movie " +
                    "LEFT JOIN FETCH ms.cinemaHall " +
                    "WHERE o.user = :user";
            Query<Order> query = session.createQuery(hql, Order.class);
            query.setParameter("user", user);
            return query.getResultList();
    } catch (Exception e) {
            throw new DataProcessingException("Can't get any order by user: " + user, e);
        }
    }
}
