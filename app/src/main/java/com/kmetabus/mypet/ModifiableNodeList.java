package com.kmetabus.mypet;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
// nodelist를 sort해서 담는다
public class ModifiableNodeList implements NodeList {
    private List<Node> nodes;

    public ModifiableNodeList(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Node item(int index) {
        return nodes.get(index);
    }

    @Override
    public int getLength() {
        return nodes.size();
    }
}
