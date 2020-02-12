# Preface
Ulyp is a proof-of-concept tracing instrumentation agent which records an execution of desired methods and sends them to UI. The main purpose is mostly research of frameworks and  troubleshooting of heavy enterprise applications where frameworks like Hibernate or Spring may call thousands of methods. 
Please note, the agent has very high performance impact and it's very intrusive.

# Usage
Ulyp is bundled with very simple desktop UI written on JavaFX and agent itself.

# Examples
Simple hibernate test recording:
 
 	@Autowired
	private JpaProxyUserRepository repository;
  
	@Test
	public void sampleTestCase() {
		User dave = new User("Dave", "Mathews");
		dave = repository.save(dave);
	}
  
The whole method traces tree could be investigated in the UI:

![Ulyp UI](https://github.com/0xaa4eb/ulyp/blob/master/images/hibernate.png)
