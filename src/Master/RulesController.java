package Master;

import util.AssociationRuleRequest;
import util.AssociationRuleResponse;
import util.Rule;
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

	public void createRuleCorrelationRequests() {
		System.out.println("!!! inside createRuleCorrelationRequests"); // FIXME delete
	}

	public void setRuleRequests(List<AssociationRuleRequest> ruleRequests) {
		this.ruleRequests = ruleRequests;
	}

	public void clearRuleResponses() {
		clearSharedList(ruleResponses);
	}

	/**
	 * Append the element onto the sharedList. Used for shared resources in multithreading.
	 * @param sharedList
	 * @param appendElement
	 * @param <T>
	 */
	private synchronized <T> void addToSharedList(List<T> sharedList, T appendElement) {
		sharedList.add(appendElement);
	}

	/**
	 * Append the appendList onto the sharedList. Used for shared resources in multithreading
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

	private synchronized  <T> void clearSharedList(List<T> sharedList) {
		sharedList.clear();
	}
}
