package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // [ 비즈니스 로직 ] => 데이터를 가지고 있는 쪽에서 비즈니스 로직이 있으면 응집도가 높아진다.(setter 사용 지양)

    /**
     * 재고 추가
     * @param quantity 추가할 재고 수량
     */
    public void addStockQuantity(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 재고 감소
     * @param quantity 줄일 재고 수량
     */
    public void subtractStockQuantity(int quantity) {
        int rest = this.stockQuantity - quantity;

        if (rest < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = rest;
    }
}
