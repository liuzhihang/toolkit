package com.liuzhihang.toolkit.ui;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * @author liuzhihang
 * @date 2021/3/30 10:16
 */
public class ParamTreeTableUtils {


    /**
     * 对 treeTable 进行渲染
     *
     * @param treeTable
     */
    public static void render(JXTreeTable treeTable) {

        final DefaultListSelectionModel defaultListSelectionModel = new DefaultListSelectionModel();
        treeTable.setSelectionModel(defaultListSelectionModel);

        defaultListSelectionModel.setSelectionMode(SINGLE_SELECTION);
        defaultListSelectionModel.addListSelectionListener(e -> defaultListSelectionModel.clearSelection());


        for (int i = 0; i < treeTable.getColumnCount(); i++) {

            TableColumn column = treeTable.getColumn(i);

            if (column.getIdentifier().equals("类型")) {
                // 设置 表格列 的 单元格编辑器, 只能从指定类型中获取
                column.setCellEditor(new DefaultCellEditor(new JComboBox<>(ParamTreeTableModel.PARAM_TYPE_VECTOR)));
            }

        }

        treeTable.setRowHeight(30);
        treeTable.setLeafIcon(null);
        treeTable.setOpenIcon(null);
        treeTable.setClosedIcon(null);

        if (UIUtil.isUnderDarcula()) {
            treeTable.addHighlighter(new ColorHighlighter((renderer, adapter) -> adapter.row % 2 == 1, Gray._45, null));
        } else {
            treeTable.addHighlighter(new ColorHighlighter((renderer, adapter) -> adapter.row % 2 == 1, Gray._245, null));
        }

        treeTable.setSelectionForeground(JBColor.WHITE);
    }

    public static void createTreeData(DefaultMutableTreeTableNode root, List<ParamData> paramDataList) {

        for (ParamData paramData : paramDataList) {
            DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(paramData);
            root.add(node);
            if (paramData.getChild() != null && paramData.getChild().size() > 0) {
                createTreeData(node, paramData.getChild());
            }
        }

    }

}
