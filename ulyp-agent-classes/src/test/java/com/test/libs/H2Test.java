package com.test.libs;

import com.test.cases.AbstractInstrumentationTest;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.util.MethodMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class H2Test extends AbstractInstrumentationTest {

    @Test
    public void shouldRecordStatementCreation() {

        CallRecord root = runSubprocessWithUi(new TestSettingsBuilder()
                .setInstrumentedPackages("com.test", "org.h2")
                .setMainClassName(H2TestRun.class)
                .setMethodToRecord(MethodMatcher.parse("Connection.createStatement")));


        Assert.assertTrue(root.getSubtreeNodeCount() > 10);
    }

    @Test
    public void shouldRecordMainMethodIfSpecified() {

        CallRecord root = runSubprocessWithUi(new TestSettingsBuilder()
                .setInstrumentedPackages("com.test", "org.h2")
                .setMainClassName(H2TestRun.class)
                .setMethodToRecord("main"));


        Assert.assertTrue(root.getSubtreeNodeCount() > 4000);
    }

    static class H2TestRun {

        public static void main(String[] args) throws Exception {
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:", "sa", "")) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("create table test(id int primary key, name varchar)");
                }

                try (PreparedStatement statement = connection.prepareStatement("insert into test values(?, ?)")) {
                    statement.setInt(1, 42);
                    statement.setString(2, "Hello World");
                    statement.execute();
                }
            }
        }
    }
}
