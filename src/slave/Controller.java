package slave;

import util.AssociationRuleRequest;
import util.KeywordGroup;
import util.Message;
import util.SurveyEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller {
	public void handleMessage(Message message) {
		String action = "requestAssociationRules"; // TODO hook into Message input
		switch (action) {
			case "requestAssociationRules":
				calculateAssociationRules((AssociationRuleRequest) message);
				break;
			default:
				// TODO Throw exception
		}
	}

	private void calculateAssociationRules(AssociationRuleRequest request) {
		ArrayList<KeywordGroup> groups = request.getKeywordGroups();
		ArrayList<SurveyEntry> entries = request.getSurveyEntries();

		Map<String, Integer> keywordCount = new HashMap<>();

		ArrayList<String> selections;
		for (SurveyEntry entry : entries) {
			selections = entry.getSelections();
			
		}
	}
}
