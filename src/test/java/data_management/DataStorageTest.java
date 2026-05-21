package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    @BeforeEach
    void setUp() {
        // 每次测试执行前，获取单例实例并清空数据，确保测试环境干净且互相独立
        DataStorage.getInstance().clear();
    }

    @Test
    void testAddAndGetRecords() {
        // 改用单例模式获取全局唯一的数据仓库实例
        DataStorage storage = DataStorage.getInstance();

        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // 验证是否成功检索到 2 条记录
        assertEquals(100.0, records.get(0).getMeasurementValue()); // 验证第一条记录的值
    }
}