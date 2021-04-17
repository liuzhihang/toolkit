package com.liuzhihang.toolkit.ui;

import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.WindowMoveListener;
import com.intellij.util.ui.JBUI;
import com.liuzhihang.toolkit.utils.CustomPsiUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liuzhihang
 * @date 2021/4/15 22:45
 */
public class ToolkitForm {

    @NonNls
    public static final String TOOLKIT_POPUP = "com.intellij.toolkit.popup";
    private static final AtomicBoolean myIsPinned = new AtomicBoolean(false);

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final PsiMethod psiMethod;

    private JPanel rootPanel;
    private JTabbedPane toolkitTabbedPane;
    private JPanel headToolbarPanel;
    private JPanel jsonFormatTabPanel;

    private JLabel fileReference;

    private JPanel entityJsonTabPanel;
    private JPanel base64TabPanel;
    private JPanel urlTabPanel;

    protected ToolkitForm(@NotNull Project project, PsiFile psiFile,
                          PsiClass psiClass, PsiMethod psiMethod) {
        this.project = project;
        this.psiClass = psiClass;
        this.psiMethod = psiMethod;
        this.psiFile = psiFile;

        initUI();

        initTitle(psiFile, psiClass);

        initHeadToolbar();
        // 鼠标拖动
        addMouseListeners();

        tabPanelListener();
    }


    @NotNull
    public static ToolkitForm getInstance(@NotNull Project project, Editor editor, PsiFile psiFile) {
        PsiClass psiClass = null;
        PsiMethod targetMethod = null;

        if (editor != null) {
            // 获取Java类或者接口
            psiClass = CustomPsiUtils.getTargetClass(editor, psiFile);

            targetMethod = CustomPsiUtils.getTargetMethod(editor, psiFile);
        }

        return new ToolkitForm(project, psiFile, psiClass, targetMethod);
    }

    public void popup() {

        // dialog 改成 popup, 第一个为根¸面板，第二个为焦点面板
       JBPopup  popup = JBPopupFactory.getInstance().createComponentPopupBuilder(rootPanel, toolkitTabbedPane)
                .setProject(project)
                .setResizable(true)
                .setMovable(true)

                .setModalContext(false)
                .setRequestFocus(true)
                .setBelongsToGlobalPopupStack(true)
                .setDimensionServiceKey(null, TOOLKIT_POPUP, true)
                .setLocateWithinScreenBounds(false)
                // 鼠标点击外部时是否取消弹窗 外部单击, 未处于 pin 状态则可关闭
                .setCancelOnMouseOutCallback(event -> event.getID() == MouseEvent.MOUSE_PRESSED && !myIsPinned.get())

                // 单击外部时取消弹窗
                .setCancelOnClickOutside(false)
                // 在其他窗口打开时取消
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnWindowDeactivation(false)
                .createPopup();

        popup.showCenteredInCurrentWindow(project);
        initTabs(popup);
    }

    private void addMouseListeners() {
        WindowMoveListener windowMoveListener = new WindowMoveListener(rootPanel);
        rootPanel.addMouseListener(windowMoveListener);
        rootPanel.addMouseMotionListener(windowMoveListener);

    }

    private void initHeadToolbar() {
        DefaultActionGroup group = new DefaultActionGroup();

        // group.add(new AnAction("Setting", "Doc view settings", AllIcons.General.GearPlain) {
        //     @Override
        //     public void actionPerformed(@NotNull AnActionEvent e) {
        //         ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), SettingsConfigurable.class);
        //     }
        // });

        // group.addSeparator();

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

    private void initUI() {
        // 边框
        headToolbarPanel.setBorder(JBUI.Borders.empty());
    }

    private void initTitle(PsiFile psiFile, PsiClass psiClass) {

        if (psiClass != null) {
            fileReference.setText(psiClass.getQualifiedName());
        } else if (psiFile != null) {
            fileReference.setText(psiFile.getName());
        } else {
            fileReference.setText("Toolkit");
        }
    }

    /**
     * 初始化所有选项卡
     *
     * @param popup
     */
    private void initTabs(JBPopup popup) {
        // json tab
        JPanel rootJPanel = JsonFormatForm.getInstance(project, psiFile, psiClass, popup).getRootJPanel();

        System.out.println("rootJPanel = " + rootJPanel.getHeight());

        jsonFormatTabPanel.add(rootJPanel);

        System.out.println("rootPanel = " + rootPanel.getHeight());

        if (psiClass != null) {
            entityJsonTabPanel.add(EntityJsonForm.getInstance(project, psiFile, psiClass, popup).getRootJPanel());
        } else {
            toolkitTabbedPane.removeTabAt(1);
        }
        base64TabPanel.add(Base64Form.getInstance(project, psiFile, psiClass, popup).getRootJPanel());
        urlTabPanel.add(UrlForm.getInstance(project, psiFile, psiClass, popup).getRootJPanel());
    }

    private void tabPanelListener() {

        toolkitTabbedPane.addChangeListener(e -> {
        });

    }
}
