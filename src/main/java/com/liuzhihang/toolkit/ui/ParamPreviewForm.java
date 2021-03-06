package com.liuzhihang.toolkit.ui;

import com.google.gson.JsonObject;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.WindowMoveListener;
import com.intellij.util.ui.JBUI;
import com.liuzhihang.toolkit.ToolkitBundle;
import com.liuzhihang.toolkit.config.Settings;
import com.liuzhihang.toolkit.utils.ParamDataUtils;
import com.liuzhihang.toolkit.utils.SettingsHandlerUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liuzhihang
 * @date 2021/5/27 15:33
 */
public class ParamPreviewForm {

    @NonNls
    public static final String TOOLKIT_PREVIEW_POPUP = "com.intellij.toolkit.preview.popup";

    private final AtomicBoolean myIsPinned = new AtomicBoolean(false);

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final JsonObject jsonObject;

    private final PsiElementFactory psiElementFactory;

    private JPanel rootPanel;
    private JPanel tailToolbarPanel;
    private JPanel headToolbarPanel;
    private JLabel fileReference;
    private JScrollPane paramScrollPane;


    private List<ParamData> rootParamDataList;

    private JBPopup popup;


    public ParamPreviewForm(@NotNull Project project, @NotNull PsiFile psiFile,
                            @NotNull PsiClass psiClass, @NotNull JsonObject jsonObject) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.jsonObject = jsonObject;

        // 获取PsiElementFactory来创建Element，包括字段，方法, 注解, 内部类等
        this.psiElementFactory = JavaPsiFacade.getElementFactory(project);

        initUI();
        initTitle();
        initHeadToolbar();
        addMouseListeners();
        initParamData();
        initTailRightToolbar();

    }

    private void initUI() {
        rootPanel.setBorder(JBUI.Borders.empty(10, 10, 0, 10));
        paramScrollPane.setBorder(JBUI.Borders.empty());
        tailToolbarPanel.setBorder(JBUI.Borders.empty());
    }

    private void initTitle() {

        if (psiClass != null) {
            fileReference.setText(psiClass.getQualifiedName());
        } else if (psiFile != null) {
            fileReference.setText(psiFile.getName());
        } else {
            fileReference.setText("Toolkit");
        }
    }

    private void initHeadToolbar() {
        DefaultActionGroup group = new DefaultActionGroup();

        group.add(new AnAction("Setting", "Toolkit settings", AllIcons.General.GearPlain) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                myIsPinned.set(true);
                SettingsForm.getInstance(project).popup();
            }
        });

        group.addSeparator();

        group.add(new ToggleAction("Pin", "Pin window", AllIcons.General.Pin_tab) {

            @Override
            public boolean isDumbAware() {
                return true;
            }

            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return myIsPinned.get();
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                myIsPinned.set(state);
            }
        });

        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("ToolkitHeadToolbar", group, true);
        toolbar.setTargetComponent(headToolbarPanel);

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        headToolbarPanel.add(toolbar.getComponent(), BorderLayout.EAST);
    }

    private void addMouseListeners() {
        WindowMoveListener windowMoveListener = new WindowMoveListener(rootPanel);
        rootPanel.addMouseListener(windowMoveListener);
        rootPanel.addMouseMotionListener(windowMoveListener);
        headToolbarPanel.addMouseListener(windowMoveListener);
        headToolbarPanel.addMouseMotionListener(windowMoveListener);

    }


    @NotNull
    public static ParamPreviewForm getInstance(@NotNull Project project, @NotNull PsiFile psiFile,
                                               @NotNull PsiClass psiClass, @NotNull JsonObject jsonObject) {

        return new ParamPreviewForm(project, psiFile, psiClass, jsonObject);
    }

    public void popup() {
        // dialog 改成 popup, 第一个为根¸面板，第二个为焦点面板
        popup = JBPopupFactory.getInstance().createComponentPopupBuilder(rootPanel, rootPanel)
                .setProject(project)
                .setResizable(true)
                .setMovable(true)

                .setModalContext(false)
                .setRequestFocus(true)
                .setBelongsToGlobalPopupStack(true)
                .setDimensionServiceKey(null, TOOLKIT_PREVIEW_POPUP, true)
                .setLocateWithinScreenBounds(false)
                // 鼠标点击外部时是否取消弹窗 外部单击, 未处于 pin 状态则可关闭
                .setCancelOnMouseOutCallback(event -> event.getID() == MouseEvent.MOUSE_PRESSED)

                // 单击外部时取消弹窗
                .setCancelOnClickOutside(false)
                // 在其他窗口打开时取消
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnWindowDeactivation(false)
                .createPopup();
        popup.showCenteredInCurrentWindow(project);
    }

    private void initTailRightToolbar() {

        DefaultActionGroup rightGroup = new DefaultActionGroup();

        rightGroup.add(new AnAction(ToolkitBundle.message("common.confirm"), "", AllIcons.Actions.Commit) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                Settings settings = Settings.getInstance(project);

                // 调用生成逻辑
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    generateEntity(settings, rootParamDataList);

                    SettingsHandlerUtils.lombokHandler(settings, psiElementFactory, psiClass);
                    SettingsHandlerUtils.commentHandler(settings, psiElementFactory, psiClass, jsonObject.toString());

                    // 格式化代码
                    JavaCodeStyleManager javaCodeStyleManager = JavaCodeStyleManager.getInstance(project);
                    javaCodeStyleManager.optimizeImports(psiClass.getContainingFile());
                    javaCodeStyleManager.shortenClassReferences(psiClass);

                    // 格式化注释
                    CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiClass.getProject());

                    codeStyleManager.reformatText(psiClass.getContainingFile(), 0, psiClass.getTextLength() + 1);
                });

                popup.cancel();
            }
        });

        // init toolbar
        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("PreviewPanelRightToolbar", rightGroup, true);
        toolbar.setTargetComponent(tailToolbarPanel);

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        tailToolbarPanel.add(toolbar.getComponent(), BorderLayout.EAST);

    }

    /**
     * 生成字段
     */
    private void generateEntity(Settings settings, List<ParamData> paramDataList) {

        for (ParamData paramData : paramDataList) {
            PsiClass parentPisClass = paramData.getParentPisClass();

            if (parentPisClass == null) {
                continue;
            }

            PsiField psiField = psiElementFactory.createField(paramData.getParamName(), paramData.getParamPsiType());

            PsiModifierList modifierList = psiField.getModifierList();
            modifierList.setModifierProperty(settings.getFieldModifier(), true);

            if (parentPisClass.findFieldByName(paramData.getParamName(), false) == null) {
                parentPisClass.add(psiField);
            }

            if (CollectionUtils.isNotEmpty(paramData.getChild())
                    && parentPisClass.findInnerClassByName(paramData.getParamName(), false) == null) {

                PsiClass paramPsiClass = paramData.getParamPsiClass();

                PsiModifierList psiClassModifierList = paramPsiClass.getModifierList();
                psiClassModifierList.setModifierProperty(PsiModifier.PUBLIC, true);
                psiClassModifierList.setModifierProperty(PsiModifier.STATIC, true);

                generateEntity(settings, paramData.getChild());

                SettingsHandlerUtils.lombokHandler(settings, psiElementFactory, paramPsiClass);
                parentPisClass.addBefore(paramPsiClass, parentPisClass.getRBrace());
            }

        }

    }


    private void initParamData() {

        PsiField[] allFields = psiClass.getAllFields();

        Set<ParamData> paramDataSet = ParamDataUtils.jsonObjectToParamData(psiClass, psiElementFactory, jsonObject);


        for (PsiField field : allFields) {

            ParamData paramData = new ParamData(field.getName());
            paramData.setKey(ParamDataUtils.DEFAULT_FIELD);
            paramData.setParamPsiType(field.getType());
            paramData.setParamType(field.getType().getPresentableText());
            paramData.setValue("");

            paramDataSet.add(paramData);
        }

        rootParamDataList = new ArrayList<>(paramDataSet);

        ParamDataUtils.sort(rootParamDataList);

        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();

        createTreeData(root, rootParamDataList);


        ParamTreeTableModel paramTreeTableModel = new ParamTreeTableModel(root);

        JXTreeTable treeTable = new JXTreeTable(paramTreeTableModel) {

            @Override
            public boolean isHierarchical(int column) {
                if (column >= 0 && column < this.getColumnCount()) {
                    return this.getHierarchicalColumn() == column;
                } else {
                    return false;
                }
            }
        };
        treeTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        treeTable.expandAll();
        treeTable.setCellSelectionEnabled(false);
        treeTable.setRowHeight(30);
        treeTable.setLeafIcon(null);
        treeTable.setCollapsedIcon(null);
        treeTable.setExpandedIcon(null);
        treeTable.setOpenIcon(null);
        treeTable.setClosedIcon(null);
        treeTable.setExpandedIcon(null);
        treeTable.setSelectionBackground(JBColor.WHITE);
        treeTable.setSelectionForeground(JBColor.WHITE);


        paramScrollPane.setViewportView(treeTable);


    }

    private void createTreeData(DefaultMutableTreeTableNode root, List<ParamData> paramDataList) {

        for (ParamData paramData : paramDataList) {
            DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(paramData);
            root.add(node);
            if (paramData.getChild() != null && paramData.getChild().size() > 0) {
                createTreeData(node, paramData.getChild());
            }
        }

    }

}
