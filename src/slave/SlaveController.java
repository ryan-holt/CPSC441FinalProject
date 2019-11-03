package slave;

import util.AssociationRuleRequest;
import util.KeywordGroup;
import util.Message;
import util.SurveyEntry;

import java.util.*;
import java.util.stream.Collectors;

public class SlaveController {
	RulesController rulesController;

	public SlaveController() {
		rulesController = new RulesController();
	}

	public void handleMessage(Message message) {
		String action = "requestAssociationRules"; // TODO hook into Message input
		switch (action) {
			case "requestAssociationRules":
				rulesController.calculateAssociationRules((AssociationRuleRequest) message);
				break;
			default:
				// TODO Throw exception
		}
	}


}
