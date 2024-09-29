package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        // id가 없다 == 새로운 객체다
        if (item.getId() == null) {
            em.persist(item);
        } else {
            // 병합
            // itemService의 updateItem와 동일
            // mergeItem : 영속성 컨텍스트에서 관리, item : X
            // 변경감지 : 원하는 속성만 변경 가능, 병합 : 모든 속성이 변경
            Item mergeItem = em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
