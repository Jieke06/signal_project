package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.data_management.DataStorage;
import com.data_management.WebSocketClientReader;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.util.List;

public class WebSocketClientReaderTest {
    private DataStorage storage;
    private WebSocketClientReader reader;

    @BeforeEach
    public void setUp() throws IOException {
        storage = DataStorage.getInstance();
        storage.clear();
        reader = new WebSocketClientReader();
        // 初始化网络配置，指向虚拟测试桩端口
        reader.connectToStream("ws://localhost:8085", storage);
    }

    @Test
    public void testReceiveAndParseValidStream() {
        // 模拟正常实时流信号消息
        String validMessage = "12, 1714376789050, HeartRate, 115.0";

        // 诱发客户端处理流
        assertDoesNotThrow(() -> reader.getClient().onMessage(validMessage));

        // 验证系统是否无延迟响应，完成了从网络包到单例数据库的流式写入
        List<PatientRecord> records = storage.getRecords(12, 0, Long.MAX_VALUE);
        assertEquals(1, records.size(), "Valid streaming data should be recorded.");
        assertEquals(115.0, records.get(0).getMeasurementValue());
        assertEquals("HeartRate", records.get(0).getRecordType());
    }

    @Test
    public void testRobustnessAgainstCorruptedFrames() {
        // 极端边缘情况：缺失字段的损坏帧
        String badFrame = "12, 1714376789050, SpO2";
        // 极端边缘情况：无法进行数值转换的脏数据
        String alphaFrame = "12, abc, SpO2, ninety-two";
        // 极端边缘情况：完全空白的信息帧
        String emptyFrame = "   ";

        // 验证系统具备极强的容错防御机制，面临崩溃数据流时，优雅捕获而不崩溃退出
        assertDoesNotThrow(() -> reader.getClient().onMessage(badFrame));
        assertDoesNotThrow(() -> reader.getClient().onMessage(alphaFrame));
        assertDoesNotThrow(() -> reader.getClient().onMessage(emptyFrame));

        // 终极断言：单例数据中心没有任何脏数据被非法写入，依旧保持绝对纯净
        assertTrue(storage.getAllPatients().isEmpty(), "Storage must remain empty when filtered by corrupted network stream payloads.");
    }
}
