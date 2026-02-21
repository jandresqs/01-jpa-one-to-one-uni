package dev.jaqs.cruddemo.dao;

import dev.jaqs.cruddemo.entity.Instructor;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AppDAOImpl implements AppDAO{

    private final EntityManager entityManager;

    @Autowired
    public AppDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Instructor findInstructorById(Long id) {
        return entityManager.find(Instructor.class, id);
    }

    @Override
    @Transactional // Better in the service layer
    public void saveInstructor(Instructor instructor) {
        entityManager.persist(instructor);
    }

    @Override
    @Transactional
    public void deleteInstructorById(Long id) {
        Instructor instructor = findInstructorById(id);
        entityManager.remove(instructor);
    }

}

