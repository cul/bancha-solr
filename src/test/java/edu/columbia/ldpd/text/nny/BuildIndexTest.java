package edu.columbia.ldpd.text.nny;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.impl.CollectingPageProcessor;

public class BuildIndexTest {

	private Configuration testConfig;
	@Before
	public void setUp() {
		testConfig = new Configuration("test");
		testConfig.set("xmlDir","src/test/resources/nny");
		testConfig.set("homeDir", ".");
	}
	@Test
	public void test() {
		CollectingPageProcessor<NNYRecord> proc = new CollectingPageProcessor<>();
		BuildIndex index = new BuildIndex(proc);
		try {
			index.run(testConfig, null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(53,proc.size());
		HashSet<String> ids = new HashSet<>();

		for (NNYRecord page: proc) {
			ids.add(proc.idFor(page));
		}
		assertEquals(53,ids.size());
	}

}
