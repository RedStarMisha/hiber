package ru.get.hiber.service.buisnes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.get.hiber.model.Item;
import ru.get.hiber.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    public Item addItem(Item item) {
        Item savedItem = itemRepository.save(item);
        log.info("Add new item {}", savedItem);
        return savedItem;
    }

    public List<Item> addItems(List<Item> items) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.baeldung.movie_catalog");
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        Item item = items.get(0);
        items = itemRepository.saveAll(items);
        log.info("Item's batch of {} pieces was added", items.size());
        return items;
    }

    public List<Item> getAllItems() {

    }
}
