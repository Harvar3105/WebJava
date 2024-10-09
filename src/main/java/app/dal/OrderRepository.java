package app.dal;

import app.helpers.OrderRow;
import app.helpers.Order;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderRepository {

    private final DataSource ds;
    private static final String ORDER_FIELD = "orderNumber";

    public OrderRepository(DataSource dataSource){this.ds = dataSource;}

    public List<Order> getAllWithJoin() throws SQLException {
        List<Order> result = new ArrayList<>();

        try (Connection conn = ds.getConnection();
            Statement st = conn.createStatement()){
            String sql = "SELECT o.id as orderId, o.orderNumber, r.id as orderRowId, r.itemName, r.quantity, r.price, r.orderId AS rowOrderId FROM orders o LEFT JOIN orderRows r ON o.id = r.orderId";
            ResultSet rs = st.executeQuery(sql);

            Order currentOrder = null;
            while (rs.next()){

                long orderId = rs.getLong("orderId");
                long rowOrderId = rs.getLong("orderId");

                if (currentOrder == null || currentOrder.getId() != rowOrderId) {
                    if (currentOrder != null) {
                        result.add(currentOrder);
                    }

                    currentOrder = new Order();
                    currentOrder.setId(orderId);
                    currentOrder.setOrderNumber(rs.getString(ORDER_FIELD));
                    currentOrder.setOrderRows(new OrderRow[0]);
                }

                if (currentOrder.getId() == rowOrderId){
                    OrderRow[] newData = Arrays.copyOf(currentOrder.getOrderRows(), currentOrder.getOrderRows().length + 1);
                    OrderRow row = buildOrderRow(rs, rowOrderId);
                    newData[newData.length - 1] = row;
                    currentOrder.setOrderRows(newData);
                }
            }
            if (currentOrder != null) {
                result.add(currentOrder);
            }

            return result;
        }
    }

    private OrderRow buildOrderRow(ResultSet rs, long orderId) throws SQLException{
        OrderRow row = new OrderRow();
        row.setId(rs.getLong("orderId"));
        row.setItemName(rs.getString("itemName"));
        row.setQuantity(rs.getInt("quantity"));
        row.setPrice(rs.getFloat("price"));
        row.setOrderId(orderId);
        return row;
    }

    public Order getById(long id, boolean withRows) throws SQLException{
        String sql = "SELECT o.id as orderId, o.orderNumber, r.id as orderRowId, r.itemName, r.quantity, r.price, r.orderId AS rowOrderId FROM orders o LEFT JOIN orderRows r ON o.id = r.orderId WHERE o.id=?";
        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Order order = new Order();
                order.setId(id);
                order.setOrderNumber(rs.getString(ORDER_FIELD));

                if (!withRows) {
                    return order;
                }

                List<OrderRow> rows = new ArrayList<>();

                do {
                    rows.add(buildOrderRow(rs, id));
                } while (rs.next());

                order.setOrderRows(rows.toArray(OrderRow[]::new));
                return order;
            }
        }
        return null;
    }

    public List<Order> insertOrderBulk(List<Order> orders) throws SQLException {
        String orderSql  = "INSERT INTO orders(orderNumber) VALUES (?)";
        String orderRowSql = "INSERT INTO orderRows(itemName, quantity, price, orderId) VALUES (?, ?, ?, ?)";
        Order[] arrayed = orders.toArray(Order[]::new);
        try (Connection conn = ds.getConnection();
            PreparedStatement orderPs = conn.prepareStatement(orderSql , new String[] { "id" });
            PreparedStatement orderRowPs = conn.prepareStatement(orderRowSql, new String[] {"id"})) {
            conn.setAutoCommit(false);

            for (Order order : orders){
                orderPs.setString(1, order.getOrderNumber());
                orderPs.addBatch();
            }
            orderPs.executeBatch();

            ResultSet keys = orderPs.getGeneratedKeys();
            int pos = 0;
            while (keys.next()){
                Order order = orders.get(pos);
                order.setId(keys.getLong("id"));
                arrayed[pos] = order;
                pos++;
            }

            for (Order order : arrayed) {
                for (OrderRow orderRow : order.getOrderRows()) {
                    orderRowPs.setString(1, orderRow.getItemName());
                    orderRowPs.setInt(2, orderRow.getQuantity());
                    orderRowPs.setFloat(3, orderRow.getPrice());
                    orderRowPs.setLong(4, order.getId());
                    orderRowPs.addBatch();
                }
            }
            orderRowPs.executeBatch();

            ResultSet rowKeys = orderRowPs.getGeneratedKeys();
            for (Order order : orders) {
                for (OrderRow orderRow : order.getOrderRows()) {
                    if (rowKeys.next()) {
                        orderRow.setId(rowKeys.getLong("id"));
                        orderRow.setOrderId(order.getId());
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

            return Arrays.stream(arrayed).toList();
        }
    }

    public long insertOrder(Order order) throws SQLException{
        String sql = "INSERT INTO orders(orderNumber) VALUES (?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[] {"id"})){
            ps.setString(1, order.getOrderNumber());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {throw new RuntimeException("Could not insert: " + order);}

            order.setId(rs.getLong("id"));
            insertRows(conn, order);

            return rs.getLong("id");
        }
    }

    private void insertRows(Connection conn, Order order) throws SQLException{
        if (order.getOrderRows() == null) {
            return;
        }

        String sql = "INSERT INTO orderRows(itemName, quantity, price, orderId) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);

            for (OrderRow row : order.getOrderRows()){
                ps.setString(1, row.getItemName());
                ps.setInt(2, row.getQuantity());
                ps.setFloat(3, row.getPrice());
                ps.setLong(4, order.getId());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    public void deleteOrderById(long id) throws SQLException{
        String sql = "DELETE FROM orders WHERE id=?";
        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void deleteOrderRowById(Connection conn, long id) throws SQLException{
        String sql = "DELETE FROM orderRows WHERE id=?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
