package com.data_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alerts.AlertGenerator;

/**
 * Singleton implementation of DataStorage to ensure a centralized, unified telemetry repository.
 */
public class DataStorage {
    // 经典的单例：饿汉式/双重校验锁。这里采用静态内部类形式，既保证线程安全又延迟加载
    private static class InstanceHolder {
        private static final DataStorage INSTANCE = new DataStorage();
    }

    private Map<Integer, Patient> patientMap;

    // 必须将构造函数设为 private，防止外部 new 出来
    private DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Retrieves the global unique instance of DataStorage.
     */
    public static DataStorage getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Clear all records (Useful for resetting state between unit tests).
     */
    public void clear() {
        this.patientMap.clear();
    }

    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.computeIfAbsent(patientId, Patient::new);
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>();
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    public static void main(String[] args) {
        // 使用单例实例
        DataStorage storage = DataStorage.getInstance();
        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }
}