package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

// Fixed: Class name must match the file name and follow UpperCamelCase.
public class FileOutputStrategy implements OutputStrategy {

    // Fixed: Renamed from 'BaseDirectory' to 'baseDirectory' to adhere to lowerCamelCase naming convention.
    private String baseDirectory;

    // Fixed: Renamed from 'file_map' to 'fileMap' to eliminate underscores and follow lowerCamelCase.
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    public FileOutputStrategy(String baseDirectory) {
        // Fixed: Updated to match the new lowerCamelCase field variable.
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            // Fixed: Updated to use the correct camelCase variable 'baseDirectory'.
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }

        // Set the FilePath variable
        // Fixed: Renamed from 'FilePath' to 'filePath' to follow lowerCamelCase, and updated 'file_map' to 'fileMap'.
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        // Fixed: Updated parameter to use the lowerCamelCase 'filePath'.
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            // Fixed: Updated to use the lowerCamelCase 'filePath'.
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}