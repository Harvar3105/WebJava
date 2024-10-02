package app.helpers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Order {
    private long id;
    private String orderNumber;
    private OrderRow[] orderRows;
}
