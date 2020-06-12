package beans;

import entities_jpa.Customer;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

@Remote
@Stateless
public class CustomerEJB {

    public static EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("WebLabsDB");

    public Customer validateUserLogin(String username, String password) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        Customer customer = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("GetCustomerByUsernameAndPassword");
            query.setParameter("login", username);
            query.setParameter("password", password);
            customer = (Customer) query.getSingleResult();

            System.out.println(customer);

            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
        return customer;
    }

    public void deleteOrderProduct(String productName, int orderId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("DeleteOrderProductByOrderIdAndName");
            query.setParameter("name", productName);
            query.setParameter("orderId", orderId);
            query.executeUpdate();

            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

}
