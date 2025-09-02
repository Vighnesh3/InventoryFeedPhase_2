package com.litmus7.inventory.service;

import com.litmus7.inventory.dao.InventoryDAO;
import com.litmus7.inventory.dto.Inventory;
import com.litmus7.inventory.exception.DatabaseOperationException;
import com.litmus7.inventory.exception.FileProcessingException;
import com.litmus7.inventory.util.DatabaseConnection;
import com.litmus7.inventory.util.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;

public class InventoryService implements Runnable {
    private static final Logger logger = LogManager.getLogger(InventoryService.class);
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final File csvFile;
    private static final String PROCESSED_DIR = "D:\\Java_lit\\employeeManager\\inventoryapp2\\inventory-feed\\processed\\";
    private static final String ERROR_DIR = "D:\\Java_lit\\employeeManager\\inventoryapp2\\inventory-feed\\error\\";
    public InventoryService(File csvFile) {
        this.csvFile = csvFile;
    }

    @Override
    public void run() {
        processFile(csvFile);
    }
    public Response<String> processFile(File csvFile) {
        logger.info("Processing file: {}", csvFile.getName());
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            boolean success=false;
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean isFirstLine = true;
                
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (parts.length != 4) {
                        throw new FileProcessingException("Invalid CSV format in file: " + csvFile.getName());
                    }

                    Inventory dto = new Inventory(
                            parts[0].trim(),
                            parts[1].trim(),
                            Integer.parseInt(parts[2].trim()),
                            Double.parseDouble(parts[3].trim())
                    );

                    Inventory item = new Inventory(
                            dto.getSku(),
                            dto.getProductName(),
                            dto.getQuantity(),
                            dto.getPrice()
                    );

                    inventoryDAO.insertItem(connection, item);
                }

                connection.commit();
                success=true;
                
            } catch (Exception e) {
                connection.rollback();
                moveFile(csvFile, ERROR_DIR);
                logger.error("Error processing file {}: {}", csvFile.getName(), e.getMessage());
                return new Response<>("FAIL", "Processing failed: " + e.getMessage(), csvFile.getName());
            }
            if(success) {
            	moveFile(csvFile, PROCESSED_DIR);
                logger.info("Successfully processed: {}", csvFile.getName());
                return new Response<>("SUCCESS", "File processed successfully", csvFile.getName());

            }

        } catch (SQLException e) {
            logger.fatal("Database connection error for file {}: {}", csvFile.getName(), e.getMessage());
            return new Response<>("FAIL", "Database connection error", csvFile.getName());
        }
        return new Response<>("FAIL", "Processing failed: " + "Unexpected Error", csvFile.getName());
    }

    private void moveFile(File file, String targetDir) {
        try {
            Files.move(file.toPath(), new File(targetDir + file.getName()).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            logger.debug("Moved file {} to {}", file.getName(), targetDir);
        } catch (Exception e) {
            logger.error("Failed to move file {}: {}", file.getName(), e.getMessage());
        }
    }
}
