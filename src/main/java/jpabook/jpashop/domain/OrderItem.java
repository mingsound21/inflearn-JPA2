package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성메서드를 통해서만 객체 생성하도록! 아예 new를 통한 생성은 오류 발생하게끔
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id") // 연관관계의 주인
    private Item item;

    @JsonIgnore// 간단한 주문조회 V1: 엔티티 직접 노출에서 Order로 다시 돌아가서 무한루프 생김 방지를 위한 코드
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id") // 연관관계의 주인
    private Order order;
    
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    //== 생성 메서드 ==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);

        return orderItem;
    }

    //== 비즈니스 로직 ==//
    public void cancel(){
        getItem().addStock(count);
    }

    //== 조회 로직 ==//
    /**
     * 주문상품 전체 가격 조회
     */
    public int getTotalPrice(){
        return getOrderPrice() * getCount();
    }
}
