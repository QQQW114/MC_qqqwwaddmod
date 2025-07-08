package com.qwsadd.qwsaddmod.client;

public class ClientPoopLevelData {
    // 1. 定义一个静态变量，用于在客户端存储便意值
    private static int clientPoopLevel;

    // 2. 定义一个setter方法，让网络数据包可以更新这个值
    public static void setClientPoopLevel(int poopLevel) {
        ClientPoopLevelData.clientPoopLevel = poopLevel;
    }

    // 3. 定义一个getter方法，让GUI覆盖层可以读取这个值
    // 这就是我们之前缺少的那个方法！
    public static int getClientPoopLevel() {
        return clientPoopLevel;
    }
}