package slave;

import org.junit.jupiter.api.Test;
import util.AssociationRuleRequest;
import util.KeywordGroup;
import util.SurveyEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SlaveControllerTest {

	@Test
	void handleMessage() {
		SlaveController slaveController = new SlaveController();
		AssociationRuleRequest request = makeTestAssociationRuleRequest();

		slaveController.handleMessage(request);
		// TODO finish this test
	}

	@Test
	void calculateAssociationRules() {
		SlaveController slaveController = new SlaveController();
		AssociationRuleRequest request = makeTestAssociationRuleRequest();

		Map<KeywordGroup, Double> out = slaveController.calculateAssociationRules(request);

		/*
			base
				python: 3
				java: 2
				c++: 2
				entries: 3
				python, java: 2
				c++, python: 2
				java, c++: 1

			support:
				python, java: 0.666667
				c++, python: 0.666667,
				java, c++: 0.333333

			confidence:
				java, python / java: 1.0
				python, java / python: 0.666667
				c++, python / c++: 1.0
				python, c++ / python: 0.6666667
				c++, java / c++: 0.5
				java, c++ / java: 0.5

			scores:
				java, python / java:        0.9
				python, java / python:      0.66667
				c++, python / c++:          0.9
				python, c++ / python:       0.66667
				c++, java / c++:            0.45
				java, c++ / java:           0.45
		 */

		assertEquals(6, out.size());

		Map<KeywordGroup, Double> expectedOut = new HashMap<>();
		expectedOut.put(new KeywordGroup("java", "python"), 0.9);
		expectedOut.put(new KeywordGroup("python", "java"), 2.0/3.0);
		expectedOut.put(new KeywordGroup("c++", "python"), 0.9);
		expectedOut.put(new KeywordGroup("python", "c++"), 2.0/3.0);
		expectedOut.put(new KeywordGroup("c++", "java"), 0.45);
		expectedOut.put(new KeywordGroup("java", "c++"), 0.45);

		for (KeywordGroup expectedKey : expectedOut.keySet()) {
			assertTrue(out.containsKey(expectedKey));
		}

		for (Map.Entry<KeywordGroup, Double> expectedEntry : expectedOut.entrySet()) {
			assertEquals(expectedEntry.getValue(), out.get(expectedEntry.getKey()), 0.0001);
		}
	}

	private AssociationRuleRequest makeTestAssociationRuleRequest() {
		int question = 1;
		ArrayList<KeywordGroup> testKeywordGroup = new ArrayList<KeywordGroup>(Arrays.asList(
				new KeywordGroup("python", "java"),
				new KeywordGroup("c++", "python"),
				new KeywordGroup("java", "c++")
		));

		ArrayList<SurveyEntry> testEntries = new ArrayList<SurveyEntry>(Arrays.asList(
				new SurveyEntry("Jim", "python", "java", "c++"),
				new SurveyEntry("Bob", "python", "java"),
				new SurveyEntry("Frank", "python", "c++")
		));

		return new AssociationRuleRequest(1, testKeywordGroup, testEntries);
	}
}