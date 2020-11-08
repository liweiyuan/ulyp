package com.ulyp.ui;

import com.ulyp.core.CallEnterRecordList;
import com.ulyp.core.CallExitRecordList;
import com.ulyp.core.CallRecord;
import com.ulyp.core.MethodInfoList;
import com.ulyp.core.impl.FileBasedCallRecordDatabase;
import com.ulyp.transport.RecordingInfo;
import com.ulyp.transport.TStackTraceElement;
import com.ulyp.ui.code.SourceCode;
import com.ulyp.ui.code.SourceCodeView;
import com.ulyp.ui.code.find.SourceCodeFinder;
import com.ulyp.ui.font.FontSizeChanger;
import com.ulyp.ui.util.ResizeEvent;
import com.ulyp.ui.util.ResizeEventSupportingScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.sql.Timestamp;

@Component
@Scope(value = "prototype")
public class CallRecordTreeTab extends Tab {

    private final Region parent;
    private final CallRecord root;
    private final FileBasedCallRecordDatabase database;
    private final RecordingInfo recordingInfo;

    private TreeView<CallTreeNodeContent> treeView;

    @Autowired
    private SourceCodeView sourceCodeView;
    @Autowired
    private RenderSettings renderSettings;
    @Autowired
    private FontSizeChanger fontSizeChanger;

    @SuppressWarnings("unchecked")
    public CallRecordTreeTab(Region parent, CallRecordTreeChunk chunk) {
        this.parent = parent;

        try {
            database = new FileBasedCallRecordDatabase(chunk.getProcessInfo().getMainClassName());

            database.persistBatch(
                    new CallEnterRecordList(chunk.getRequest().getRecordLog().getEnterRecords()),
                    new CallExitRecordList(chunk.getRequest().getRecordLog().getExitRecords()),
                    new MethodInfoList(chunk.getRequest().getMethodDescriptionList().getData()),
                    chunk.getRequest().getDescriptionList()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.root = database.find(0);
        this.recordingInfo = chunk.getRecordingInfo();
    }

    @PostConstruct
    public void init() {
        treeView = new TreeView<>(new CallRecordTreeNode(database, root.getId(), renderSettings));
        treeView.prefHeightProperty().bind(parent.heightProperty());
        treeView.prefWidthProperty().bind(parent.widthProperty());

        SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(recordingInfo.getProcessInfo().getClasspathList());

        treeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    CallRecordTreeNode callRecord = (CallRecordTreeNode) newValue;
                    if (callRecord != null && callRecord.getCallRecord() != null) {
                        SourceCode sourceCode = sourceCodeFinder.find(callRecord.getCallRecord().getClassName());
                        sourceCodeView.setText(sourceCode, callRecord.getCallRecord().getMethodName());
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
    }

    public String getTabName() {
        boolean complete = database.find(0).isComplete();

        return root.getMethodName() + "(" + 0 + ", life=" + recordingInfo.getLifetimeMillis() + " ms, nodes=" + database.countAll() + ")"
                + (complete ? " complete" : "");
    }

    private Tooltip getTooltipText() {

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

    public void uploadChunk(CallRecordTreeChunk chunk) {
        try {
            database.persistBatch(
                    new CallEnterRecordList(chunk.getRequest().getRecordLog().getEnterRecords()),
                    new CallExitRecordList(chunk.getRequest().getRecordLog().getExitRecords()),
                    new MethodInfoList(chunk.getRequest().getMethodDescriptionList().getData()),
                    chunk.getRequest().getDescriptionList()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CallRecordTreeNode root = (CallRecordTreeNode) treeView.getRoot();
        setText(getTabName());
        root.refresh();
    }
}
