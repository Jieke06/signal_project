package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     *
     * @param dataStorage the data storage system
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions are met.
     * Covers core metrics (HeartRate, SpO2, BloodPressure) and handles edge cases.
     *
     * @param patient the patient data to evaluate
     */
    public void evaluateData(Patient patient) {
        if (patient == null) {
            return;
        }

        String patientIdStr = String.valueOf(patient.getRecords(0, Long.MAX_VALUE).isEmpty() ?
                "" : patient.getRecords(0, Long.MAX_VALUE).get(0).getPatientId());

        // 1. Evaluate Heart Rate (Normal range: 60 - 100 bpm)
        List<PatientRecord> heartRateRecords = patient.getRecords(0, Long.MAX_VALUE);
        for (PatientRecord record : heartRateRecords) {
            if ("HeartRate".equalsIgnoreCase(record.getRecordType())) {
                double val = record.getMeasurementValue();
                if (val < 60 || val > 100) {
                    triggerAlert(new Alert(patientIdStr, "Critical Heart Rate: " + val + " bpm", record.getTimestamp()));
                }
            }
        }

        // 2. Evaluate SpO2 (Critical if drops below 92%)
        for (PatientRecord record : heartRateRecords) {
            if ("SpO2".equalsIgnoreCase(record.getRecordType())) {
                double val = record.getMeasurementValue();
                if (val < 92.0) {
                    triggerAlert(new Alert(patientIdStr, "Low Blood Oxygen (SpO2): " + val + "%", record.getTimestamp()));
                }
            }
        }

        // 3. Evaluate Blood Pressure (Systolic > 140 or < 90, Diastolic > 90 or < 60)
        for (PatientRecord record : heartRateRecords) {
            if ("SystolicPressure".equalsIgnoreCase(record.getRecordType())) {
                double val = record.getMeasurementValue();
                if (val > 140 || val < 90) {
                    triggerAlert(new Alert(patientIdStr, "Abnormal Systolic Pressure: " + val + " mmHg", record.getTimestamp()));
                }
            }
            if ("DiastolicPressure".equalsIgnoreCase(record.getRecordType())) {
                double val = record.getMeasurementValue();
                if (val > 90 || val < 60) {
                    triggerAlert(new Alert(patientIdStr, "Abnormal Diastolic Pressure: " + val + " mmHg", record.getTimestamp()));
                }
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system and logs it to console.
     *
     * @param alert the alert object containing details
     */
    public void triggerAlert(Alert alert) {
        System.out.println(" [ALERT TRIGGERED] Patient ID: " + alert.getPatientId()
                + " | Condition: " + alert.getCondition()
                + " | Timestamp: " + alert.getTimestamp());
    }
}