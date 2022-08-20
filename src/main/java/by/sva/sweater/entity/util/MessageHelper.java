package by.sva.sweater.entity.util;

import by.sva.sweater.entity.User;

public abstract class MessageHelper {
	public static String getAuthorName(User author) {
		return author != null ? author.getUsername() : "<none>";
	}

}
