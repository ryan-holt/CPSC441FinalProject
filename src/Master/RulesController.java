package Master;

import util.*;
import util.sockethandler.ClientSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

	public void batchStartRuleRequests(int threadCount, List<ClientSocketHandler> clientSocketHandlers) {
		batchStartRequests(ruleRequests, threadCount, clientSocketHandlers);
	}

	public void batchStartCorrelationRequests(int threadCount, List<ClientSocketHandler> clientSocketHandlers) {
		batchStartRequests(correlationRequests, threadCount, clientSocketHandlers);
	}

	private <T extends Message> void batchStartRequests(List<T> requests, int threadCount, List<ClientSocketHandler> clientSocketHandlers) {
		// Send all requests to different slaves
		int threadIndex = 0;
		sentMultithreadedMessagesCount = 0;

		while (threadIndex < threadCount && sentMultithreadedMessagesCount < requests.size()) {
			ClientSocketHandler clientSocketHandler = clientSocketHandlers.get(threadIndex); // TODO Optimize by using iterator
			clientSocketHandler.setNextMsgOut(requests.get(sentMultithreadedMessagesCount));

			sentMultithreadedMessagesCount++;
			threadIndex++;
		}
	}

	public boolean addRuleResponse(AssociationRuleResponse ruleResponse, ClientSocketHandler clientSocketHandler) {
		return addResponse(ruleRequests, ruleResponses, ruleResponse, clientSocketHandler);
	}

	public boolean addCorrelationResponse(RuleCorrelationResponse correlationResponse, ClientSocketHandler clientSocketHandler) {
		return addResponse(correlationRequests, correlationResponses, correlationResponse, clientSocketHandler);
	}

	private <T extends Message, S extends HostAddressMessage>
	boolean addResponse(List<T> requests, List<S> responses, S response, ClientSocketHandler clientSocketHandler) {

		addToSharedList(responses, response);

		// Check if any other messages need to be sent still
		if (sentMultithreadedMessagesCount < requests.size()) {
			clientSocketHandler.setNextMsgOut(requests.get(sentMultithreadedMessagesCount));
			sentMultithreadedMessagesCount++;

			return true;
		}

		return false;
	}

	/**
	 * Returns an arraylist of rule correlation requests
	 *
	 * @return the arraylist of rule correlation requests
	 */
	void createRuleCorrelationRequests() {
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

		correlationRequests = outputList;
	}

	public void setRuleRequests(List<AssociationRuleRequest> ruleRequests) {
		this.ruleRequests = ruleRequests;
	}

	public List<RulesCorrelation> getTopCorrelations() {
		int maxEntries = 30;

		// Get all the correlations out of their individual responses and into a single list
		List<RulesCorrelation> correlations = new ArrayList<>(correlationResponses.size() * maxEntries);
		correlationResponses.stream().forEach(r -> correlations.addAll(r.getCorrelations()));

		// Cut down on the list to the top 10 only
		return correlations.stream()
				.filter(distinctByKey(RulesCorrelation::similarHashCode))
				.sorted((a, b) -> -1 * ((Double) a.getScore()).compareTo(b.getScore()))
				.limit(maxEntries)
				.collect(Collectors.toList());
	}

	public void clearRuleResponses() {
		clearSharedList(ruleResponses);
	}

	public void clearCorrelationResponses() {
		clearSharedList(correlationResponses);
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

	private synchronized <T> void clearSharedList(List<T> sharedList) {
		sharedList.clear();
	}

	// TODO Move to a different spot
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}
}
