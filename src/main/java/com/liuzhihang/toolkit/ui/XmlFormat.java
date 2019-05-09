package com.liuzhihang.toolkit.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

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
        super(true);
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

                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(text));
                Document document = builder.parse(inputSource);

                OutputFormat format = new OutputFormat(document);
                format.setLineWidth(80);
                format.setIndenting(true);
                format.setIndent(2);
                Writer out = new StringWriter();
                XMLSerializer serializer = new XMLSerializer(out, format);
                serializer.serialize(document);
                textPane.setText(out.toString());
                errorJLabel.setText("");
            } catch (Exception e) {
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("JsonFormat Failed!");
            }

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
