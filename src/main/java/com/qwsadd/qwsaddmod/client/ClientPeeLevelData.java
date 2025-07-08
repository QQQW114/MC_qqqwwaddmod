// 文件路径: com/qwsadd/qwsaddmod/client/ClientPeeLevelData.java
package com.qwsadd.qwsaddmod.client;

// 【修复】这是一个全新的文件
public class ClientPeeLevelData {
    private static int clientPeeLevel;

    public static void setClientPeeLevel(int peeLevel) {
        ClientPeeLevelData.clientPeeLevel = peeLevel;
    }

    public static int getClientPeeLevel() {
        return clientPeeLevel;
    }
}
