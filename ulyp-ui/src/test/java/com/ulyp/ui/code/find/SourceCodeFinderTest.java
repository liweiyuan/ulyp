package com.ulyp.ui.code.find;

import com.ulyp.core.process.Classpath;
import com.ulyp.ui.code.SourceCode;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SourceCodeFinderTest {

    private final SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(new Classpath().toList());

    @Test
    public void shouldFindSourceCodeFromJunitLibraryInCurrentClasspath() {
        SourceCode sourceCode = sourceCodeFinder.find("org.junit.Test");


        assertThat(sourceCode.getCode(), containsString("@Retention(RetentionPolicy.RUNTIME)"));
        assertThat(sourceCode.getCode(), containsString("public @interface Test {"));

        assertThat(sourceCode.getClassName(), is("org.junit.Test"));
    }
}