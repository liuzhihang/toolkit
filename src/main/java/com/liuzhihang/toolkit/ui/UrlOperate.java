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
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author liuzhihang
 * @date 2020/2/18 20:20
 */
public class UrlOperate extends DialogWrapper {
    private JPanel rootJPanel;
    private JButton cancelButton;
    private JLabel errorJLabel;
    private JButton nextButton;
    private JLabel fileReference;
    private JButton urlDecodeButton;
    private JButton urlEncodeButton;
    private JTextPane textPane;

    private Project project;
    private PsiFile psiFile;
    private Editor editor;
    private PsiClass psiClass;

    public UrlOperate(@Nullable Project project, PsiFile psiFile, Editor editor, PsiClass psiClass) {
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
        setTitle("Url Encode/Decode");
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
        urlDecodeButton.addActionListener(actionEvent -> decodeAction());
        urlEncodeButton.addActionListener(actionEvent -> encodeAction());

        cancelButton.addActionListener(actionEvent -> dispose());

        nextButton.addActionListener(actionEvent -> dispose());
    }

    private void encodeAction() {

        try {
            String text = textPane.getText().trim();
            String writer = URLEncoder.encode(text, "UTF-8");
            textPane.setText(writer);
            errorJLabel.setText("");
        } catch (Exception e) {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("Url encode Failed!");
        }

    }

    private void decodeAction() {

        try {
            String text = textPane.getText().trim();
            String writer = URLDecoder.decode(text, "UTF-8");
            textPane.setText(writer);
            errorJLabel.setText("");
        } catch (Exception e) {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("Url decode Failed!");
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
