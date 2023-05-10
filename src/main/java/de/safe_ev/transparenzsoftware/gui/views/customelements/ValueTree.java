package de.safe_ev.transparenzsoftware.gui.views.customelements;

import javax.swing.*;
import javax.swing.tree.*;

import de.safe_ev.transparenzsoftware.gui.views.helper.NodeHeadingHolder;
import de.safe_ev.transparenzsoftware.gui.views.helper.NodeValueHolder;
import de.safe_ev.transparenzsoftware.gui.views.helper.ValueIndexHolder;
import de.safe_ev.transparenzsoftware.gui.views.helper.ValueMapBuilder;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

import java.math.BigInteger;
import java.util.*;

public class ValueTree extends JTree {

    private TreeNode[] pathMinimumTransactionId;
    private Map<BigInteger, List<ValueIndexHolder>> transactionMap;
    private final List<NodeValueHolder> values;



    public ValueTree(TreeNode treeNode, List<NodeValueHolder> values, Map<BigInteger, List<ValueIndexHolder>> transactionMap, DefaultMutableTreeNode treeNodeMininumTransactionId) {
        super(treeNode);
        this.values = values;
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) this.getCellRenderer();
        renderer.setLeafIcon(null);
        setRootVisible(false);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        toggleTree(true);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.transactionMap = transactionMap;
        setExpandsSelectedPaths(true);
        TreeModel model = getModel();
        if (model instanceof DefaultTreeModel) {
            DefaultTreeModel dfModel = (DefaultTreeModel) model;
            //we always need the full path so we calculate it out of the root
            this.pathMinimumTransactionId = dfModel.getPathToRoot(treeNodeMininumTransactionId);
        }
    }

    public static ValueTree createFromValues(Values values) throws InvalidInputException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        Map<BigInteger, List<ValueIndexHolder>> transactionMap = ValueMapBuilder.buildTransactionMap(values);
        List<NodeValueHolder> valueHolders = new ArrayList<>();

        //integer which we will store to select the child with the lowest transaction id
        DefaultMutableTreeNode pathMinimumTransactionId = null;
        BigInteger minimumTransactionId = BigInteger.valueOf(Long.MAX_VALUE);

        //counter object for looping over the map
        int indexTransactions = 0;
        for (BigInteger transactionId : transactionMap.keySet()) {
            String label = Translator.get("app.view.no.transactionid");
            if (!transactionId.equals(ValueMapBuilder.NO_TRANSACTION_KEY)) {
                label = String.format("%s %d", Translator.get("app.view.transaction.id"), transactionId);
            }
            DefaultMutableTreeNode transactionRootNode = new DefaultMutableTreeNode(label);
            if (!transactionId.equals(ValueMapBuilder.NO_TRANSACTION_KEY)) {
                NodeHeadingHolder headingHolder = new NodeHeadingHolder(transactionId, label);
                transactionRootNode.setUserObject(headingHolder);
                if (minimumTransactionId.longValue() > transactionId.longValue()) {
                    pathMinimumTransactionId = transactionRootNode;
                    minimumTransactionId = transactionId;
                }

            }
            int i = 0;
            for (ValueIndexHolder value : transactionMap.get(transactionId)) {
                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode("node");
                NodeValueHolder holder = new NodeValueHolder(value, i, valueNode);
                valueHolders.add(holder);
                valueNode.setUserObject(holder);
                transactionRootNode.add(valueNode);
                i++;
            }
            root.add(transactionRootNode);
            indexTransactions++;
        }
        //if we have not found a we just select the first value
        ValueTree tree = new ValueTree(root, valueHolders, transactionMap, pathMinimumTransactionId);
        return tree;
    }

    /**
     * Expands all tree nodes if expand to true otherwise collapse
     *
     * @param expand if true tree will be expanded otherwise collapsed
     */
    public void toggleTree(boolean expand) {
        TreeNode root = (TreeNode) this.getModel().getRoot();
        toggleAll(new TreePath(root), expand);
    }

    /**
     * Expand or close path
     *
     * @param path   path to close or expand
     * @param expand if true expand or
     */
    public void toggleAll(TreePath path, boolean expand) {
        TreeNode node = (TreeNode) path.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            Enumeration enumeration = node.children();
            while (enumeration.hasMoreElements()) {
                TreeNode n = (TreeNode) enumeration.nextElement();
                TreePath p = path.pathByAddingChild(n);
                toggleAll(p, expand);
            }
        }

        if (expand) {
            expandPath(path);
        } else {
            collapsePath(path);
        }
    }

    /**
     * Select the value based on the index of the values
     * in the loaded xml value
     *
     * @param index index in the xml file
     */
    public void selectValue(int index) {
        for (NodeValueHolder value : values) {
            if (value.getValue().getInitIndex() == index) {
                setSelectionPath(new TreePath(value.getValueNode().getPath()));
            }
        }
    }

    public Map<BigInteger, List<ValueIndexHolder>> getTransactionMap() {
        return transactionMap;
    }

    public TreeNode[] getPathMinimumTransactionId() {
        return pathMinimumTransactionId;
    }
}
