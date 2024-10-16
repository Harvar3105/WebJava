package app.helpers;

import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Order {
    private long id;
    @NonNull
    @Size(min = 2, message = "Order number must be at least 2 characters long")
    private String orderNumber;
    private OrderRow[] orderRows;
}
