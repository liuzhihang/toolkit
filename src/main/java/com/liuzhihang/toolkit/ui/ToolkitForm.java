package com.liuzhihang.toolkit.ui;

import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author liuzhihang
 * @date 2020/12/24 10:36
 */
public class ToolkitForm extends DialogWrapper {
    private JPanel rootPanel;
    private JTabbedPane tabbedPane;

    private JPanel jsonLeftPanel;
    private JPanel jsonRightPanel;
    private JPanel jsonLeftToolBarPane;
    private JPanel jsonRightToolBarPane;
    private JTextPane jsonLeftTextPane;
    private JPanel jsonEditorPane;
    private EditorEx jsonEditor;
    private Document jsonDocument = EditorFactory.getInstance().createDocument("");

    private Project project;
    private PsiFile psiFile;
    private Editor editor;
    private PsiClass psiClass;

    public ToolkitForm(@Nullable Project project, PsiFile psiFile, Editor editor, PsiClass psiClass) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.psiFile = psiFile;
        this.editor = editor;
        this.psiClass = psiClass;


        init();
        setTitle("Toolkit");
    }


    private void initJsonEditor() {

        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("json");

        final EditorHighlighter editorHighlighter =
                HighlighterFactory.createHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), project);

        jsonEditor = (EditorEx) EditorFactory.getInstance().createEditor(jsonDocument, project, fileType, true);

        EditorSettings editorSettings = jsonEditor.getSettings();
        editorSettings.setAdditionalLinesCount(0);
        editorSettings.setAdditionalColumnsCount(0);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setLineNumbersShown(false);
        editorSettings.setVirtualSpace(false);
        editorSettings.setFoldingOutlineShown(false);

        editorSettings.setLanguageSupplier(() -> Language.findLanguageByID("Json"));

        jsonEditor.setHighlighter(editorHighlighter);
        jsonEditor.setBorder(JBUI.Borders.emptyLeft(5));

        JBScrollPane templateScrollPane = new JBScrollPane(jsonEditor.getComponent());

        JBScrollBar jbScrollBar = new JBScrollBar();
        jbScrollBar.setBackground(jsonEditor.getBackgroundColor());

        templateScrollPane.setHorizontalScrollBar(jbScrollBar);
        jsonRightPanel.add(templateScrollPane, BorderLayout.CENTER);
    }

    private void initJsonLeftToolbar() {

        DefaultActionGroup leftGroup = new DefaultActionGroup();

        leftGroup.add(new AnAction("Search", "Search", AllIcons.Actions.RemoveMulticaret) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {


            }
        });


        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("ToolkitJsonLeftToolbar", leftGroup, true);
        toolbar.setTargetComponent(jsonLeftToolBarPane);

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        jsonLeftToolBarPane.add(toolbar.getComponent(), BorderLayout.WEST);
    }

    private void initJsonRightToolbar() {

        DefaultActionGroup leftGroup = new DefaultActionGroup();

        leftGroup.add(new AnAction("Search", "Search", AllIcons.Actions.RemoveMulticaret) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {


            }
        });


        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("ToolkitJsonLeftToolbar", leftGroup, true);
        toolbar.setTargetComponent(jsonLeftPanel);

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        jsonLeftPanel.add(toolbar.getComponent(), BorderLayout.EAST);
    }



    private void initEditorLeftToolbar() {

        DefaultActionGroup leftGroup = new DefaultActionGroup();

        leftGroup.add(new AnAction("Search", "Search", AllIcons.Actions.RemoveMulticaret) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {


            }
        });

        leftGroup.addSeparator();

        leftGroup.add(new AnAction("Editor", "Editor doc", AllIcons.Actions.Search) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

            }
        });

        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("DocViewEditorLeftToolbar", leftGroup, true);
        toolbar.setTargetComponent(jsonRightPanel);
        toolbar.getComponent().setBackground(jsonEditor.getBackgroundColor());

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        jsonRightPanel.setBackground(jsonEditor.getBackgroundColor());
        jsonRightPanel.add(toolbar.getComponent(), BorderLayout.WEST);
    }

    private void initEditorRightToolbar() {
        DefaultActionGroup rightGroup = new DefaultActionGroup();

        rightGroup.add(new AnAction("Generate", "Generate field", AllIcons.Actions.EditScheme) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

            }
        });

        rightGroup.add(new AnAction("Copy", "Copy to clipboard", AllIcons.Actions.Copy) {


            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                String text = jsonDocument.getText();

                if (StringUtils.isBlank(text)) {

                } else {
                    StringSelection selection = new StringSelection(text);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                }

            }
        });

        // init toolbar
        // ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
        //         .createActionToolbar("DocViewEditorRightToolbar", rightGroup, true);
        // toolbar.setTargetComponent(previewEditorPane);
        // toolbar.getComponent().setBackground(markdownEditor.getBackgroundColor());
        //
        // toolbar.setForceMinimumSize(true);
        // toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        // Utils.setSmallerFontForChildren(toolbar);
        //
        // previewEditorPane.setBackground(markdownEditor.getBackgroundColor());
        // previewEditorPane.add(toolbar.getComponent(), BorderLayout.EAST);

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
        return rootPanel;
    }
}
