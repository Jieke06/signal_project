package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    @Override
    public List<Alert> checkAlerts(Patient patient) {
        List<Alert> list = new ArrayList<>();
        if (patient == null) return list;

        String pid = "";
        if (!patient.getRecords(0, Long.MAX_VALUE).isEmpty()) {
            pid = String.valueOf(patient.getRecords(0, Long.MAX_VALUE).get(0).getPatientId());
        }

        for (PatientRecord r : patient.getRecords(0, Long.MAX_VALUE)) {
            if ("SystolicPressure".equalsIgnoreCase(r.getRecordType())) {
                double val = r.getMeasurementValue();
                if (val > 140 || val < 90) {
                    list.add(new Alert(pid, "Abnormal Systolic Pressure: " + val + " mmHg", r.getTimestamp()));
                }
            }
            if ("DiastolicPressure".equalsIgnoreCase(r.getRecordType())) {
                double val = r.getMeasurementValue();
                if (val > 90 || val < 60) {
                    list.add(new Alert(pid, "Abnormal Diastolic Pressure: " + val + " mmHg", r.getTimestamp()));
                }
            }
        }
        return list;
    }
}