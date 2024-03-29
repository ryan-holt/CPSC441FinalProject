package slave;

import org.junit.jupiter.api.Test;
import util.AssociationRuleRequest;
import util.AssociationRuleResponse;
import util.KeywordGroup;
import util.Rule;

import java.util.List;
import java.util.*;

class SlaveRulesControllerTest {

	@Test
	void calculateAssociationRules() {
		SlaveRulesController rulesController = new SlaveRulesController();
		AssociationRuleRequest request = SlaveControllerTest.makeTestAssociationRuleRequest();

		AssociationRuleResponse msgOut = rulesController.calculateAssociationRules(request);
		List<Rule> out = msgOut.getRules();

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

//		assertEquals(6, out.size());
//
//		List<Rule> expectedOut = new ArrayList<>();
//		addTestRule(expectedOut, "java", "python", 0.9, "Bob", "Jim");
//		addTestRule(expectedOut, "python", "java", 2.0/3.0, "Bob", "Jim");
//		addTestRule(expectedOut, "c++", "python", 0.9, "Frank", "Jim");
//		addTestRule(expectedOut, "python", "c++", 2.0/3.0, "Frank", "Jim");
//		addTestRule(expectedOut, "c++", "java", 0.45, "Jim");
//		addTestRule(expectedOut, "java", "c++", 0.45, "Jim");
//
//		for (Rule rule : expectedOut) {
//			assertTrue(out.contains(expectedKey));
//		}
//
//		for (Map.Entry<KeywordGroup, Rule> expectedEntry : expectedOut.entrySet()) {
//			assertEquals(expectedEntry.getValue().getScore(), out.get(expectedEntry.getKey()).getScore(), 0.0001);
//		}
	}

	private void addTestRule(List<Rule> expectedOut, String keyword1, String keyword2, double score, String... users) {
		KeywordGroup grp = new KeywordGroup(keyword1, keyword2);
		expectedOut.add(new Rule(grp, score, Arrays.asList(users)));
	}
}