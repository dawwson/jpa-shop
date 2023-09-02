package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 디폴트가 즉시로딩(EAGER) -> 지연로딩(LAZY)로 바꿔야 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 디폴트가 지연로딩(Lazy)
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    // 디폴트가 즉시로딩(EAGER) -> 지연로딩(LAZY)로 바꿔야 함
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;  // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status;  // 주문상태 [ORDER, CANCEL]
}
