package app.dal;

import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    public List<User> getAllUsers(){
        return em.createQuery("SELECT u FROM User u JOIN FETCH u.authorities", User.class)
                .getResultList();
    }

    public User getUserByName(String userName){
        var sql = em.createQuery("SELECT u FROM User u JOIN FETCH u.authorities WHERE u.userName = :name", User.class);
        sql.setParameter("name", userName);
        return sql.getSingleResult();
    }
}
