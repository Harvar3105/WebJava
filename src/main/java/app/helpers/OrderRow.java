package app.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderRow {
    private long id;
    private String itemName;
    private int quantity;
    private float price;
    private long orderId;
}
