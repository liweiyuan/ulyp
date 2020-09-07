[![Build Status](https://travis-ci.org/0xaa4eb/ulyp.svg?branch=master)](https://travis-ci.org/0xaa4eb/ulyp)[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2F0xaa4eb%2Fulyp.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2F0xaa4eb%2Fulyp?ref=badge_shield)

[![](https://tokei.rs/b1/github/0xaa4eb/ulyp)](https://github.com/0xaa4eb/ulyp)

# Preface
Ulyp is a proof-of-concept tracing instrumentation agent which records an execution of desired methods and sends them to UI. The agent should be provided only with two parameters: packages in which classes should be instrumented and a strating point in the following format: (simple class name).(method name). The starting point may be `Runnable.run` or it may contain a wildcard, like: `UserDao.*`.

The main purpose is mostly research of frameworks and  troubleshooting of heavy enterprise applications where frameworks like Hibernate or Spring may call thousands of methods. 
Please note, the agent has very high performance impact and it's very intrusive.

# Usage
Ulyp is bundled with very simple desktop UI written on JavaFX and agent itself.

Agent has the following props which are set as java system properties (via `-Dkey=value`)

<table border="1">
<tr>
		<th>Property</th>
		<th>Mandatory</th>
		<th>Default value</th>
		<th>Example</th>
		<th>Description</th>
</tr>
<tr><td>ulyp.ui-host</td><td>No</td><td>localhost</td><td>localhost</td><td>Target host for UI connection</td></tr>
<tr><td>ulyp.ui-port</td><td>No</td><td>13991</td><td>13991</td><td>Target port for UI connection</td></tr>

<tr><td>ulyp.max-depth</td><td>No</td><td>Integer.MAX_VALUE</td><td>20</td><td>Max depth of call trace tree. May be useful for limiting instrumentation data</td></tr>
<tr><td>ulyp.ui-port</td><td>No</td><td>13991</td><td>13991</td><td>Target port for UI connection</td></tr>

<tr><td>ulyp.log</td><td>No</td><td>-</td><td>Used as -Dulyp.log</td><td>Turns on agent logging</td></tr>
</table>

Example of running java app with the agent:

	java -javaagent:C:\Work\Tools\ulyp-agent-0.1\ulyp-agent-0.1.jar -Dulyp.packages=com.demo,org.hibernate,org.h2 -Dulyp.start-method=JpaProxyUserRepositoryIntegrationTest.sampleTestCase YourClassName

# Build

	./gradlew clean build test

# Simplest example: Fibbonaci numbers

	package com.example;

	public class FibbonaciTest {
	    @Test
	    public void test() {
		Assert.assertEquals(13, compute(7));
	    }

	    public static int compute(int n) {
		if (n <= 1)
		    return n;
		return compute(n - 1) + compute(n - 2);
	    }
	}

In order to activate ulyp the test should be executed with the following additional VM key: 

	-javaagent:C:\Work\ulyp\ulyp-agent\build\libs\ulyp-agent-0.2.jar
	
Specify "com" package to be instrumented and "FibbonaciTest.test" as tracing start method in UI and run the test: 

![Ulyp UI](https://github.com/0xaa4eb/ulyp/blob/master/images/fibbonaci.png)

# Example (hibernate + h2 database)
Simple hibernate test recording:
 
 	@Autowired
	private JpaProxyUserRepository repository;
  
	@Test
	public void sampleTestCase() {
		User dave = new User("Dave", "Mathews");
		dave = repository.save(dave);
	}
  
The whole method traces tree may be investigated in the UI:

![Ulyp UI](https://github.com/0xaa4eb/ulyp/blob/master/images/hibernate.png)


## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2F0xaa4eb%2Fulyp.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2F0xaa4eb%2Fulyp?ref=badge_large)