package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class AlertGeneratorTest {
    private DataStorage storage;
    private AlertGenerator alertGenerator;
    private List<Alert> triggeredAlerts;

    @BeforeEach
    public void setUp() {
        // 测试单例模式
        storage = DataStorage.getInstance();
        storage.clear();

        triggeredAlerts = new ArrayList<>();
        alertGenerator = new AlertGenerator(storage) {
            @Override
            public void triggerAlert(Alert alert) {
                super.triggerAlert(alert);
                triggeredAlerts.add(alert);
            }
        };
    }

    @Test
    public void testSingletonPattern() {
        DataStorage s1 = DataStorage.getInstance();
        DataStorage s2 = DataStorage.getInstance();
        assertSame(s1, s2, "DataStorage must follow the Singleton Pattern");
    }

    @Test
    public void testFactoryPattern() {
        AlertFactory bpFactory = new BloodPressureAlertFactory();
        Alert alert = bpFactory.createAlert("1", "High", 123456L);
        assertTrue(alert.getCondition().contains("[BP]"), "Factory method should inject BP tag");

        AlertFactory oxygenFactory = new BloodOxygenAlertFactory();
        Alert oxAlert = oxygenFactory.createAlert("2", "Low", 123456L);
        assertTrue(oxAlert.getCondition().contains("[SpO2]"), "Factory method should inject SpO2 tag");
    }

    @Test
    public void testDecoratorPattern() {
        Alert simpleAlert = new Alert("001", "Anomalous State", System.currentTimeMillis());
        Alert priorityAlert = new PriorityAlertDecorator(simpleAlert, "URGENT");
        Alert fullyDecorated = new RepeatedAlertDecorator(priorityAlert);

        assertTrue(fullyDecorated.getCondition().contains("[PRIORITY: URGENT]"));
        assertTrue(fullyDecorated.getCondition().contains("[REPEATED NOTIFICATION SENT]"));
    }

    @Test
    public void testStrategiesViaGenerator() {
        int patientId = 77;
        storage.addPatientData(patientId, 150.0, "HeartRate", System.currentTimeMillis());
        storage.addPatientData(patientId, 85.0, "SpO2", System.currentTimeMillis());

        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertFalse(triggeredAlerts.isEmpty(), "Strategies should execute and pick up anomalies.");
    }
}