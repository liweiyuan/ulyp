package com.ulyp.ui.code.find;

import com.ulyp.core.process.Classpath;
import com.ulyp.ui.code.SourceCode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SourceCodeFinderTest {

    @Test
    public void shouldFindSourceCodeFromJunitLibraryInCurrentClasspath() {
        SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(new Classpath().toList());

        SourceCode sourceCode = sourceCodeFinder.find("org.junit.Test");


        assertThat(sourceCode.getCode(), containsString("@Retention(RetentionPolicy.RUNTIME)"));
        assertThat(sourceCode.getCode(), containsString("public @interface Test {"));

        assertThat(sourceCode.getClassName(), is("org.junit.Test"));
    }

    @Test
    public void shouldFindBytecodeAndDecompile() {
        SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(Arrays.asList(Paths.get("src", "test", "resources", "ProcessTab.jar").toString()));

        SourceCode sourceCode = sourceCodeFinder.find("com.ulyp.ui.ProcessTab");

        Assert.assertThat(sourceCode.getCode(), Matchers.containsString("// Decompiled from"));
        Assert.assertThat(sourceCode.getCode(), Matchers.containsString("import org.springframework.stereotype.Component;"));
        Assert.assertThat(sourceCode.getCode(), Matchers.containsString("@Component"));
        Assert.assertThat(sourceCode.getCode(), Matchers.containsString("public class ProcessTab extends Tab {"));
    }
}