package com.ulyp.ui.code;

import javafx.embed.swing.SwingNode;
import javafx.scene.control.ScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class SourceCodeView extends SwingNode {

    private final RTextScrollPane textScrollPane;
    private final RSyntaxTextArea textArea;

    public SourceCodeView() {

        textArea = new RSyntaxTextArea();

        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);

        textScrollPane = new RTextScrollPane(textArea);

        Theme theme;
        try {
            theme = Theme.load(getClass().getResourceAsStream("/rsyntax-dark.xml"));
            theme.apply(textArea);
        } catch (IOException e) {
            throw new RuntimeException("Could not load theme", e);
        }

        setContent(textScrollPane);
    }

    public void setText(SourceCode code) {
        SwingUtilities.invokeLater(
                () -> {

                    if (code == null) {
                        this.textArea.setText(null);
                    } else {
                        this.textArea.setText(code.getCode());
                    }
                }
        );
    }
}
