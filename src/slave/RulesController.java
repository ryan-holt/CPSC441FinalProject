package slave;

import util.AssociationRuleRequest;
import util.KeywordGroup;
import util.SurveyEntry;

import java.util.*;
import java.util.stream.Collectors;

public class RulesController {
	/**
	 * Calculate the support and confidence of each keyword group for each survey entry
	 * @param request
	 */
	public Map<KeywordGroup, Double> calculateAssociationRules(AssociationRuleRequest request) {
		// Sort the groups by keyword
		List<KeywordGroup> groups = sortKeywordsInGroup(request.getKeywordGroups());
		List<SurveyEntry> entries = request.getSurveyEntries();

		Map<String, Integer> keywordCounts = new HashMap<>();
		Map<KeywordGroup, Integer> groupCounts = new HashMap<>();

		// Add the individual keywordCounts and the groups
		for (KeywordGroup group : groups) {
			groupCounts.put(group, 0);

			for (String keyword : group.getKeywords()) {
				keywordCounts.put(keyword, 0);
			}
		}

		countKeywordEntries(groups, entries, keywordCounts, groupCounts);

		Map<KeywordGroup, Double> scores = calculateRulesScores((double) entries.size(), groups, keywordCounts, groupCounts);

		return scores;
	}

	private List<KeywordGroup> sortKeywordsInGroup(List<KeywordGroup> groups) {
		// Sort the groups by keyword
		return groups.stream().map(group -> { group.sort(); return group; })
				.collect(Collectors.toList());
	}

	private void countKeywordEntries(List<KeywordGroup> groups, List<SurveyEntry> entries,
	                                 Map<String, Integer> keywordCounts, Map<KeywordGroup, Integer> groupCounts) {
		ArrayList<String> selections;
		for (SurveyEntry entry : entries) {
			selections = entry.getSelections();

			for (KeywordGroup group : groups) {
				ArrayList<String> keywords = group.getKeywords();
				if (selections.containsAll(keywords)) {
					incrementMapCount(groupCounts, group);
				}
			}

			for (Map.Entry<String, Integer> keywordEntry : keywordCounts.entrySet()) {
				if (selections.contains(keywordEntry.getKey())) {
					keywordEntry.setValue(keywordEntry.getValue() + 1); // RIP incrementMapCount, screw reusability
				}
			}
		}
	}

	private <K> void incrementMapCount(Map<K, Integer> map, K key) {
		map.put(key, map.get(key) + 1);
	}

	/**
	 * Calculate the association rule scores for support and lift by dividing the different counts of groups
	 * by entries count (support) and also dividing the different group counts by individual keywords (confidence)
	 * @param entriesCount
	 * @param groups
	 * @param keywordCounts
	 * @param groupCounts
	 * @return
	 */
	private LinkedHashMap<KeywordGroup, Double> calculateRulesScores(double entriesCount, List<KeywordGroup> groups,
	                                                       Map<String, Integer> keywordCounts, Map<KeywordGroup, Integer> groupCounts) {
		LinkedHashMap<KeywordGroup, Double> scores = new LinkedHashMap<>();

		int maxEntries = 10;
		KeywordGroup lowestScoreGroup = null;
		double lowestScore = 99999; // Impossibly high score, highest score = 1

		for (KeywordGroup group : groups) {
			ArrayList<String> keywords = group.getKeywords();
			double groupCount = (double) groupCounts.get(group);

			double supportScore = groupCount / entriesCount;

			for (String keyword : keywords) {
				double keywordCount = (double) keywordCounts.get(keyword);

				// Get the confidence score
				double confidenceScore = groupCount / keywordCount;
				double score = calculateAssociationScore(supportScore, confidenceScore);


				KeywordGroup orderedGroup = getOrderedKeywordGroup(group, keyword);
				if (scores.size() < maxEntries) {
					scores.put(orderedGroup, score);
					if (score < lowestScore) {
						lowestScore = score;
						lowestScoreGroup = orderedGroup;
					}

				} else if (score > lowestScore){
					scores.put(orderedGroup, score);
					// Evict the lowest score group
					scores.remove(lowestScoreGroup);
					// Find the new lowest score group
					for (Map.Entry<KeywordGroup, Double> entry : scores.entrySet()) {
						if (lowestScore > entry.getValue()) {
							lowestScoreGroup = entry.getKey();
							lowestScore = entry.getValue();
						}
					}
				}
			}
		}

		// Sort the map
		List<Map.Entry<KeywordGroup, Double>> scoresArr = new ArrayList<>(scores.entrySet());
		scores.clear();
		scoresArr.stream()
				.sorted(Comparator.comparingDouble(Map.Entry<KeywordGroup, Double>::getValue).reversed()) // Reverse sort
				.forEachOrdered(e -> scores.put(e.getKey(), e.getValue())); // Store values back into scores

		return scores;
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
