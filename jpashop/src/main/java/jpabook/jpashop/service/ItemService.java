package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // 변경 감지
    @Transactional
    public Item updateItem(Long itemId, Book param) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
        // save를 호출할 필요가 없음 -> transactional에 의해 commit이 됨
        // itemRepository.save(findItem);
        return findItem;
    }

    @Transactional
    public Item updateItem(Long itemId, String name, int price, int stockQuantity) {
        // repository에서 조회하면 영속성 컨텍스트에서 관리하기 때문에 더티체킹이 됨 -> 변경 감지
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
        // save를 호출할 필요가 없음 -> transactional에 의해 commit이 됨
        // itemRepository.save(findItem);
        return findItem;
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
