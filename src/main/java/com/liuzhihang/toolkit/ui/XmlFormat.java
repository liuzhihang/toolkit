package com.liuzhihang.toolkit.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author liuzhihang
 * @date 2019/5/9 17:36
 */
public class XmlFormat extends DialogWrapper {
    private JPanel rootJPanel;
    private JScrollPane scrollPane;
    private JButton formatButton;

    private JLabel errorJLabel;
    private JButton nextButton;
    private JButton cancelButton;
    private JTextPane textPane;

    public XmlFormat(@Nullable Project project) {
        super(project, true, IdeModalityType.MODELESS);
        init();
        setTitle("XmlFormat");

        getRootPane().setDefaultButton(nextButton);
        // TODO: 2019/5/9 nextButton 以后开发
        nextButton.setEnabled(false);

        startListener();
    }

    private void startListener() {

        formatButton.addActionListener(actionEvent -> {

            try {
                String text = textPane.getText().trim();

                SAXReader reader = new SAXReader();
                StringReader in = new StringReader(text);
                Document doc = reader.read(in);
                OutputFormat formater = OutputFormat.createPrettyPrint();
                formater.setNewLineAfterDeclaration(false);
                formater.setIndent(true);
                formater.setIndentSize(4);
                StringWriter out = new StringWriter();
                XMLWriter writer = new XMLWriter(out, formater);
                writer.write(doc);
                writer.close();
                textPane.setText(out.toString());
                errorJLabel.setText("");
            } catch (Exception e) {
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("JsonFormat Failed!");
            }

        });
        cancelButton.addActionListener(actionEvent -> dispose());

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
