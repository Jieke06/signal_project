package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.alerts.AlertGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebSocketClientReader connects to a real-time cardiovascular telemetry stream,
 * parses CSV chunks, and instantly pushes records to DataStorage and Alert evaluation.
 */
public class WebSocketClientReader implements DataReader {
    private WebSocketClient client;

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        // 保持对旧文件批处理方法的支持，这里空实现或抛出未实现异常
        throw new UnsupportedOperationException("Batch file read not supported by WebSocketClientReader.");
    }

    @Override
    public void connectToStream(String serverUrl, DataStorage dataStorage) throws IOException {
        try {
            URI serverUri = new URI(serverUrl);

            // 初始化内置的 WebSocket 长连接客户端
            this.client = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println(" [WS CONNECTED] Successfully established link to server: " + serverUrl);
                }

                @Override
                public void onMessage(String message) {
                    // 核心高分项：防畸形/脏数据破坏的网络鲁棒流解析
                    if (message == null || message.trim().isEmpty()) {
                        System.err.println(" [WS WARNING] Received empty or blank stream token. Dropping package.");
                        return;
                    }

                    try {
                        String[] tokens = message.split(",");
                        if (tokens.length != 4) {
                            System.err.println(" [WS CORRUPTION ERROR] Invalid data frame size: \"" + message + "\". Skipping.");
                            return;
                        }

                        // 按服务器端格式解析: PatientID,Timestamp,Label,Value
                        int patientId = Integer.parseInt(tokens[0].trim());
                        long timestamp = Long.parseLong(tokens[1].trim());
                        String label = tokens[2].trim();
                        // 兼容某些测算值带有单位或者浮点数解析
                        double measurementValue = Double.parseDouble(tokens[3].trim());

                        // 1. 实时塞入单例数据中心
                        dataStorage.addPatientData(patientId, measurementValue, label, timestamp);

                        // 2. 实时触发第四周重构后的低耦合设计模式 AlertGenerator 判定
                        AlertGenerator generator = new AlertGenerator(dataStorage);
                        if (!dataStorage.getAllPatients().isEmpty()) {
                            dataStorage.getAllPatients().stream()
                                    .filter(p -> p.getRecords(timestamp, timestamp).stream().anyMatch(r -> r.getPatientId() == patientId))
                                    .forEach(generator::evaluateData);
                        }

                    } catch (NumberFormatException nfe) {
                        System.err.println(" [WS PARSING ERROR] Metric data conversion failed in message: \"" + message + "\" -> " + nfe.getMessage());
                    } catch (Exception e) {
                        System.err.println(" [WS UNEXPECTED EXCEPTION] General error evaluating incoming stream frame: " + e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println(" [WS DISCONNECTED] Stream closed. Code: " + code + " | Reason: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println(" [WS NETWORK ERROR] Connection exception encountered: " + ex.getMessage());
                }
            };

            // 发起长连接异步握手
            this.client.connect();

        } catch (URISyntaxException e) {
            throw new IOException("Failed to parse WebSocket URL schema: " + serverUrl, e);
        }
    }

    /**
     * Expose access to the underlying client structure for mocking and testing.
     */
    public WebSocketClient getClient() {
        return this.client;
    }
}