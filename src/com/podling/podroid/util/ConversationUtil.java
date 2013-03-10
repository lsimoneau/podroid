package com.podling.podroid.util;

import org.the86.model.Conversation;

public class ConversationUtil {

	public static String replyCount(Conversation conversation) {
		int count = conversation.getPosts().size() - 1; // -1 for OP
		switch (count) {
		case 0:
			return "";
		case 1:
			return "1 reply";
		default:
			return String.format("%d replies", count);
		}
	}
}
