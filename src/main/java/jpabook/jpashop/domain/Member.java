package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty // @Valid 적용 - @ResquestBody로 Member 객체 받을 때, name은 empty이면 에러
    private String name;

    @Embedded
    private Address address;

    // @JsonIgnore // 회원조회V1일 때, orders 제외하고 싶어서 => 엔티티에 presentation 계층을 위한 로직 포함됨(BAD)
    @OneToMany(mappedBy = "member") // 연관관계의 주인 X
    private List<Order> orders = new ArrayList<>(); // 컬렉션은 필드에서 바로 초기화하는 것이 null 문제에서 안전


}
