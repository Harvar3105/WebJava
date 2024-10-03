package app.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Item {
    private long id;
    private String name;
    private int quantity;
    private float price;
    @JsonIgnore
    private long orderNumber;
}
