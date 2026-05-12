package com.data_management;

/**
 * Represents a single immutable record of patient health data at a specific point in time.
 * This class stores all necessary details for a single observation or measurement
 * taken from a patient, including the type of record (such as ECG, Blood Pressure, SpO2),
 * the measurement value, and the precise timestamp when the measurement was captured.
 */
public class PatientRecord {
    private int patientId;
    private String recordType; // Example: ECG, BloodPressure, SpO2, etc.
    private double measurementValue; // Example: 75.0 (for heart rate)
    private long timestamp;

    /**
     * Constructs a new patient record with the specified clinical details.
     * * @param patientId        the unique identifier representing the specific patient
     * @param measurementValue the numerical value of the recorded medical measurement
     * @param recordType       the type of measurement category (e.g., "HeartRate", "SpO2", "BloodPressure")
     * @param timestamp        the exact time at which the measurement was recorded,
     * in milliseconds since the Unix epoch (January 1, 1970, 00:00:00 GMT)
     */
    public PatientRecord(int patientId, double measurementValue, String recordType, long timestamp) {
        this.patientId = patientId;
        this.measurementValue = measurementValue;
        this.recordType = recordType;
        this.timestamp = timestamp;
    }

    /**
     * Returns the unique patient identifier associated with this medical record.
     * * @return the unique integer representing the patient ID
     */
    public int getPatientId() {
        return patientId;
    }

    /**
     * Returns the numerical measurement value stored within this record.
     * * @return the recorded health metric value as a double
     */
    public double getMeasurementValue() {
        return measurementValue;
    }

    /**
     * Returns the precise timestamp indicating when this record was captured.
     * * @return the epoch timestamp in milliseconds representing the record time
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the descriptive category or type of this record (e.g., "HeartRate", "SpO2").
     * * @return the string representing the specific type of health record
     */
    public String getRecordType() {
        return recordType;
    }
}