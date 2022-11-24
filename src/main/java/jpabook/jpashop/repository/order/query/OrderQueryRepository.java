package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    // V4: JPA에서 DTO 직접 조회
    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders();

        // findOrders에서 OrderItems 컬렉션 못 채워서 findOrderItems함수 별도로 만들어서 채워줌
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    // V5: N+1문제 해결을 위한 코드
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders(); // result: Order들

        // OrderId 담긴 배열
        List<Long> orderIds = toOrderIds(result);

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    /**
     * IN절 사용해서 한방에 OrderItems 가져와서 key: OrderId, value: List<OrderItemQueryDto>인 Map 반환
     */
    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class) // IN절 사용해서 최적화
                .setParameter("orderIds", orderIds)
                .getResultList();

        // OrderItems -> Map으로 변환: 매칭 성능 향상 - O(1)
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto -> OrderItemQueryDto.getOrderId()));
        return orderItemMap;
    }

    /**
     * 조회한 Orders에서 OrderId 배열 추출
     */
    private static List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
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
