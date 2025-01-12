package ru.get.hiber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.get.hiber.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
