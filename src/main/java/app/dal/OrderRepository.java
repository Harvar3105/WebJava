package app.dal;

import app.helpers.Order;
import app.helpers.OrderResultSetExtractor;
import app.helpers.OrderRow;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Repository
public class OrderRepository {

    @Autowired
    private final JdbcTemplate template;
    private SimpleJdbcInsert orderRowInsert;
    private SimpleJdbcInsert orderInsert;
    private final OrderResultSetExtractor extractor = new OrderResultSetExtractor();

    public OrderRepository(DataSource ds){
        this.template = new JdbcTemplate(ds);
    }

    @PostConstruct
    public void init(){
        orderRowInsert = new SimpleJdbcInsert(template)
                .withTableName("orderRows")
                .usingGeneratedKeyColumns("id");
        orderInsert = new SimpleJdbcInsert(template)
                .withTableName("orders")
                .usingGeneratedKeyColumns("id");
    }

    public List<Order> getAllWithJoin() throws SQLException {
        String sql = "SELECT o.id as orderId, o.orderNumber, r.id as orderRowId, r.itemName, r.quantity, r.price, r.orderId AS rowOrderId FROM orders o LEFT JOIN orderRows r ON o.id = r.orderId";
        return template.query(sql, extractor);
    }

    public Order getOrderById(long id) throws SQLException {
        String sql = "SELECT o.id as orderId, o.orderNumber, r.id as orderRowId, r.itemName, r.quantity, r.price, r.orderId AS rowOrderId FROM orders o LEFT JOIN orderRows r ON o.id = r.orderId WHERE o.id=?";
        return Objects.requireNonNull(template.query(sql, extractor, id)).getFirst();
    }

    public long saveOrderRow(OrderRow orderRow) throws SQLException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("itemName", orderRow.getItemName());
        parameters.put("quantity", orderRow.getQuantity());
        parameters.put("price", orderRow.getPrice());
        parameters.put("orderId", orderRow.getOrderId());

        Number newId = orderRowInsert.executeAndReturnKey(parameters);
        return newId.longValue();
    }

    public long saveOrder(Order order) throws SQLException{
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orderNumber", order.getOrderNumber());

        Number newId = orderInsert.executeAndReturnKey(parameters);

        if (order.getOrderRows() != null) {
            var rows = order.getOrderRows();
            for (OrderRow row : rows){
                row.setOrderId(newId.longValue());
            }
            if (rows.length == 1){
                saveOrderRow(rows[0]);
            } else if (rows.length > 1){
                saveOrderRowsBatch(Arrays.asList(rows));
            }
        }

        return newId.longValue();
    }

    public void saveOrderRowsBatch(List<OrderRow> orderRows) {
        String sql = "INSERT INTO orderRows (itemName, quantity, price, orderId) VALUES (?, ?, ?, ?)";

        template.batchUpdate(sql, orderRows, orderRows.size(),
                (ps, orderRow) -> {
                    ps.setString(1, orderRow.getItemName());
                    ps.setInt(2, orderRow.getQuantity());
                    ps.setFloat(3, orderRow.getPrice());
                    ps.setLong(4, orderRow.getOrderId());
                });
    }

    public int deleteOrder(long id) throws SQLException{
        String sql = "DELETE FROM orders WHERE id=?";
        return template.update(sql, id);
    }

}
