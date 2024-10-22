package app.helpers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderRow {
    private long id;
    @Size(min = 1, message = "OrderName must be at least 1 character long!")
    private String itemName;
    @Min(value = 1, message = "Cannot be 0 or less!")
    private int quantity;
    @Positive(message = "Must be greater than 0!")
    private float price;
    private long orderId;
}
