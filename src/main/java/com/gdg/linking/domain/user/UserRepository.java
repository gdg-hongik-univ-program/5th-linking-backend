package com.gdg.linking.domain.user;

import com.gdg.linking.domain.user.dto.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;



    public void register(User user){

        em.persist(user);

    }

    public User findByLoginId(String loginId) {
        List<User> result = em.createQuery(
                "SELECT u FROM User u WHERE u.loginId = :loginId", User.class
        ).setParameter("loginId",loginId).getResultList();

        return result.isEmpty() ? null : result.get(0);

    }

    public User findByIdAndPassword(String loginId, String password) {
        List<User> result = em.createQuery(
                "SELECT u FROM User u WHERE u.loginId = :loginId AND u.password = :password",User.class)
                .setParameter("loginId",loginId)
                .setParameter("password",password)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}
