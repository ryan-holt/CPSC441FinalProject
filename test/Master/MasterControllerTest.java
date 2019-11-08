package Master;

import org.junit.jupiter.api.Test;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MasterControllerTest {

	@Test
	void communicateWithClient() {
	}

	@Test
	void handleMessage() {
	}

	@Test
	public void prepareAssociationRuleRequests() {
		//TODO: REMOVE ONCE DONE TESTING ASSOCIATIONSTUFF HERE
		Map<Integer, AssociationRuleResponse> testMap = new HashMap<>();
		ArrayList<Rule> testArray = new ArrayList<>();
		ArrayList<String> tempList = new ArrayList<>();
		tempList.add("Bob"); tempList.add("Frank"); tempList.add("Jane");
		testArray.add(new Rule(new KeywordGroup("Python", "Java"), 0.97, tempList));
		AssociationRuleResponse testResponse = new AssociationRuleResponse(1, testArray);
		testMap.put(1, testResponse);
		ArrayList<Rule> testArray2 = new ArrayList<>();
		ArrayList<String> tempList2 = new ArrayList<>();
		tempList2.add("Alice"); tempList2.add( "Jane"); tempList2.add("Kevin");
		testArray2.add(new Rule(new KeywordGroup("Superstore", "H&M"), 0.92, tempList2));
		ArrayList<String> tempList3 = new ArrayList<>();
		tempList3.add("Frank"); tempList3.add("Uaran"); tempList3.add("Reacat");
		testArray2.add(new Rule(new KeywordGroup("Superstore", "Walmart"), 0.87, tempList3));
		AssociationRuleResponse testResponse2 = new AssociationRuleResponse(2, testArray2);
		testMap.put(2, testResponse2);
		ArrayList<RuleCorrelationRequest> correlationRequestList = createRuleCorrelationRequests(testMap);

		//TODO: REMOVE ONCE DONE
		ArrayList<RulesCorrelation> correlationScoresList = new ArrayList<>();
		ArrayList<KeywordGroup> keywordList = new ArrayList<>();
		keywordList.add(new KeywordGroup("Ryan", "Holt"));
		keywordList.add(new KeywordGroup("Tyler", "Lam"));
		correlationScoresList.add(new RulesCorrelation(keywordList, 0.69));
		fileHandler.writeScoresToFile(correlationScoresList);
		String result = fileHandler.getListOfHistoricalCorrelation();
	}
}