package com.liuzhihang.toolkit.ui;

import com.google.gson.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.liuzhihang.toolkit.utils.GsonFormatUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * JsonFormat 窗口设置
 *
 * @author liuzhihang
 * @date 2019/5/8 12:40
 */
public class JsonFormat extends DialogWrapper {

    private JPanel rootJPanel;
    private JButton formatButton;
    private JTextPane textPane;
    private JButton removeSpecialCharsButton;
    private JLabel errorJLabel;
    private JButton cancelButton;
    private JButton nextButton;

    public JsonFormat(@Nullable Project project) {
        super(project);
        init();
        setTitle("JsonFormat");
        getRootPane().setDefaultButton(nextButton);
        // TODO: 2019/5/9 nextButton 以后开发
        nextButton.setEnabled(false);
        startListener();
    }

    /**
     * 监听行为
     */
    private void startListener() {

        // 监听formatButton按钮
        formatButton.addActionListener(actionEvent -> {

            try {
                String text = textPane.getText().trim();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser jsonParser = new JsonParser();
                if (text.startsWith("{") && text.endsWith("}")) {

                    JsonObject jsonObject = jsonParser.parse(text).getAsJsonObject();
                    String writer = GsonFormatUtil.gsonFormat(gson, jsonObject);
                    textPane.setText(writer);
                    errorJLabel.setText("");
                } else if (text.startsWith("[") && text.endsWith("]")) {

                    JsonArray jsonArray = jsonParser.parse(text).getAsJsonArray();
                    String writer = GsonFormatUtil.gsonFormat(gson, jsonArray);
                    textPane.setText(writer);
                    errorJLabel.setText("");
                } else {
                    errorJLabel.setForeground(JBColor.RED);
                    errorJLabel.setText("Please enter the correct Json string!");
                }
            } catch (Exception e) {
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("JsonFormat Failed!");
            }

        });
        // 去除转义符号
        removeSpecialCharsButton.addActionListener(actionEvent -> {
            String text = textPane.getText();
            String resultText = text.replace("\\", "");
            textPane.setText(resultText);
        });

    }


    @NotNull
    @Override
    protected Action[] createActions() {
        // 覆盖默认的 确认和撤销
        return new Action[]{};
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootJPanel;
    }
}
