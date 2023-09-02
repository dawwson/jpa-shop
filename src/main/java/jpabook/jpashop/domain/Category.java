package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    // 디폴트가 즉시로딩(EAGER) -> 지연로딩(LAZY)로 바꿔야 함
    @ManyToOne(fetch = FetchType.LAZY) // 순환참조
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent") // 순환참조
    private List<Category> child = new ArrayList<>();

    // [ 연관관계 편의 메서드 ]
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
