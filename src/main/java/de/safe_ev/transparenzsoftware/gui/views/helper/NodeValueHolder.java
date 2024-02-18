package de.safe_ev.transparenzsoftware.gui.views.helper;

import javax.swing.tree.DefaultMutableTreeNode;

import de.safe_ev.transparenzsoftware.i18n.Translator;

public class NodeValueHolder {
    private final ValueIndexHolder value;
    private final int index;
    private final DefaultMutableTreeNode valueNode;

    public NodeValueHolder(ValueIndexHolder value, int index, DefaultMutableTreeNode valueNode) {
        this.value = value;
        this.index = index;
        this.valueNode = valueNode;
    }

    public ValueIndexHolder getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public DefaultMutableTreeNode getValueNode() {
        return valueNode;
    }

    public String toString() {

        String nameNode = String.format("%s %d", Translator.get("app.view.single.value"), value.getInitIndex());
        if(value.getValue().getContext() != null && !value.getValue().getContext().trim().isEmpty()){
            nameNode = String.format(
                    "%s %d (%s)",
                    Translator.get("app.view.single.value"),
                    value.getInitIndex(),
                    value.getValue().getContext().trim()
            );
        }
        return nameNode;
    }

}
