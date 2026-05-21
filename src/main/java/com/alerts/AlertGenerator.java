package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import java.util.ArrayList;
import java.util.List;

public class AlertGenerator {
    private DataStorage dataStorage;
    private List<AlertStrategy> strategies;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.strategies = new ArrayList<>();
        // 动态加载策略
        this.strategies.add(new HeartRateStrategy());
        this.strategies.add(new BloodOxygenStrategy());
        this.strategies.add(new BloodPressureStrategy());
    }

    public void evaluateData(Patient patient) {
        if (patient == null) return;

        // 使用策略模式执行评估
        for (AlertStrategy strategy : strategies) {
            List<Alert> rawAlerts = strategy.checkAlerts(patient);
            for (Alert alert : rawAlerts) {
                triggerAlert(alert);
            }
        }
    }

    public void triggerAlert(Alert alert) {
        System.out.println(" [ALERT] Patient ID: " + alert.getPatientId()
                + " | Condition: " + alert.getCondition());
    }
}