package com.liuzhihang.toolkit.ui;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiElementFactory;
import com.intellij.ui.SingleSelectionModel;
import com.intellij.ui.dualView.TreeTableView;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.util.Vector;

/**
 * @author liuzhihang
 * @date 2021/10/20 19:51
 */
public class ParamTreeTableView extends TreeTableView {

    public static final Vector<String> PARAM_TYPE_VECTOR = new Vector<>() {{
        add("String");
        add("Integer");
        add("Long");
        add("Double");
        add("Boolean");
        add("Byte");
        add("Short");
        add("Float");
        add("Number");
        add("Character");
    }};

    // {"参数名", "类型", "必选", "描述"}
    public static final ColumnInfo[] COLUMN_INFOS = new ColumnInfo[]{
            new TreeColumnInfo("Key *") {

                @NlsContexts.Tooltip
                @Nullable
                @Override
                public String getTooltipText() {
                    return "不可修改";
                }

            },
            new ColumnInfo("Value *") {
                @Nullable
                @Override
                public Object valueOf(Object o) {
                    if (o instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                        if (node.getUserObject() instanceof ParamData) {
                            ParamData paramData = (ParamData) node.getUserObject();
                            return paramData.getValue();
                        }

                    }
                    return o;
                }

                @NlsContexts.Tooltip
                @Nullable
                @Override
                public String getTooltipText() {
                    return "不可修改";
                }
            },

            new ColumnInfo("字段名 *") {
                @Nullable
                @Override
                public Object valueOf(Object o) {
                    if (o instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                        if (node.getUserObject() instanceof ParamData) {
                            ParamData paramData = (ParamData) node.getUserObject();
                            return paramData.getParamName();
                        }

                    }
                    return o;
                }

                @NlsContexts.Tooltip
                @Nullable
                @Override
                public String getTooltipText() {
                    return "不可修改";
                }
            },
            new ColumnInfo("类型") {
                @Nullable
                @Override
                public Object valueOf(Object o) {
                    if (o instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                        if (node.getUserObject() instanceof ParamData) {
                            ParamData paramData = (ParamData) node.getUserObject();
                            return paramData.getParamType();
                        }

                    }
                    return o;
                }

                @Override
                public int getWidth(JTable table) {
                    return 80;
                }
            },
            new ColumnInfo("注释") {
                @Nullable
                @Override
                public Object valueOf(Object o) {
                    if (o instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                        if (node.getUserObject() instanceof ParamData) {
                            ParamData paramData = (ParamData) node.getUserObject();
                            return paramData.getDesc();
                        }

                    }
                    return o;
                }
            }
    };


    private static final NodeRenderer NODE_RENDERER = new NodeRenderer() {

        @Override
        public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            ParamData paramData = (ParamData) node.getUserObject();
            append(paramData.getKey());
        }

    };


    private static final String TYPE_PREFIX = "java.lang.";


    private final PsiElementFactory psiElementFactory;

    public ParamTreeTableView(ListTreeTableModelOnColumns treeTableModel, PsiElementFactory psiElementFactory) {
        super(treeTableModel);
        this.psiElementFactory = psiElementFactory;
    }

    @Override
    public void setTreeCellRenderer(TreeCellRenderer renderer) {
        super.setTreeCellRenderer(NODE_RENDERER);
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {

        // 类型 选择
        if (column == 3) {
            return new DefaultCellEditor(new JComboBox<>(PARAM_TYPE_VECTOR));
        }
        return super.getCellEditor(row, column);
    }


    @Override
    public Object getValueAt(int row, int column) {
        ListTreeTableModelOnColumns tableModel = (ListTreeTableModelOnColumns) getTableModel();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tableModel.getRowValue(row);
        ParamData paramData = (ParamData) node.getUserObject();
        switch (column) {
            case 0:
                return paramData.getKey();
            case 1:
                return paramData.getValue();
            case 2:
                return paramData.getParamName();
            case 3:
                return paramData.getParamType();
            case 4:
                return paramData.getDesc();
        }
        return "";
    }


    @Override
    public void setValueAt(Object value, int row, int column) {

        ListTreeTableModelOnColumns tableModel = (ListTreeTableModelOnColumns) getTableModel();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tableModel.getRowValue(row);
        ParamData data = (ParamData) node.getUserObject();

        // "Key", "Value", "字段名", "类型", "注释"
        if (column == 3) {
            data.setParamType(String.valueOf(value));
            data.setParamPsiType(psiElementFactory.createTypeFromText(TYPE_PREFIX + value, null));

        } else if (column == 4) {
            data.setDesc(String.valueOf(value));
        }

        super.setValueAt(value, row, column);

    }

    @Override
    public boolean isCellEditable(int row, int column) {

        if (column == 3) {
            return true;
        }
        if (column == 4) {
            return true;
        }

        return false;
    }

    @Override
    public int getRowHeight() {
        return 24;
    }

    @Override
    public void setSelectionModel(@NotNull ListSelectionModel selectionModel) {
        super.setSelectionModel(new SingleSelectionModel());
    }


}
