package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class BloodOxygenStrategy implements AlertStrategy {
    @Override
    public List<Alert> checkAlerts(Patient patient) {
        List<Alert> list = new ArrayList<>();
        if (patient == null) return list;
        String pid = String.valueOf(patient.getRecords(0, Long.MAX_VALUE).isEmpty() ? "" : patient.getRecords(0, Long.MAX_VALUE).get(0).getPatientId());

        for (PatientRecord r : patient.getRecords(0, Long.MAX_VALUE)) {
            if ("SpO2".equalsIgnoreCase(r.getRecordType())) {
                double val = r.getMeasurementValue();
                if (val < 92.0) {
                    list.add(new Alert(pid, "Low Blood Oxygen (SpO2): " + val + "%", r.getTimestamp()));
                }
            }
        }
        return list;
    }
}