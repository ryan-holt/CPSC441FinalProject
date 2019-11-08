package Master;

import util.*;
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

	private List<RuleCorrelationRequest> correlationRequests;
	private List<RuleCorrelationResponse> correlationResponses;

	/**
	 * Field representing the number of messages sent to slaves.
	 * Field is reset to zero after a batch task to slaves is completed
	 */
	private int sentMultithreadedMessagesCount;

	public RulesController(MasterController masterController) {
		this.masterController = masterController;
		ruleResponses = new ArrayList<>();
		correlationResponses = new ArrayList<>();
	}

	public void batchStartRuleRequests(int threadCount, Map<String, ClientSocketHandler> clientSocketHandlers) {
		batchStartRequests(ruleRequests, threadCount, clientSocketHandlers);
	}

	public void batchStartCorrelationRequests(int threadCount, Map<String, ClientSocketHandler> clientSocketHandlers) {
		batchStartRequests(correlationRequests, threadCount, clientSocketHandlers);
	}

	private <T extends Message> void batchStartRequests(List<T> requests, int threadCount, Map<String, ClientSocketHandler> clientSocketHandlers) {
		// Send all requests to different slaves
		int threadIndex = 0;
		sentMultithreadedMessagesCount = 0;
		ArrayList<ClientSocketHandler> clientSocketHandlersArrList = new ArrayList<>(clientSocketHandlers.values());

		while (threadIndex < threadCount && sentMultithreadedMessagesCount < requests.size()) {
			ClientSocketHandler clientSocketHandler = clientSocketHandlersArrList.get(threadIndex);

			clientSocketHandler.setNextMsgOut(requests.get(sentMultithreadedMessagesCount));

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

		for (int i = 0; i < ruleResponses.size() - 1; i++) {
			AssociationRuleResponse baseQuestion = ruleResponses.get(i);
			for (Rule baseRule : baseQuestion.getRules()) {
				ArrayList<Rule> otherRules = new ArrayList<>();
				for (int j = i + 1; j < ruleResponses.size(); j++) {
					AssociationRuleResponse question = ruleResponses.get(j);
					for (Rule rule : question.getRules()) {
						otherRules.add(rule);

					}
				}
				outputList.add(new RuleCorrelationRequest(baseRule, otherRules));
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
