package app.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class OrderResultSetMapper {
    public static Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderNumber(rs.getString("orderNumber"));
        return order;
    }

    public static OrderRow mapOrderRow(ResultSet rs, long orderId) throws SQLException{
        OrderRow row = new OrderRow();
        row.setId(rs.getLong("Id"));
        row.setPrice(rs.getFloat("price"));
        row.setQuantity(rs.getInt("quantity"));
        row.setItemName(rs.getString("itemName"));
        row.setOrderId(orderId);
        return row;
    }
}
