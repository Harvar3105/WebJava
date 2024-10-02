package app.helpers;

import lombok.Data;

@Data
public class OrderRow {
    private String itemName;
    private int quantity;
    private long price;
}
