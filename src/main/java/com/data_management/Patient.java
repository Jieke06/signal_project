package com.data_management;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient and manages their medical records within the healthcare monitoring system.
 * This class stores patient-specific data, allowing for the addition and retrieval
 * of medical records based on specified time range criteria.
 */
public class Patient {
    private int patientId;
    private List<PatientRecord> patientRecords;

    /**
     * Constructs a new Patient with a specified unique identifier.
     * Initializes an empty list to store the patient's records.
     *
     * @param patientId the unique identifier for the patient
     */
    public Patient(int patientId) {
        this.patientId = patientId;
        this.patientRecords = new ArrayList<>();
    }

    /**
     * Adds a new medical record to this patient's list of history records.
     * The record is instantiated with the specified measurement value, record type, and timestamp.
     *
     * @param measurementValue the health metric measurement value to store in the record
     * @param recordType       the type of health record (e.g., "HeartRate", "SpO2", "BloodPressure")
     * @param timestamp        the time at which the measurement was taken, in milliseconds since the Unix epoch
     */
    public void addRecord(double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(this.patientId, measurementValue, recordType, timestamp);
        this.patientRecords.add(record);
    }

    /**
     * Retrieves a list of PatientRecord objects for this patient that fall within a specified time range.
     * The results are filtered inclusively based on the start and end times provided.
     *
     * @param startTime the start of the target time range, in milliseconds since the Unix epoch
     * @param endTime   the end of the target time range, in milliseconds since the Unix epoch
     * @return a filtered list of PatientRecord objects that fall within the specified time range
     */
    public List<PatientRecord> getRecords(long startTime, long endTime) {
        List<PatientRecord> filteredRecords = new ArrayList<>();
        for (PatientRecord record : this.patientRecords) {
            if (record.getTimestamp() >= startTime && record.getTimestamp() <= endTime) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }
}