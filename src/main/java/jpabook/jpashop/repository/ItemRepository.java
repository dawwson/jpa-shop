package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            // 신규 아이템 등록(새로 등록할 때는 id가 없음)
            em.persist(item);
        } else {
            // 이미 있는 상품 업데이트
            em.merge(item);
        }
    }

    public Item findById(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em
                .createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
