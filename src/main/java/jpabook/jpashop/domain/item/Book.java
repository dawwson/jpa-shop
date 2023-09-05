package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.service.dto.UpdateBookDto;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B")
@Getter @Setter
public class Book extends Item {
    private String author;
    private String isbn;

    public void changeBook(UpdateBookDto dto) {
        this.setName(dto.getName());
        this.setPrice(dto.getPrice());
        this.setStockQuantity(dto.getStockQuantity());
        this.setAuthor(dto.getAuthor());
        this.setIsbn(dto.getIsbn());
    }
}
