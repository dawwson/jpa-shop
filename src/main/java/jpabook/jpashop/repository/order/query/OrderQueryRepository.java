package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        // N + 1 문제
        List<OrderQueryDto> orders = findOrders();  // Query 1번 -> 결과 N개
        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행)
        orders.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());  // Query N번
            o.setOrderItems(orderItems);
        });

        orders.forEach(order -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(order.getOrderId());
            order.setOrderItems(orderItems);
        });

        return orders;
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate,o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto" +
                                "(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orders = findOrders();  // order에 member, delivery join

        List<Long> orderIds = toOrderIds(orders);

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        orders.forEach(order -> order.setOrderItems(orderItemMap.get(order.getOrderId())));

        return orders;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class
                )
                .setParameter("orderIds", orderIds)
                .getResultList();
        // select
        //        oi.order_id,
        //        i.name,
        //        oi.order_price,
        //        oi.count
        //    from
        //        order_item oi
        //    join
        //        item i
        //            on i.item_id=oi.item_id
        //    where
        //        oi.order_id in (?,?)

        return orderItems.stream()
                .collect(Collectors.groupingBy(orderItem -> orderItem.getOrderId()));
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> orders) {
        return orders.stream()
                .map(order -> order.getOrderId())
                .collect(Collectors.toList());
    }
}
