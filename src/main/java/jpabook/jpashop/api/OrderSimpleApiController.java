package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 컬렉션이 아닌, xToOne 관계
 * ===== 연관관계 =====
 * Order
 * Order -> Member     : @ManyToOne(fetch = LAZY)
 * Order -> Delivery   : @OneToOne(fetch = LAZY)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // 그냥 new OrderSearch() 넘기면 검색 조건 없이 전체 조회
        for (Order order : all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getOrder(); // Lazy 강제 초기화
        }
        return all;
    }
    /* === 문제점 1) 무한루프
        Order를 반환할 때, Order -> Member -> 다시 양방향 걸려서 Order로 + (Delivery, OrderItem에 걸린 양방향 Order로 가는 것들)
            => 결국, Order <-> Member, Delivery, OrderItem: 무한루프에 빠짐
            양방향 연관관계 중 한쪽을 끊어줘야함: Member, Delivery, OrderItem에서 Order로 가는쪽에 @JsonIgnore 어노테이션 추가
     */
    /* === 문제점 2) Fetch = LAZY: 프록시 객체
        지연로딩인 경우에는 JSON 라이브러리에게 이 객체는 뿌리지 말라고 할 수 있음.
            1) 하이버네이트5 모듈 설치
                => 지연로딩 모두 null
            2) 하이버네이트5 사용 + 일부러 프록시 객체 초기화 시키기
                => 정보 가져오길 원하는 것만 일부러 프록시 객체 초기화 시키기, 프록시 초기화 안한 것은 null(하이버네이트5에 의해서)
     */

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){ // 쿼리 5방
        // N + 1 문제 -> 1(주문) +  회원 N + 배송 N
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){ // 쿼리 1방
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}

// ========== 주의 깊게 봐야할 점 ==========
// ===== V1 =====
// 1. 엔티티 직접 노출 (BAD)
/*
    - 엔티티 스펙 변경시 API 스펙도 변경됨
    - 밖으로 내보내지 말아야하는 내용까지 내보내게 됨
    - 무한루프
    - Fetch = LAZY로 인해 프록시 객체로 인한 문제
 */

// 2. 가급적이면 필요한 스펙만 최소한으로 API 스펙으로 노출

// 3. 그렇다고 LAZY 문제 피하려고, 절대 EAGER로 설정하면 안됨 => SQL 예측이 안됨

// ===== V2 =====
// 1. LAZY 로딩으로 인한 과다한 DB 쿼리 호출 (BAD) -> 해결 : V3의 페치 조인

// ===== V2 =====
// 1. 페치조인 SQL보면 SELECT에서 모든 필드 값들을 모두 가져옴
