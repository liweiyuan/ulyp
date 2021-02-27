package com.ulyp.database;

import com.ulyp.core.MethodInfo;
import com.ulyp.core.MethodInfoList;
import com.ulyp.core.TestAgentRuntime;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DatabaseDatabaseWriterImplTest {

    private final TestAgentRuntime agentRuntime = new TestAgentRuntime();

    @Test
    public void test() throws IOException {
        DatabaseWriterImpl databaseWriterImpl = new DatabaseWriterImpl(Paths.get("C:/Temp/t2.dat"));

        MethodInfoList methodInfos = new MethodInfoList();

        MethodInfo toStringMethod = new MethodInfo(
                100,
                "toString",
                false,
                false,
                true,
                new ArrayList<>(),
                agentRuntime.get(String.class),
                agentRuntime.get(DatabaseDatabaseWriterImplTest.class)
        );

        methodInfos.add(toStringMethod);

        /*databaseWriterImpl.write(
                new CallRecordTreeRequest(
                        null,

                )
        );*/

    }
}