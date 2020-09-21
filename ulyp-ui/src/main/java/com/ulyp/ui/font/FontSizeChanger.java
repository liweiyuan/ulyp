package com.ulyp.ui.font;

import javafx.scene.Scene;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class FontSizeChanger {

    private static final String STYLE_PREFIX = "ulyp-ctt-font-style";

    private int currentFontSize = 14;

    public void upscale(Scene scene) {
        int font = currentFontSize++;

        refreshFont(scene, font);
    }

    public void downscale(Scene scene) {
        int font = currentFontSize--;

        refreshFont(scene, font);
    }

    private void refreshFont(Scene scene, int font) {
        try {
            Path path = Files.createTempFile(STYLE_PREFIX, null);
            path.toFile().deleteOnExit();

            Files.write(
                    path,
                    (".ulyp-ctt {\n" +
                            "    -fx-font-family: consolas;\n" +
                            "    -fx-font-size: " + font + "px;\n" +
                            "}").getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.WRITE
            );


            Integer index = null;
            for (int i = 0; i < scene.getStylesheets().size(); i++) {
                if (scene.getStylesheets().get(i).contains(STYLE_PREFIX)) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                scene.getStylesheets().set(index, path.toFile().toURI().toString());
            } else {
                scene.getStylesheets().add(path.toFile().toURI().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
