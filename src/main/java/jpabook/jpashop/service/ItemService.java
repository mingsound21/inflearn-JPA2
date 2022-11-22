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
    public void saveItem(Item item){
        itemRepository.save(item);
    }


    // em.merge(준영속 엔티티)가 아래와 똑같은 코드를 실행해줌
    @Transactional
    public void updateItem(Long itemId, UpdateItemDto itemDto){
        Item findItem = itemRepository.findOne(itemId); // 영속상태가 됨
        // dirty checking됨
        findItem.setName(itemDto.getName());
        findItem.setPrice(itemDto.getPrice());
        findItem.setStockQuantity(itemDto.getStockQuantity());

        // itemRepository.save(findItem); 혹은 merge 이런거 할 필요 X
        // @Transactional에 의해서 commit됨 >> flush(변경된 애 찾음) >> update SQL 날림
    }
    // TODO: 리팩토링) 변경을 set을 사용하기 보다는 의미있는 함수를 만들어서 사용하는게 좋음
    // >> findItem.change(price, name, stockQuantity);
    // 그래야 변경지점이 모두 엔티티에서 일어나고, 유지보수를 할 때 변경된 장소 추적이 쉬움

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
