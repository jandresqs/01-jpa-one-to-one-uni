package dev.jaqs.cruddemo.dao;

import dev.jaqs.cruddemo.entity.Instructor;

public interface AppDAO {

    // Instructor

    Instructor findInstructorById(Long id);

    void saveInstructor(Instructor instructor);

    void deleteInstructorById(Long id);

}
