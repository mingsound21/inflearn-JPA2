package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore// 간단한 주문조회 V1: 엔티티 직접 노출에서 Order로 다시 돌아가서 무한루프 생김 방지를 위한 코드
    @OneToOne(mappedBy = "delivery", fetch = LAZY) // 연관관계의 주인 X
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // enum type 무조건 STRING으로
    private DeliveryStatus status; // READY, COMP



}
