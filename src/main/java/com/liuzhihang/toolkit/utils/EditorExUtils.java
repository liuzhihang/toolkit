package com.liuzhihang.toolkit.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

/**
 * @author liuzhihang
 * @date 2021/4/17 13:33
 */
public class EditorExUtils {

    private static final EditorColorsScheme EDITOR_COLORS_SCHEME = EditorColorsManager.getInstance().getGlobalScheme();

    public static EditorEx createEditorEx(@NotNull Project project, @NotNull Document document,
                                          @NotNull FileType fileType, @NotNull Boolean isViewer) {

        EditorEx editorEx = (EditorEx) EditorFactory.getInstance().createEditor(document, project, fileType, isViewer);
        EditorSettings editorSettings = editorEx.getSettings();
        editorSettings.setAdditionalLinesCount(0);
        editorSettings.setAdditionalColumnsCount(0);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setLineNumbersShown(false);
        editorSettings.setVirtualSpace(false);
        editorSettings.setFoldingOutlineShown(false);
        editorSettings.setTabSize(4);

        // editorEx.setHighlighter(editorHighlighter);
        editorEx.setBorder(JBUI.Borders.emptyLeft(5));

        EditorColorsScheme colorsScheme = editorEx.getColorsScheme();
        colorsScheme.setColor(EditorColors.CARET_ROW_COLOR, null);

        return editorEx;
    }
}
