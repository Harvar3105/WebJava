package app.dal;

import app.entities.Order;
import app.entities.OrderRow;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Order> getAllOrdersWithRows(){
        return em.createQuery("SELECT o FROM Order o JOIN FETCH o.orderRows", Order.class)
                .getResultList();
    }

    public List<Order> getAllOrders(){
        return em.createQuery("SELECT o FROM Order o", Order.class)
                .getResultList();
    }

    public Order getOrderById(long id){
        return em.find(Order.class, id);
    }

    public Order getOrderByIdWithRows(long id){
        var sql = em.createQuery("SELECT o FROM Order o JOIN FETCH o.orderRows WHERE o.id = :id", Order.class);
        sql.setParameter("id", id);
        return sql.getSingleResult();
    }

    @Transactional
    public Order insertOrder(Order order){
        if (order.getOrderRows() != null) {
            order.getOrderRows().forEach(orderRow -> orderRow.setOrder(order));
        }
        em.persist(order);
        return order;
    }

    @Transactional
    public OrderRow insertOrderRow(long orderId, OrderRow orderRow){
        var order = em.find(Order.class, orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }

        orderRow.setOrder(order);
        order.getOrderRows().add(orderRow);

        em.persist(orderRow);
        em.merge(order);

        return orderRow;
    }

    @Transactional
    public List<OrderRow> insertOrderRow(long orderId, List<OrderRow> orderRows){
        Order order = em.find(Order.class, orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }

        for (OrderRow orderRow : orderRows) {
            orderRow.setOrder(order);
            order.getOrderRows().add(orderRow);
            em.persist(orderRow);
        }
        em.merge(order);

        return orderRows;
    }


    @Transactional
    public Order updateOrder(Order order){
        if (order.getOrderRows() != null) {
            order.getOrderRows().forEach(orderRow -> orderRow.setOrder(order));
        }
        em.merge(order);
        em.flush();

        return order;
    }

    @Transactional
    public int deleteOrder(Order order){
        var sql = em.createQuery("DELETE FROM OrderRow o WHERE o.order.id = :orderId");
        sql.setParameter("orderId", order.getId());

        return sql.executeUpdate();
    }

    @Transactional
    public int deleteOrder(long id){
        var sql = em.createQuery("DELETE FROM OrderRow o WHERE o.order.id = :orderId");
        sql.setParameter("orderId", id);

        return sql.executeUpdate();
    }

}
