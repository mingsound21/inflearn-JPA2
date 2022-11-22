package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter // @Setter 없음: 값 타입은 변경 불가능하게 설계
public class Address {
    private String city;
    private String street;
    private String zipcode;

    // 기본 생성자 반드시 필요 : JPA가 리플렉션, 프록시같은 기술을 사용할 수 있도록 지원해야하기 때문
    // protected로 두는 이유는 이것보다는 public으로 된 생성자를 사용해야겠구나!라고 인지할 수 있도록
    protected Address(){

    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
