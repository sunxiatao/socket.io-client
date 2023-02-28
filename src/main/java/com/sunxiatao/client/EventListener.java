package com.sunxiatao.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashSet;
import java.util.Set;

public class EventListener extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel desc;
    private JTextField eventInput;
    private JButton addBtn;
    private JPanel eventListPanel;
    private JTextArea eventListArea;
    private final Set<String> eventListTemp = new LinkedHashSet<>();

    private final SocketClient socketClient;

    public EventListener(SocketClient socketClient) {
        this.socketClient = socketClient;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        addBtn.addActionListener(e -> {
            String text = eventInput.getText();
            if (StrUtil.isNotEmpty(text)) {
                eventInput.setText("");
                refreshEventListArea(text);
            }
        });
    }

    private void onOK() {
        // add your code here
        socketClient.getEventList().addAll(eventListTemp);
        socketClient.refreshListener();
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void refreshEventListArea(Set<String> temp) {
        eventListTemp.addAll(temp);
        eventListArea.setText(CollectionUtil.join(eventListTemp, ", "));
    }

    public void refreshEventListArea(String temp) {
        eventListTemp.add(temp);
        eventListArea.setText(CollectionUtil.join(eventListTemp, ", "));
    }

}
