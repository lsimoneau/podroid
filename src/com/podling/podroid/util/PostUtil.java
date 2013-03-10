package com.podling.podroid.util;

import org.the86.model.Post;

public class PostUtil {

	public static String likeCount(Post post) {
		int count = post.getLikes().size();
		switch (count) {
		case 0:
			return "";
		case 1:
			return "1 like";
		default:
			return String.format("%d likes", count);
		}
	}
}
