package jpabook.jpashop.domain;

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

    @OneToOne(mappedBy = "delivery", fetch = LAZY) // 연관관계의 주인 X
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // enum type 무조건 STRING으로
    private DeliveryStatus status; // READY, COMP



}
