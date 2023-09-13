package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
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

    // 엔티티가 외부에 그대로 노출됨
    @GetMapping("/api/v1/orders")
    public List<Order> getOrdersV1() {
        List<Order> orders = orderRepository.findAll((new OrderSearch()));

        for (Order order : orders) {
            // 프록시 강제 초기화
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(orderItem -> orderItem.getItem().getName());
        }

        return orders;
    }

    // 쿼리가 너무 많이 날라감.
    @GetMapping("/api/v2/orders")
    public List<OrderDto> getOrdersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());
        return collect;
    }

    @Getter
    private static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems()
                    .stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    private static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            System.out.println(orderItem.getItem().getName());
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> getOrdersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> getOrdersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        // 1. OneToOne, ManyToOne은 fetch join 한다. + paging
        List<Order> orders = orderRepository.findAllWithMemberAndDelivery(offset, limit);

        // 2. orderItem lazy loading 할 때
        // SQL IN 절로 batch size만큼 조회할 OrderItem id가 들어간다.
        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }
}
