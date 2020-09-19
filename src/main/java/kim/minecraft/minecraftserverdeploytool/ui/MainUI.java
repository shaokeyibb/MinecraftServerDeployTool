package kim.minecraft.minecraftserverdeploytool.ui;

import kim.minecraft.minecraftserverdeploytool.guide.ServersManage;
import kotlin.jvm.internal.ArrayIteratorKt;

import javax.swing.*;
import java.awt.*;

public class MainUI {

    static RunningManage manage;

    public static void run() {
        EventQueue.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setTitle("Minecraft Server Deploy Tool - UI Mode");
            frame.setResizable(false);
            frame.setLocationByPlatform(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            manage = new RunningManage(frame);
        });
    }

    public static class MainFrame extends JFrame {
        public JLabel startLabel;
        public JComboBox<String> selector;
        public JPanel selectorPanel;
        public JButton deployButton;
        public JPanel buttonPanel;
        public JProgressBar progressBar;

        public MainFrame() {
            startLabel = new JLabel("Please select the server core you want to deploy:");

            selector = new JComboBox<>();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            ArrayIteratorKt.iterator(ServersManage.Servers.values()).forEachRemaining(e -> model.addElement(e.name()));
            selector.setModel(model);

            selectorPanel = new JPanel();
            selectorPanel.add(startLabel);
            selectorPanel.add(selector);

            deployButton = new JButton("DEPLOY");
            deployButton.addActionListener((event) -> {
                String selected = selector.getItemAt(selector.getSelectedIndex());
                int response = JOptionPane.showConfirmDialog(selectorPanel, "continue to deploy " + selected + "?", "continue?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == 0) {
                    manage.run(selected);
                }
            });

            buttonPanel = new JPanel();
            buttonPanel.add(deployButton);

            add(selectorPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.AFTER_LAST_LINE);

            pack();
        }

    }
}
