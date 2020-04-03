package com.ulyp.storage;

import java.util.List;

public interface Storage {

    MethodTraceTreeNode find(long id);

    List<MethodTraceTreeNode> getChildren(long id);

    void persist(MethodTraceTreeNode node);
}
