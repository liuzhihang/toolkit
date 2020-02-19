package com.liuzhihang.toolkit.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author liuzhihang
 * @date 2020/2/18 16:48
 */
public class Base64Operate extends DialogWrapper {
    private JPanel rootJPanel;
    private JButton cancelButton;
    private JLabel errorJLabel;
    private JButton nextButton;
    private JTextPane textPane;
    private JLabel fileReference;
    private JButton decodeButton;
    private JButton encodeButton;

    private Project project;
    private PsiFile psiFile;
    private Editor editor;
    private PsiClass psiClass;

    public Base64Operate(@Nullable Project project, PsiFile psiFile, Editor editor, PsiClass psiClass) {
        super(project, true, DialogWrapper.IdeModalityType.MODELESS);
        this.project = project;
        this.psiFile = psiFile;
        this.editor = editor;
        this.psiClass = psiClass;

        if (psiClass != null) {
            fileReference.setText(psiClass.getQualifiedName());
        } else if (psiFile != null) {
            fileReference.setText(psiFile.getName());
        }

        init();
        setTitle("Base64");
        getRootPane().setDefaultButton(nextButton);
        nextButton.setEnabled(true);
        nextButton.setText("OK");
        startListener();
    }

    /**
     * 监听行为
     */
    private void startListener() {

        // 监听decodeButton按钮
        decodeButton.addActionListener(actionEvent -> decodeAction());
        encodeButton.addActionListener(actionEvent -> encodeAction());

        cancelButton.addActionListener(actionEvent -> dispose());

        nextButton.addActionListener(actionEvent -> dispose());
    }

    private void encodeAction() {

        try {
            String text = textPane.getText().trim();
            String writer = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
            textPane.setText(writer);
            errorJLabel.setText("");
        } catch (Exception e) {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("Base64 encode Failed!");
        }

    }

    private void decodeAction() {

        try {
            String text = textPane.getText().trim();
            byte[] decode = Base64.getDecoder().decode(text);
            String writer = new String(decode, StandardCharsets.UTF_8);
            textPane.setText(writer);
            errorJLabel.setText("");
        } catch (Exception e) {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("Base64 decode Failed!");
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
