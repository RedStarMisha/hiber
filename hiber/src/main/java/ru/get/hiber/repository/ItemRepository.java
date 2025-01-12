package ru.get.hiber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.get.hiber.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>  {
}
