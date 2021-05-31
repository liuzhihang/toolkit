package com.liuzhihang.toolkit.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;

import static com.liuzhihang.toolkit.ToolkitBundle.message;

/**
 * @author liuzhihang
 * @date 2021/5/27 14:47
 */
public class SupportForm {

    @NonNls
    public static final String TOOLKIT_SUPPORT_POPUP = "com.intellij.toolkit.support.popup";


    private JPanel rootPanel;
    private LinkLabel<String> starLinkLabel;
    private LinkLabel<String> reportLinkLabel;
    private LinkLabel<String> websiteLinkLabel;

    private final Project project;

    public SupportForm(Project project) {
        this.project = project;

        starLinkLabel.setIcon(null);
        reportLinkLabel.setIcon(null);
        websiteLinkLabel.setIcon(null);

        rootPanel.setBorder(JBUI.Borders.empty(12, 15));
        starLinkLabel.setListener((source, data) -> BrowserUtil.browse(data), message("common.github.url"));
        reportLinkLabel.setListener((source, data) -> BrowserUtil.browse(data), message("common.github.issues.url"));
        websiteLinkLabel.setListener((source, data) -> BrowserUtil.browse(data), "common.website.issues.url");
    }

    @NotNull
    public static SupportForm getInstance(@NotNull Project project) {

        return new SupportForm(project);
    }

    public void popup() {
        // dialog 改成 popup, 第一个为根¸面板，第二个为焦点面板
        JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(rootPanel, rootPanel)
                .setProject(project)
                .setResizable(true)
                .setMovable(true)

                .setModalContext(false)
                .setRequestFocus(true)
                .setBelongsToGlobalPopupStack(true)
                .setDimensionServiceKey(null, TOOLKIT_SUPPORT_POPUP, true)
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
}
