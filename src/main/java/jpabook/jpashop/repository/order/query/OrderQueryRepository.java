package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders();

        // findOrders에서 OrderItems 컬렉션 못 채워서 findOrderItems함수 별도로 만들어서 채워줌
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;

    }

    // OrderItem - Item: @ManyToOne
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    // Order - Member, Order - Delivery : @OneToOne
    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" + // orderItem은 넣을 수 없음
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();

    }
}
// Q. OrderQueryDto, OrderItemQueryDto를 왜 OrderApiController에 만들어둔 DTO를 사용하지 않고 새로 만드셨나요?
// A. Controller에 정의해둔 DTO를 Repository에서 사용할 경우, 의존관계가 Repository -> Controller 생겨서 의존관계 순환이 생김
//      + 그냥 같은 패키지에 넣음
