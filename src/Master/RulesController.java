package Master;

import util.AssociationRuleRequest;
import util.AssociationRuleResponse;
import util.Rule;
import util.RuleCorrelationRequest;
import util.sockethandler.ClientSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a controller for the MASTER relating to rules
 */
public class RulesController {
	private MasterController masterController;

	private List<AssociationRuleRequest> ruleRequests;
	private List<AssociationRuleResponse> ruleResponses;

	/**
	 * Field representing the number of messages sent to slaves.
	 * Field is reset to zero after a batch task to slaves is completed
	 */
	private int sentMultithreadedMessagesCount;

	public RulesController(MasterController masterController) {
		this.masterController = masterController;
		ruleResponses = new ArrayList<>();
	}

	public void batchStartRuleRequests(int threadCount, Map<String, ClientSocketHandler> clientSocketHandlers) {
		// Send all requests to different slaves
		int threadIndex = 0;
		sentMultithreadedMessagesCount = 0;
		ArrayList<ClientSocketHandler> clientSocketHandlersArrList = new ArrayList<>(clientSocketHandlers.values());

		while (threadIndex < threadCount && sentMultithreadedMessagesCount < ruleRequests.size()) {
			ClientSocketHandler clientSocketHandler = clientSocketHandlersArrList.get(threadIndex);

			clientSocketHandler.setNextMsgOut(ruleRequests.get(sentMultithreadedMessagesCount));

			sentMultithreadedMessagesCount++;
			threadIndex++;
		}
	}

	public ClientSocketHandler addRuleResponse(AssociationRuleResponse ruleResponse, Map<String, ClientSocketHandler> clientSocketHandlers) {
		addToSharedList(ruleResponses, ruleResponse);

		// Check if any other messages need to be sent still
		if (sentMultithreadedMessagesCount < ruleRequests.size()) {
			ClientSocketHandler clientSocketHandler = clientSocketHandlers.get(ruleResponse.getHostIP());
			clientSocketHandler.setNextMsgOut(ruleRequests.get(sentMultithreadedMessagesCount));
			sentMultithreadedMessagesCount++;

			return clientSocketHandler;
		}

		return null;
	}

	/**
	 * Returns an arraylist of rule correlation requests
	 *
	 * @param ruleResponses the rule responses
	 * @return the arraylist of rule correlation requests
	 */
	ArrayList<RuleCorrelationRequest> createRuleCorrelationRequests() {
		ArrayList<RuleCorrelationRequest> outputList = new ArrayList<RuleCorrelationRequest>();
		for (int i = 1; i <= ruleResponses.size(); i++) {
			for (int j = 0; j < ruleResponses.get(i).getRules().size(); j++) {
				if (i + 1 <= ruleResponses.size()) {
					ArrayList<Rule> ruleCorrelationArray = new ArrayList<>();
					ruleCorrelationArray.add(ruleResponses.get(i).getRules().get(j));
					outputList.add(new RuleCorrelationRequest("baseRule", ruleCorrelationArray));
					for (int k = i + 1; k <= ruleResponses.size(); k++) {
						ruleCorrelationArray = new ArrayList<>();
						for (int l = 0; l < ruleResponses.get(k).getRules().size(); l++) {
							ruleCorrelationArray.add(ruleResponses.get(k).getRules().get(l));
						}
						outputList.add(new RuleCorrelationRequest("rule", ruleCorrelationArray));
					}
				}
			}
		}
		return outputList;
	}

	public void setRuleRequests(List<AssociationRuleRequest> ruleRequests) {
		this.ruleRequests = ruleRequests;
	}

	public void clearRuleResponses() {
		clearSharedList(ruleResponses);
	}

	/**
	 * Append the element onto the sharedList. Used for shared resources in multithreading.
	 *
	 * @param sharedList
	 * @param appendElement
	 * @param <T>
	 */
	private synchronized <T> void addToSharedList(List<T> sharedList, T appendElement) {
		sharedList.add(appendElement);
	}

	/**
	 * Append the appendList onto the sharedList. Used for shared resources in multithreading
	 *
	 * @param sharedList
	 * @param appendList
	 * @param <T>
	 */
	private synchronized <T> void addToSharedList(List<T> sharedList, List<T> appendList) {
		sharedList.addAll(appendList);
	}

	/**
	 * Get the next entry from a shared list. Used for shared resources in multithreading
	 */
	private synchronized <T> T popSharedElement(List<T> sharedList) {
		return sharedList.remove(0);
	}

	private synchronized <T> void clearSharedList(List<T> sharedList) {
		sharedList.clear();
	}
}
