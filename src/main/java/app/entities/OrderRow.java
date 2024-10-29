package app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "OrderRows")
public class OrderRow {
    @Id
    @GeneratedValue
    private long id;
    @Size(min = 1, message = "OrderName must be at least 1 character long!")
    private String itemName;
    @Min(value = 1, message = "Cannot be 0 or less!")
    private int quantity;
    @Positive(message = "Must be greater than 0!")
    private float price;

    @ManyToOne
    @JoinColumn(name = "orders_id")
    @JsonBackReference
    private Order order;
}
