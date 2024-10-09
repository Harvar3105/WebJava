package app.helpers;

import lombok.Data;

@Data
public class OrderRow {
    private long id;
    private String itemName;
    private int quantity;
    private float price;
//    @JsonIgnore
    private long orderId;
}
