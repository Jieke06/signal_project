package com.data_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alerts.AlertGenerator;

/**
 * Manages the centralized storage and historical retrieval of patient health data
 * within the monitoring system.
 * This class serves as a repository for all patient records, indexed efficiently
 * by unique patient identifiers.
 */
public class DataStorage {
    private Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.

    /**
     * Constructs a new instance of {@code DataStorage}, initializing the underlying
     * map repository structure.
     */
    public DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Adds or updates recorded patient health data within the storage system.
     * If the specified patient does not exist, a new {@link Patient} object is instantiated
     * and added to the repository; otherwise, the data is appended to the existing patient's log.
     *
     * @param patientId        the unique identifier of the target patient
     * @param measurementValue the numerical value of the health metric being recorded
     * @param recordType       the type of record category (e.g., "HeartRate", "SpO2")
     * @param timestamp        the time at which the measurement was taken, in milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.get(patientId);
        if (patient == null) {
            patient = new Patient(patientId);
            patientMap.put(patientId, patient);
        }
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves a chronological list of {@link PatientRecord} objects for a specific patient,
     * filtered strictly by a provided time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be retrieved
     * @param startTime the start boundary of the time range, in milliseconds since the Unix epoch
     * @param endTime   the end boundary of the time range, in milliseconds since the Unix epoch
     * @return a list of PatientRecord objects falling within the time window; returns an empty list if none match
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>(); // return an empty list if no patient is found
    }

    /**
     * Retrieves a comprehensive collection containing all patient instances stored in the database.
     *
     * @return a lists containing all tracked {@link Patient} objects
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * The main entry point for the DataStorage application module.
     * Initializes the core database system, loads data streams, and evaluates records against active conditions.
     * * @param args execution arguments passed via the command line interface
     */
    public static void main(String[] args) {
        DataStorage storage = new DataStorage();

        // Example of using DataStorage to retrieve and print records for a patient
        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);
        for (PatientRecord record : records) {
            System.out.println("Record for Patient ID: " + record.getPatientId() +
                    ", Type: " + record.getRecordType() +
                    ", Data: " + record.getMeasurementValue() +
                    ", Timestamp: " + record.getTimestamp());
        }

        // Initialize the AlertGenerator with the storage
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        // Evaluate all patients' data to check for conditions that may trigger alerts
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }
}