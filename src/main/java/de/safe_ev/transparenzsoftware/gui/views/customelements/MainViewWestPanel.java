package de.safe_ev.transparenzsoftware.gui.views.customelements;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.gui.views.MainView;
import de.safe_ev.transparenzsoftware.gui.views.helper.NodeHeadingHolder;
import de.safe_ev.transparenzsoftware.gui.views.helper.NodeValueHolder;
import de.safe_ev.transparenzsoftware.gui.views.helper.ValueIndexHolder;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MainViewWestPanel extends JPanel {

    private final static Logger LOGGER = LogManager.getLogger(MainViewWestPanel.class);
    private ValueTree tree;
    private MainView mainView;
    private BigInteger transactionid;

    public MainViewWestPanel(MainView mainView) {
        this.mainView = mainView;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentY(0f);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initEmpty();
        transactionid = null;
    }

    private void initEmpty() {
        removeAll();
        validate();
        this.tree = null;
        this.transactionid = null;
    }

    public void updateTree(Values values) throws InvalidInputException {
        if (values == null || values.getValues().size() == 0) {
            initEmpty();
            return;
        }
        removeAll();
        add(Box.createHorizontalStrut(mainView.getPreferredSize().width / 3));

        tree = ValueTree.createFromValues(values);

        tree.getSelectionModel().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode.getUserObject() instanceof NodeValueHolder) {
                NodeValueHolder valueHolder = (NodeValueHolder) selectedNode.getUserObject();
                transactionid = null;
                mainView.setEnableVerifyMode(true);
                mainView.stepToValue(valueHolder.getValue().getInitIndex());
            } else if (selectedNode.getUserObject() instanceof NodeHeadingHolder) {
                NodeHeadingHolder valueHolder = (NodeHeadingHolder) selectedNode.getUserObject();
                mainView.setEnableVerifyMode(true);
                transactionid = valueHolder.getTransactionId();
                if (tree.getTransactionMap().get(transactionid) != null && tree.getTransactionMap().get(transactionid).size() > 0) {
                    mainView.stepToValueWithKeyCheck(tree.getTransactionMap().get(transactionid).get(0).getInitIndex());
                } else {
                    LOGGER.error(String.format("Could not load value for transaction id %s", transactionid));
                }
            } else {
                transactionid = null;
            }
        });

        //wrap in invoke later to prevent weird selection highlighting bug
        SwingUtilities.invokeLater(() -> {
            //0 means we have not found a transaction id so we select the first value in the xml
            if (tree.getPathMinimumTransactionId() != null) {
                tree.setSelectionPath(new TreePath(tree.getPathMinimumTransactionId()));
            } else {
                tree.selectValue(0);
            }
        });


        //clean up old values and set new


        removeAll();
        add(Box.createVerticalStrut(40));
        add(new JLabel(Translator.get("app.view.loaded.values")));
        add(Box.createVerticalStrut(10));
        add(new JScrollPane(tree));
        validate();
    }

    public void setSelection(int index) {
        if (tree == null) {
            return;
        }
        tree.selectValue(index);
    }

    public BigInteger getCurrentTransactionid() {
        return transactionid;
    }

    public List<Value> getValues(BigInteger transactionid) {
        List<ValueIndexHolder> valueIndexHolders = this.tree.getTransactionMap().get(transactionid);
        List<Value> values = new ArrayList<>();
        for (ValueIndexHolder valueIndexHolder : valueIndexHolders) {
            values.add(valueIndexHolder.getValue());
        }
        return values;
    }

    public void initView() {
        initEmpty();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
