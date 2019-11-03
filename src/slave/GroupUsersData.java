package slave;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic data structure class to hold:
 * the number of occurences of a KeywordGroup
 * the users associated with those KeywordGroup
 */
class GroupUsersData {
	List<String> users;

	GroupUsersData() {
		users = new ArrayList<>();
	}

	GroupUsersData(List<String> users) {
		this.users = users;
	}

	int size() {
		return users.size();
	}
}
