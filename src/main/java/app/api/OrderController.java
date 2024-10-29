package app.api;

import app.dal.OrderRepository;
import app.entities.Order;
import app.entities.OrderRow;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("orders")
public class OrderController {

    private OrderRepository rep;

    @Autowired
    public OrderController(OrderRepository rep){
        this.rep = rep;
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Order>> getAllOrders() throws SQLException {
        return ResponseEntity.ok(rep.getAllOrdersWithRows());
    }

    @ResponseBody
    @GetMapping(params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getById(@RequestParam("id") String id) throws SQLException {
        return ResponseEntity.ok(rep.getOrderById(Long.parseLong(id)));
    }

    @ResponseBody
    @GetMapping(value = "{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getByPathId(@PathVariable("orderId") String id) throws SQLException {
        return ResponseEntity.ok(rep.getOrderById(Long.parseLong(id)));
    }

    @ResponseBody
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws SQLException {
        order = rep.insertOrder(order);
        return ResponseEntity.ok(order);
    }

    @ResponseBody
    @PostMapping(value = "/{orderId}/row", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderRow> addOrderRow(@PathVariable("orderId") long orderId, @RequestBody OrderRow orderRow) throws SQLException {
        orderRow = rep.insertOrderRow(orderId, orderRow);
        return ResponseEntity.ok(orderRow);
    }

    @PostMapping("/{orderId}/rows")
    public void addOrderRowsBatch(@PathVariable("orderId") long orderId,@RequestBody List<OrderRow> orderRows) throws SQLException{
        rep.insertOrderRow(orderId, orderRows);
    }

    @DeleteMapping(params = "id")
    public ResponseEntity<Integer> deleteOrder(@RequestParam("id") long id) throws SQLException{
        return ResponseEntity.ok(rep.deleteOrder(id));
    }
}
