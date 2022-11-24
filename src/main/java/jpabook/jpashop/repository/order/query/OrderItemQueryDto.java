package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemQueryDto {

    @JsonIgnore // 하다가 orderId 필요없으면
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;
}
