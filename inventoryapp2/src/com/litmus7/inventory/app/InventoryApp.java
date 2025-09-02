package com.litmus7.inventory.app;

import com.litmus7.inventory.controller.InventoryController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InventoryApp {
    private static final Logger logger = LogManager.getLogger(InventoryApp.class);

    public static void main(String[] args) {
        logger.info("Starting Inventory Feed Processor phase 2");
        InventoryController controller = new InventoryController();
        controller.processAllFiles();
        
    }
}
