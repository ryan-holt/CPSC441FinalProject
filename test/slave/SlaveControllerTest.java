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

	static AssociationRuleRequest makeTestAssociationRuleRequest() {
		int question = 1;
		ArrayList<KeywordGroup> testKeywordGroup = new ArrayList<KeywordGroup>(Arrays.asList(
				new KeywordGroup("python", "java"),
				new KeywordGroup("c++", "python"),
				new KeywordGroup("java", "c++")
		));

		ArrayList<SurveyEntry> testEntries = new ArrayList<SurveyEntry>(Arrays.asList(
				new SurveyEntry("Jim", 1, "python", "java", "c++"),
				new SurveyEntry("Bob", 1, "python", "java"),
				new SurveyEntry("Frank", 1, "python", "c++")
		));

		return new AssociationRuleRequest(1, testKeywordGroup, testEntries);
	}
}