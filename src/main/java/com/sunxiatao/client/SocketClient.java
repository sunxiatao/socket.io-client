package com.sunxiatao.client;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.socket.client.IO;
import io.socket.client.Socket;

import javax.swing.*;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class SocketClient {
    private JTextField socketUrl;
    private JButton connectBtn;
    private JPanel container;
    private JPanel connectContent;
    private JLabel urlLabel;
    private JScrollPane scrollPane;
    private JTextArea msgBox;
    private JPanel textPanel;
    private JButton listenerEventBtn;
    private Boolean isOpen = false;
    private final Set<String> eventList = new LinkedHashSet<>();

    private Socket socket;

    private static SocketClient socketClient;

    public Set<String> getEventList() {
        return eventList;
    }

    public SocketClient() {
        mockData();
        connectBtn.addActionListener(e -> {
            if (isOpen) {
                socket.disconnect();
            } else {
                String url = socketUrl.getText();
                if (StrUtil.isNotEmpty(url)) {
                    initSocket(url);
                    refreshListener();
                }
            }
        });
        listenerEventBtn.addActionListener(e -> listenerEvent());
    }

    /**
     * 初始化 SocketIO
     *
     * @param url SocketIO 服务器链接
     */
    private void initSocket(String url) {
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};
            options.reconnection = true;
            socket = IO.socket(url, options);
        } catch (URISyntaxException e) {
            msgBox.append("SocketIO 初始化异常，异常信息：" + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    /**
     * 刷新事件监听
     */
    public void refreshListener() {
        if (socket == null) {
            String url = socketUrl.getText();
            initSocket(url);
        }
        if (socket.connected()) {
            socket.disconnect();
        }
        socket.connect();
        socket.on(Socket.EVENT_CONNECT, args -> {
            StringBuilder sb = new StringBuilder();
            for (String eventKey : eventList) {
                sb.append("【").append(eventKey).append("】");
            }
            if (StrUtil.isEmpty(sb.toString())) {
                sb.append("无");
            }
            msgBox.append("已连接到服务器，订阅事件：" + sb + "\n");
            isOpen = true;
            connectBtn.setText("断开");
            for (String eventKey : eventList) {
                socket.on(eventKey, args1 -> {
                    String message = (String) args1[0];
                    msgBox.append("【" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " " + eventKey + "】收到消息：" + message + "\n");
                });
            }

        }).on(Socket.EVENT_DISCONNECT, args1 -> {
            msgBox.append("已断开服务器\n");
            isOpen = false;
            connectBtn.setText("连接");
        });

    }

    private void listenerEvent() {
        EventListener eventListener = new EventListener(socketClient);
        eventListener.pack();
        eventListener.setLocationRelativeTo(null);
        eventListener.refreshEventListArea(eventList);
        eventListener.setVisible(true);
    }

    /**
     * 模拟数据
     */
    public void mockData() {
        socketUrl.setText("http://192.168.16.134:3000/?userId=456&source=app");
    }

    public static void main(String[] args) {
        socketClient = new SocketClient();
        JFrame frame = new JFrame("SocketIO 客户端");
        frame.setContentPane(socketClient.container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
