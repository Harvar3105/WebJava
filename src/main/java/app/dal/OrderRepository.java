package app.dal;

import app.helpers.Item;
import app.helpers.Order;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderRepository {

    private final DataSource ds;
    private static final String ORDER_FIELD = "orderNumber";

    public OrderRepository(DataSource dataSource){this.ds = dataSource;}

    public List<Order> getAll(boolean withItems) throws SQLException {
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

            if (!withItems) {return result;}

            Map<Long, List<Item>> groups = getAllItems(conn).stream().collect(Collectors.groupingBy(Item::getOrderNumber));
            for (Order order : result){
                if (groups.get(order.getId()) == null) {continue;}
                order.setItems(groups.get(order.getId()).toArray(Item[]::new));
            }
        }
        return result;
    }

    public List<Order> getAll(boolean withItems, int freeze) throws SQLException, InterruptedException {
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

            if (!withItems) {return result;}

            Map<Long, List<Item>> groups = getAllItems(conn).stream().collect(Collectors.groupingBy(Item::getOrderNumber));
            for (Order order : result){
                if (groups.get(order.getId()) == null) {continue;}
                order.setItems(groups.get(order.getId()).toArray(Item[]::new));
            }
        }
        return result;
    }

    private List<Item> getAllItems(Connection conn) throws SQLException{
        List<Item> result = new ArrayList<>();

        try(Statement st = conn.createStatement()){
            String sql = "SELECT * FROM items";
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()){
                Item item = new Item();
                item.setName(rs.getString("name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getFloat("price"));
                item.setOrderNumber(rs.getLong("orderId"));
                result.add(item);
            }
            return result;
        }
    }

    public Order getById(long id, boolean withItems) throws SQLException{
        String sql = "SELECT * FROM orders WHERE id = ?";
        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {throw new RuntimeException("Could not find by id: " + id);}

            Order order = new Order();
            order.setId(id);
            order.setOrderNumber(rs.getString(ORDER_FIELD));

            if(!withItems) {return order;}

            order.setItems(getItemsById(conn, id).toArray(Item[]::new));
            return order;
        }
    }

    public Order getById(long id, boolean withItems, int freeze) throws SQLException, InterruptedException {
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

            if(!withItems) {return order;}

            order.setItems(getItemsById(conn, id).toArray(Item[]::new));
            return order;
        }
    }

    private List<Item> getItemsById(Connection conn, long id) throws SQLException{
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE orderId = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Item item = new Item();
                item.setOrderNumber(id);
                item.setId(rs.getLong("id"));
                item.setName(rs.getString("name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getFloat("price"));
                items.add(item);
            }
            return items;
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

            Thread.sleep(freeze);

            return rs.getLong("id");
        }
    }

    private void insertItems(Item... items) throws SQLException{
        String sql = "INSERT INTO items(name, quantity, price, orderId) VALUES (?, ?, ?, ?)";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            con.setAutoCommit(false);

            for (Item item : items){
                ps.setString(1, item.getName());
                ps.setInt(2, item.getQuantity());
                ps.setFloat(3, item.getPrice());
                ps.setLong(4, item.getOrderNumber());
                ps.addBatch();
            }

            ps.executeBatch();
            con.commit();
        }
    }
}
