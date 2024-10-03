package app.helpers;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Order {
    private long id;
    @NonNull
    private String orderNumber;
    private Item[] items;
}
