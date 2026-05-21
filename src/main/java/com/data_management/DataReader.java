package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads batch data from a specified source (legacy file support).
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to a real-time stream source via network configuration.
     * * @param serverUrl the network URI or host address to connect to
     * @param dataStorage the storage where streaming data will be inserted
     * @throws IOException if connection configuration fails
     */
    void connectToStream(String serverUrl, DataStorage dataStorage) throws IOException;
}