package com.liuzhihang.toolkit.ui;

import com.google.gson.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.ui.JBColor;
import com.liuzhihang.toolkit.utils.GsonFormatUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

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
    private Project project;
    private PsiFile psiFile;
    private Editor editor;

    public JsonFormat(@Nullable Project project, PsiFile psiFile, Editor editor) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.psiFile = psiFile;
        this.editor = editor;
        init();
        setTitle("JsonFormat");
        getRootPane().setDefaultButton(nextButton);
        // TODO: 2019/5/9 nextButton 以后开发
        nextButton.setEnabled(true);
        startListener();
    }

    /**
     * 监听行为
     */
    private void startListener() {

        // 监听formatButton按钮
        formatButton.addActionListener(actionEvent -> formatAction());
        // 去除转义符号
        removeSpecialCharsButton.addActionListener(actionEvent -> {
            String text = textPane.getText();
            String resultText = text.replace("\\", "");
            textPane.setText(resultText);
        });

        cancelButton.addActionListener(actionEvent -> dispose());

        nextButton.addActionListener(actionEvent -> nextAction());
    }

    private void textPaneAction() {

    }

    /**
     * next 按钮相关操作
     */
    private void nextAction() {
        try {
            String text = textPane.getText().trim();

            JsonParser jsonParser = new JsonParser();
            if (text.startsWith("{") && text.endsWith("}")) {
                JsonObject jsonObject = jsonParser.parse(text).getAsJsonObject();
                if (psiFile instanceof PsiJavaFile) {
                    cancelButton.doClick();
                    DialogWrapper dialog = new FieldsEditor(project, psiFile, editor, jsonObject);
                    dialog.show();
                } else {
                    errorJLabel.setForeground(JBColor.RED);
                    errorJLabel.setText("This is not a Java file");
                }

            } else if (text.startsWith("[") && text.endsWith("]")) {
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("JsonArray is not supported");
            } else {
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("Please enter the correct Json string!");
            }
        } catch (Exception e) {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("JsonFormat Failed!");
        }
    }

    /**
     * json format 相关操作
     */
    private void formatAction() {
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
