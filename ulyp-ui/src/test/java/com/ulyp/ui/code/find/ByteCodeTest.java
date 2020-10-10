package com.ulyp.ui.code.find;

import com.ulyp.ui.code.SourceCode;
import com.ulyp.ui.util.StreamDrainer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class ByteCodeTest {

    @Test
    public void test() throws Exception {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ProcessTab.class");

        Assert.assertNotNull(resourceAsStream);

        ByteCode processTab = new ByteCode("ProcessTab", StreamDrainer.DEFAULT.drain(resourceAsStream));

        SourceCode decompiled = processTab.decompile();

        Assert.assertThat(decompiled.getCode(), Matchers.containsString("import org.springframework.stereotype.Component;"));
        Assert.assertThat(decompiled.getCode(), Matchers.containsString("@Component"));
        Assert.assertThat(decompiled.getCode(), Matchers.containsString("public class ProcessTab extends Tab {"));
    }
}