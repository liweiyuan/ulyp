package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordTree;
import com.ulyp.core.util.MethodMatcher;
import org.h2.jdbc.JdbcConnection;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class H2Test extends AbstractInstrumentationTest {

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

    @Test
    public void shouldRecordMainMethodIfSpecified() {

        CallRecordTree tree = runSubprocessWithUi(new TestSettingsBuilder()
                .setInstrumentedPackages("com.test", "org.h2")
                .setMainClassName(H2TestRun.class)
                .setMethodToRecord("main"));

        CallRecord root = tree.getRoot();

        Assert.assertTrue(root.getSubtreeNodeCount() > 4000);
    }
}
