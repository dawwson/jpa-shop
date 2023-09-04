package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 서비스에서 setter를 사용하지 않도록 기본생성자를 protected로 설정
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    // 디폴트가 즉시로딩(EAGER) -> 지연로딩(LAZY)로 바꿔야 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 디폴트가 즉시로딩(EAGER) -> 지연로딩(LAZY)로 바꿔야 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;  // 주문 가격

    private int count; // 주문 수량

    /* 생성 메서드 */
    /**
     * 주문 상품 생성
     * @param item 상품
     * @param orderPrice 주문 가격
     * @param count 주문 수량
     * @return 주문 상품
     */
    public static OrderItem create(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.subtractStockQuantity(count);  // 주문 수량 만큼 재고 감소
        return orderItem;
    }

    /* 비즈니스 로직 */
    /**
     * 주문 취소
     */
    public void cancel() {
        getItem().addStockQuantity(count);
    }

    /* 조회 로직 */
    /**
     * 주문 상품 전체 가격 조회
     * @return 주문 상품 전체 가격
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
