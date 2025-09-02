package com.litmus7.inventory.controller;

import com.litmus7.inventory.service.InventoryService;
import com.litmus7.inventory.util.Response;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {
    private static final Logger logger = LogManager.getLogger(InventoryController.class);
   // private final InventoryService service = new InventoryService();
    private static final String INPUT_DIR = "inventory-feed/input"; 

    public void processAllFiles() {
        File inputFolder = new File(INPUT_DIR);
        File[] files = inputFolder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) {
            logger.warn("No files found in input folder.");
            return;
        }
      
        List<Thread> threads = new ArrayList<>();

        for (File file : files) {
            Thread thread = new Thread(new InventoryService(file));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt(); 
            }
        }

        logger.info("All files processed"); 
    }
       
        	
        


    }
