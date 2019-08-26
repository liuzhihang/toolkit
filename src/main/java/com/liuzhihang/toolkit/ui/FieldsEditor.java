package com.liuzhihang.toolkit.ui;

import com.google.gson.JsonObject;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.source.tree.JavaTreeGenerator;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author liuzhihang
 * @date 2019/8/22 7:12 下午
 */
public class FieldsEditor extends DialogWrapper {

    private JLabel classFullName;
    private JScrollPane fieldsText;
    private JButton cancelButton;
    private JButton okButton;
    private JPanel rootJPanel;
    private JLabel errorJLabel;

    private Project project;
    private PsiFile psiFile;
    private Editor editor;
    private JsonObject jsonObject;

    public FieldsEditor(Project project, PsiFile psiFile, Editor editor, JsonObject jsonObject) {
        super(project, true, DialogWrapper.IdeModalityType.MODELESS);
        this.project = project;
        this.psiFile = psiFile;
        this.editor = editor;
        this.jsonObject = jsonObject;
        init();
        setTitle("JsonFormat");
        getRootPane().setDefaultButton(okButton);
        startListener();
    }

    private void startListener() {

        classFullNameAction();

        fieldsTextAction();
    }

    private void fieldsTextAction() {

        TreeTableModel treeTableModel = buildTreeTableModel();

        JTable fieldTable = new JXTreeTable(treeTableModel);
        // JXTreeTable
        fieldsText.setViewportView(fieldTable);

    }

    /**
     * 创建树表
     * @return
     */
    private TreeTableModel buildTreeTableModel() {

        DefaultMutableTreeTableNode mutableTreeTableNode = new DefaultMutableTreeTableNode();



        DefaultTreeTableModel tableModel = new DefaultTreeTableModel();


        return null;
    }


    private void classFullNameAction() {

        if (psiFile instanceof PsiJavaFile) {

            PsiJavaFile psiJavaFile = (PsiJavaFile) this.psiFile;

            classFullName.setText(psiJavaFile.getPackageName());
        } else {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("This is not a Java file");
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
