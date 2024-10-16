package app.helpers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderResultSetExtractor implements ResultSetExtractor<List<Order>> {
    @Override
    public List<Order> extractData(ResultSet rs) throws SQLException {
        List<Order> result = new ArrayList<>();

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
                currentOrder.setOrderNumber(rs.getString("orderNumber"));
                currentOrder.setOrderRows(new OrderRow[0]);
            }

            if (currentOrder.getId() == rowOrderId){
                OrderRow[] newData = Arrays.copyOf(currentOrder.getOrderRows(), currentOrder.getOrderRows().length + 1);
                OrderRow row = OrderResultSetMapper.mapOrderRow(rs, rowOrderId);
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
