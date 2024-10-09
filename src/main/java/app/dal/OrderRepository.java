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
            String sql = "SELECT * FROM orders LEFT JOIN orderRows ON orders.id = orderRows.orderId";
            ResultSet rs = st.executeQuery(sql);

            Order currentOrder = null;
            while (rs.next()){

                long orderId = rs.getLong("orders.Id");
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
                    OrderRow row = new OrderRow();
                    row.setId(rs.getLong("orderRows.Id"));
                    row.setItemName(rs.getString("itemName"));
                    row.setQuantity(rs.getInt("quantity"));
                    row.setPrice(rs.getFloat("price"));
                    row.setOrderId(rowOrderId);
                    newData[newData.length - 1] = row;
                    currentOrder.setOrderRows(newData);
                }
            }
            result.add(currentOrder);

            return result;
        }
    }

    public List<Order> getAll(boolean withRows) throws SQLException {
        List<Order> result = new ArrayList<>();

        try(Connection conn = ds.getConnection();
            Statement st = conn.createStatement()){
            String sql = "SELECT * FROM orders";
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                Order order = new Order();
                order.setOrderNumber(rs.getString(ORDER_FIELD));
                order.setId(rs.getLong("id"));
                result.add(order);
            }

            if (!withRows) {return result;}

            Map<Long, List<OrderRow>> groups = getAllRows(conn).stream().collect(Collectors.groupingBy(OrderRow::getOrderId));
            for (Order order : result){
                if (groups.get(order.getId()) == null) {continue;}
                order.setOrderRows(groups.get(order.getId()).toArray(OrderRow[]::new));
            }
        }
        return result;
    }

    public List<Order> getAll(boolean withRows, int freeze) throws SQLException, InterruptedException {
        List<Order> result = new ArrayList<>();

        try(Connection conn = ds.getConnection();
            Statement st = conn.createStatement()){
            String sql = "SELECT * FROM orders";
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                Order order = new Order();
                order.setOrderNumber(rs.getString(ORDER_FIELD));
                order.setId(rs.getLong("id"));
                result.add(order);
            }

            Thread.sleep(freeze);

            if (!withRows) {return result;}

            Map<Long, List<OrderRow>> groups = getAllRows(conn).stream().collect(Collectors.groupingBy(OrderRow::getOrderId));
            for (Order order : result){
                if (groups.get(order.getId()) == null) {continue;}
                order.setOrderRows(groups.get(order.getId()).toArray(OrderRow[]::new));
            }
        }
        return result;
    }

    private List<OrderRow> getAllRows(Connection conn) throws SQLException{
        List<OrderRow> result = new ArrayList<>();

        try(Statement st = conn.createStatement()){
            String sql = "SELECT * FROM orderRows";
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()){
                OrderRow row = new OrderRow();
                row.setItemName(rs.getString("itemName"));
                row.setQuantity(rs.getInt("quantity"));
                row.setPrice(rs.getFloat("price"));
                row.setOrderId(rs.getLong("orderId"));
                result.add(row);
            }
            return result;
        }
    }

    public Order getById(long id, boolean withRows) throws SQLException{
        String sql = "SELECT * FROM orders WHERE id = ?";
        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {throw new RuntimeException("Could not find by id: " + id);}

            Order order = new Order();
            order.setId(id);
            order.setOrderNumber(rs.getString(ORDER_FIELD));

            if(!withRows) {return order;}

            order.setOrderRows(getRowsById(conn, id).toArray(OrderRow[]::new));
            return order;
        }
    }

    public Order getById(long id, boolean withRows, int freeze) throws SQLException, InterruptedException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {throw new RuntimeException("Could not find by id: " + id);}

            Order order = new Order();
            order.setId(id);
            order.setOrderNumber(rs.getString(ORDER_FIELD));

            Thread.sleep(freeze);

            if(!withRows) {return order;}

            order.setOrderRows(getRowsById(conn, id).toArray(OrderRow[]::new));
            return order;
        }
    }

    private List<OrderRow> getRowsById(Connection conn, long id) throws SQLException{
        List<OrderRow> rows = new ArrayList<>();
        String sql = "SELECT * FROM orderRows WHERE orderId = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                OrderRow row = new OrderRow();
                row.setOrderId(id);
                row.setId(rs.getLong("id"));
                row.setItemName(rs.getString("itemName"));
                row.setQuantity(rs.getInt("quantity"));
                row.setPrice(rs.getFloat("price"));
                rows.add(row);
            }
            return rows;
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

    public long insertOrder(Order order, int freeze) throws SQLException, InterruptedException {
        String sql = "INSERT INTO orders(orderNumber) VALUES (?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[] {"id"})){
            ps.setString(1, order.getOrderNumber());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {throw new RuntimeException("Could not insert: " + order);}

            order.setId(rs.getLong("id"));
            insertRows(conn, order);
            Thread.sleep(freeze);

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

    public void deleteOrderByNumber(String number) throws SQLException {
        String sql = "DELETE FROM orders WHERE orderNumber=?";
        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            ps.executeUpdate();
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
