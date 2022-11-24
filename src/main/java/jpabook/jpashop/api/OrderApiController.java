package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            // 프록시 객체 강제 초기화
            order.getMember().getName();
            order.getDelivery().getAddress();

            // orderItems, orderItem 프록시 객체 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    // SQL 수: 1(Order) + Order개수 x [N(Member:1, Order당 1명) + N(Delivery:1, Order당 1개) + N(OrderItems:1, 각 Order당 1개) + N(OrderItem:2, 각 OrderItems당 2개)]
    // 즉, 2개의 주문이니까 1(order) + 2 x (1 + 1 + 1 + 2) = 1 + 10 = 11
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }
    // BAD) 엔티티 외부로 노출 X
    // Entity를 외부로 노출하지 말라는 것은 완전히 Entity에 대한 의존을 끊어야 한다는 말
    // => OrderDto안에 OrderItem은 또 다시 Entity

    // SQL 수: 1
    // BAD: 페치 조인 + 컬렉션 = 페이징 불가능
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        for (Order order : orders) {
            System.out.println("order ref = " + order + " id = " + order.getId()); // 객체 참조값과 id
        }

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    // SQL 수: 3
    // V3의 문제) SQL 수는 1개였지만, h2 DB에서 실제로 쿼리 날리고 결과를 보면 중복 컬럼이 되게 많음. => 중복 데이터 다 애플리케이션으로 전송됨 = 용량 많음 이슈
    // BUT, V3.1은) 결과가 중복이 없음.
    // TRADE OFF) 네트워크 호출하는 횟수(SQL수) <-> 전송하는 데이터 양  => 상황에 따라서 판단해야함
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit){

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); // 1. ToOne관계는 모두 페치 조인 적용

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    // SQL 수: 루트 1번 + 컬렉션 N번 => N + 1 문제 터진 것
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }


    // SQL 수: 루트 1번 + 컬렉션 IN절 사용 1번 = 2번
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }

    @Data
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; // List<OrderItem> ->  List<OrderItemDto> 변경

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); // OrderItems안에 있는 OrderItem도 엔티티라서 강제초기화 필요
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto{
        private String itemName; //상품명
        private int orderPrice; //주문 가격
        private int count; //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
