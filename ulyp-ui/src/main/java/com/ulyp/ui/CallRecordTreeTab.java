package com.ulyp.ui;

import com.ulyp.core.*;
import com.ulyp.core.impl.InMemoryIndexFileBasedCallRecordDatabase;
import com.ulyp.transport.RecordingInfo;
import com.ulyp.transport.TStackTraceElement;
import com.ulyp.ui.code.SourceCode;
import com.ulyp.ui.code.SourceCodeView;
import com.ulyp.ui.code.find.SourceCodeFinder;
import com.ulyp.ui.font.FontSizeChanger;
import com.ulyp.ui.util.ResizeEvent;
import com.ulyp.ui.util.ResizeEventSupportingScrollPane;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

@Component
@Scope(value = "prototype")
public class CallRecordTreeTab extends Tab {

    private final Region parent;
    @Nullable
    private CallRecord root;
    @Nullable
    private CallRecordDatabase database;
    @Nullable
    private RecordingInfo recordingInfo;

    private TreeView<CallTreeNodeContent> treeView;

    @Autowired
    private SourceCodeView sourceCodeView;
    @Autowired
    private RenderSettings renderSettings;
    @Autowired
    private FontSizeChanger fontSizeChanger;

    private boolean initialized = false;

    @SuppressWarnings("unchecked")
    public CallRecordTreeTab(Region parent) {
        this.parent = parent;
    }

    public synchronized void init() {
        if (initialized) {
            return;
        }

        treeView = new TreeView<>(new CallRecordTreeNode(database, root.getId(), renderSettings));
        treeView.prefHeightProperty().bind(parent.heightProperty());
        treeView.prefWidthProperty().bind(parent.widthProperty());

        SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(recordingInfo.getProcessInfo().getClasspathList());

        treeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    final CallRecordTreeNode selectedNode = (CallRecordTreeNode) newValue;
                    if (selectedNode != null && selectedNode.getCallRecord() != null) {
                        CompletableFuture<SourceCode> sourceCodeFuture = sourceCodeFinder.find(selectedNode.getCallRecord().getClassName());

                        sourceCodeFuture.thenAccept(
                                sourceCode -> {
                                    Platform.runLater(
                                            () -> {
                                                TreeItem<CallTreeNodeContent> currentlySelected = treeView.getSelectionModel().getSelectedItem();
                                                CallRecordTreeNode currentlySelectedNode = (CallRecordTreeNode) currentlySelected;
                                                if (selectedNode.getCallRecord().getId() == currentlySelectedNode.getCallRecord().getId()) {
                                                    sourceCodeView.setText(sourceCode, currentlySelectedNode.getCallRecord().getMethodName());
                                                }
                                            }
                                    );
                                }
                        );
                    }
                }
        );

        ResizeEventSupportingScrollPane scrollPane = new ResizeEventSupportingScrollPane(treeView);

        scrollPane.addListener(
                resizeEvent -> {
                    if (resizeEvent == ResizeEvent.UP) {
                        fontSizeChanger.upscale(parent.getScene());
                    } else {
                        fontSizeChanger.downscale(parent.getScene());
                    }
                }
        );

        setText(getTabName());
        setContent(scrollPane);
        setOnClosed(ev -> dispose());
        setTooltip(getTooltipText());

        initialized = true;
    }

    public synchronized String getTabName() {
        if (root == null) {
            return "?";
        }

        boolean complete = database.find(0).isComplete();

        return root.getMethodName() + "(" + 0 + ", life=" + recordingInfo.getLifetimeMillis() + " ms, nodes=" + database.countAll() + ")"
                + (complete ? " complete" : "");
    }

    private synchronized Tooltip getTooltipText() {
        if (root == null) {
            return new Tooltip("");
        }

        StringBuilder builder = new StringBuilder()
                .append("Thread: ").append(recordingInfo.getThreadName()).append("\n")
                .append("Created at: ").append(new Timestamp(this.recordingInfo.getCreateEpochMillis())).append("\n")
                .append("Finished at: ").append(new Timestamp(this.recordingInfo.getCreateEpochMillis() + this.recordingInfo.getLifetimeMillis())).append("\n")
                .append("Lifetime: ").append(recordingInfo.getLifetimeMillis()).append(" millis").append("\n");

        builder.append("Stack trace: ").append("\n");

        for (TStackTraceElement element: recordingInfo.getStackTrace().getElementList()) {
            builder.append("\tat ")
                    .append(element.getDeclaringClass())
                    .append(".")
                    .append(element.getMethodName())
                    .append("(")
                    .append(element.getFileName())
                    .append(":")
                    .append(element.getLineNumber())
                    .append(")");
        }

        return new Tooltip(builder.toString());
    }

    @Nullable
    public CallRecordTreeNode getSelected() {
        return (CallRecordTreeNode) treeView.getSelectionModel().getSelectedItem();
    }

    public void dispose() {
//        this.tree.dispose();
    }

    public synchronized void refreshTreeView() {
        this.init();
        CallRecordTreeNode root = (CallRecordTreeNode) treeView.getRoot();
        setText(getTabName());
        root.refresh();
    }

    public synchronized void uploadChunk(CallRecordTreeChunk chunk) {
        try {
            if (database == null) {
                database = new InMemoryIndexFileBasedCallRecordDatabase(chunk.getProcessInfo().getMainClassName());
            }

            if (recordingInfo == null) {
                this.recordingInfo = chunk.getRecordingInfo();
            }

            database.persistBatch(
                    new CallEnterRecordList(chunk.getRequest().getRecordLog().getEnterRecords()),
                    new CallExitRecordList(chunk.getRequest().getRecordLog().getExitRecords()),
                    new MethodInfoList(chunk.getRequest().getMethodDescriptionList().getData()),
                    chunk.getRequest().getDescriptionList()
            );

            if (root == null) {
                root = database.find(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
