package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){ // 신규 등록
            em.persist(item);
        }else{ // 이미 DB에 등록된 것을 가져온 뒤 update 하는,,
            Item merge = em.merge(item);// 파라미터로 넘어가는 item은 영속상태 되지 X, merge의 반환값인 merge는 영속상태
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
