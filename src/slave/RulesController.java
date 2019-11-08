package slave;

import util.AssociationRuleRequest;
import util.KeywordGroup;
import util.Rule;
import util.SurveyEntry;

import java.security.acl.Group;
import java.util.*;
import java.util.stream.Collectors;

public class RulesController {
	/**
	 * Calculate the support and confidence of each keyword group for each survey entry
	 * @param request
	 */
	public Map<KeywordGroup, Rule> calculateAssociationRules(AssociationRuleRequest request) {
		// Sort the groups by keyword
		List<KeywordGroup> groups = sortKeywordsInGroup(request.getKeywordGroups());
		List<SurveyEntry> entries = request.getSurveyEntries();

		Map<String, Integer> keywordCounts = new HashMap<>();
		Map<KeywordGroup, GroupUsersData> groupUsers = new HashMap<>();

		// Add the individual keywordCounts and the groups
		for (KeywordGroup group : groups) {
			groupUsers.put(group, new GroupUsersData());

			for (String keyword : group.getKeywords()) {
				keywordCounts.put(keyword, 0);
			}
		}

		countKeywordEntries(groups, entries, keywordCounts, groupUsers);

		LinkedHashMap<KeywordGroup, Rule> rules = calculateRulesScores((double) entries.size(), groups, keywordCounts, groupUsers);

		return rules;
	}

	private List<KeywordGroup> sortKeywordsInGroup(List<KeywordGroup> groups) {
		// Sort the groups by keyword
		return groups.stream().map(group -> { group.sort(); return group; })
				.collect(Collectors.toList());
	}

	private void countKeywordEntries(List<KeywordGroup> groups, List<SurveyEntry> entries,
	                                 Map<String, Integer> keywordCounts,
	                                 Map<KeywordGroup, GroupUsersData> groupUsers) {
		ArrayList<String> selections;
		List<String> users = new ArrayList<>();
		for (SurveyEntry entry : entries) {
			selections = entry.getSelections();

			for (KeywordGroup group : groups) {
				if (selections.containsAll(group.getKeywords())) {
					groupUsers.get(group).users.add(entry.getUser());
				}
			}

			for (Map.Entry<String, Integer> keywordEntry : keywordCounts.entrySet()) {
				if (selections.contains(keywordEntry.getKey())) {
					keywordEntry.setValue(keywordEntry.getValue() + 1);
				}
			}
		}
	}

	/**
	 * Calculate the association rule scores for support and lift by dividing the different counts of groups
	 * by entries count (support) and also dividing the different group counts by individual keywords (confidence)
	 * @param entriesCount
	 * @param groups
	 * @param keywordCounts
	 * @param groupUsers
	 * @return
	 */
	private LinkedHashMap<KeywordGroup, Rule> calculateRulesScores(double entriesCount, List<KeywordGroup> groups,
	                                                       Map<String, Integer> keywordCounts, Map<KeywordGroup, GroupUsersData> groupUsers) {
//		LinkedHashMap<KeywordGroup, Double> scores = new LinkedHashMap<>();
		LinkedHashMap<KeywordGroup, Rule> rules = new LinkedHashMap<>();
		int maxEntries = 10;
		KeywordGroup lowestScoreGroup = null;
		double lowestScore = 99999; // Impossibly high score, highest score = 1

		for (KeywordGroup group : groups) {
			ArrayList<String> keywords = group.getKeywords();
			GroupUsersData data = groupUsers.get(group);
			double groupCount = (double) data.size();
			List<String> users = data.users; // Get the users

			double supportScore = groupCount / entriesCount;

			for (String keyword : keywords) {
				double keywordCount = (double) keywordCounts.get(keyword);

				// Get the confidence score
				double confidenceScore = groupCount / keywordCount;
				double score = calculateAssociationScore(supportScore, confidenceScore);

				KeywordGroup orderedGroup = getOrderedKeywordGroup(group, keyword);
				Rule rule = new Rule(orderedGroup, score, users);
				if (rules.size() < maxEntries) {
					rules.put(orderedGroup, rule);
					if (score < lowestScore) {
						lowestScore = score;
						lowestScoreGroup = orderedGroup;
					}

				} else if (score > lowestScore){
					rules.put(orderedGroup, rule);
					// Evict the lowest score group
					rules.remove(lowestScoreGroup);
					// Find the new lowest score group
					for (Map.Entry<KeywordGroup, Rule> entry : rules.entrySet()) {
						if (lowestScore > entry.getValue().getScore()) {
							lowestScoreGroup = entry.getKey();
							lowestScore = entry.getValue().getScore();
						}
					}
				}
			}
		}
		
		rules.forEach((k, v) -> v.sortUsers()); // Sort the users

		// Sort the map
		List<Map.Entry<KeywordGroup, Rule>> rulesArr = new ArrayList<>(rules.entrySet());
		rules.clear();
		rulesArr.stream()
				.sorted((a, b) -> (int) (a.getValue().getScore() - b.getValue().getScore())) // Reverse sort
//				.forEachOrdered(e -> rules.put(e.getKey(), e.getValue())); // Store values back into rules
				.collect(Collectors.toList()); // Store values back into rules

		return rules;
	}

	/**
	 * Get a copy of the keyword group where the first entry of the keywordGroup.keywords is the firstKeyword
	 * @param group
	 * @param firstKeyword
	 * @return
	 */
	private KeywordGroup getOrderedKeywordGroup(KeywordGroup group, String firstKeyword) {
		ArrayList<String> orderedKeywords = (ArrayList<String>) group.getKeywords().clone();
		orderedKeywords.remove(firstKeyword);
		orderedKeywords.add(0, firstKeyword);
		return new KeywordGroup(orderedKeywords);
	}

	private double calculateAssociationScore(double supportScore, double confidenceScore) {
		double supportWeight = 0.3, confidenceWeight = 0.7;
		return supportWeight * supportScore + confidenceWeight * confidenceScore;
	}
}
