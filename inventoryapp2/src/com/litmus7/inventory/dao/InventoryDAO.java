
package com.litmus7.inventory.dao;

import com.litmus7.inventory.exception.DatabaseOperationException;
import com.litmus7.inventory.dto.Inventory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryDAO {
    private static final Logger logger = LogManager.getLogger(InventoryDAO.class);
    private static final String INSERT_SQL =
            "INSERT INTO inventory (SKU, ProductName, Quantity, Price) VALUES (?, ?, ?, ?)";
   
    

    public void insertItem(Connection connection, Inventory item) throws DatabaseOperationException {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, item.getSku());
            ps.setString(2, item.getProductName());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPrice());
            ps.executeUpdate();
            logger.debug("Inserted item: {}", item.getSku());
        } catch (SQLException e) {
            logger.error("Failed to insert item: {}", item.getSku(), e);
            throw new DatabaseOperationException("Error inserting inventory item: " + item.getSku(), e);
        }
    }
}
