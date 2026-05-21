package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {
    @Override
    public List<Alert> checkAlerts(Patient patient) {
        List<Alert> list = new ArrayList<>();
        if (patient == null) return list;
        String pid = String.valueOf(patient.getRecords(0, Long.MAX_VALUE).isEmpty() ? "" : patient.getRecords(0, Long.MAX_VALUE).get(0).getPatientId());

        for (PatientRecord r : patient.getRecords(0, Long.MAX_VALUE)) {
            if ("HeartRate".equalsIgnoreCase(r.getRecordType())) {
                double val = r.getMeasurementValue();
                if (val < 60 || val > 100) {
                    list.add(new Alert(pid, "Critical Heart Rate: " + val + " bpm", r.getTimestamp()));
                }
            }
        }
        return list;
    }
}