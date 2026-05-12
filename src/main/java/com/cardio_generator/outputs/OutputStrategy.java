package com.cardio_generator.outputs;

/**
 * The {@code OutputStrategy} interface defines a generic contract for outputting
 * simulated patient health metrics to various destinations (e.g., console, file, or network).
 * Any output handler implementation must implement this interface.
 */
public interface OutputStrategy {

    /**
     * Outputs a specific health measurement record for a patient.
     *
     * @param patientId the unique identifier representing the patient
     * @param timestamp the exact time the record was generated, in milliseconds since the Unix epoch
     * @param label     the type of health metric being recorded (e.g., "HeartRate", "SpO2")
     * @param data      the actual string value or data stream generated for the metric
     */
    void output(int patientId, long timestamp, String label, String data);
}