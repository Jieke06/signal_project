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
        storage = new DataStorage();
        triggeredAlerts = new ArrayList<>();

        // 覆写 triggerAlert 以便在测试中捕获断言
        alertGenerator = new AlertGenerator(storage) {
            @Override
            public void triggerAlert(Alert alert) {
                super.triggerAlert(alert);
                triggeredAlerts.add(alert);
            }
        };
    }

    @Test
    public void testNormalVitalsNoAlert() {
        int patientId = 99;
        storage.addPatientData(patientId, 75.0, "HeartRate", System.currentTimeMillis());
        storage.addPatientData(patientId, 98.0, "SpO2", System.currentTimeMillis());
        storage.addPatientData(patientId, 120.0, "SystolicPressure", System.currentTimeMillis());
        storage.addPatientData(patientId, 80.0, "DiastolicPressure", System.currentTimeMillis());

        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertTrue(triggeredAlerts.isEmpty(), "Normal vitals should not trigger any alerts.");
    }

    @Test
    public void testHeartRateEdgeCases() {
        int patientId = 101;
        // 极端高心率
        storage.addPatientData(patientId, 140.0, "HeartRate", System.currentTimeMillis());
        // 极端低心率
        storage.addPatientData(patientId, 45.0, "HeartRate", System.currentTimeMillis());

        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(2, triggeredAlerts.size(), "Should trigger two alerts for abnormal heart rates.");
        assertTrue(triggeredAlerts.get(0).getCondition().contains("Critical Heart Rate"));
    }

    @Test
    public void testLowSpO2EdgeCase() {
        int patientId = 102;
        // 危险血氧边界
        storage.addPatientData(patientId, 89.0, "SpO2", System.currentTimeMillis());

        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(1, triggeredAlerts.size());
        assertTrue(triggeredAlerts.get(0).getCondition().contains("Low Blood Oxygen"));
    }

    @Test
    public void testBloodPressureEdgeCases() {
        int patientId = 103;
        // 高收缩压与低舒张压边界
        storage.addPatientData(patientId, 155.0, "SystolicPressure", System.currentTimeMillis());
        storage.addPatientData(patientId, 50.0, "DiastolicPressure", System.currentTimeMillis());

        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(2, triggeredAlerts.size(), "Should trigger alerts for both systolic and diastolic anomalies.");
    }

    @Test
    public void testNullAndEmptyPatientEdgeCases() {
        // 极端用例：传入 null
        assertDoesNotThrow(() -> alertGenerator.evaluateData(null),
                "Evaluating null patient should fail-safe and not throw exception.");

        // 极端用例：患者没有任何数据记录
        Patient emptyPatient = new Patient(888);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(emptyPatient),
                "Evaluating patient with no history records should fail-safe gracefully.");
        assertTrue(triggeredAlerts.isEmpty());
    }
}