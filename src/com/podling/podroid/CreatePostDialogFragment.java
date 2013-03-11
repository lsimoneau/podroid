package com.podling.podroid;

import org.the86.PostService;
import org.the86.exception.The86Exception;
import org.the86.model.Post;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.podling.podroid.posts.PostsActivity;
import com.podling.podroid.util.The86Util;

public class CreatePostDialogFragment extends DialogFragment {
	private PostService the86;
	private String groupSlug;
	private String conversationId;
	private String inReplyToId;
	// TODO this is a pretty crap encapsulation breakage - consider broadcasts?
	private PostsActivity postActivity;

	public static CreatePostDialogFragment newInstance(String groupSlug,
			String conversationId) {
		return newInstance(groupSlug, conversationId, null);
	}

	public static CreatePostDialogFragment newInstance(String groupSlug,
			String conversationId, String inReplyToId) {
		CreatePostDialogFragment dialog = new CreatePostDialogFragment();
		Bundle args = new Bundle();
		args.putString("groupSlug", groupSlug);
		args.putString("conversationId", conversationId);
		args.putString("inReplyToId", inReplyToId);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setupFragment();
		postActivity = (PostsActivity) getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.create_post_dialog, null);
		final EditText content = (EditText) view
				.findViewById(R.id.create_post_content);

		builder.setView(view)
				.setPositiveButton("post",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new CreatePostTask(getActivity())
										.execute(content.getText().toString());
							}
						})
				.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// do nothing
							}
						});

		return builder.create();
	}

	private void setupFragment() {
		Bundle args = getArguments();
		groupSlug = args.getString("groupSlug");
		conversationId = args.getString("conversationId");
		inReplyToId = args.getString("inReplyToId");

		the86 = The86Util.get(getActivity());
	}

	class CreatePostTask extends AsyncTask<String, Void, Post> {
		protected ProgressDialog dialog;

		public CreatePostTask(Context context) {
			dialog = new ProgressDialog(context);
			dialog.setMessage("posting");
			dialog.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		protected Post doInBackground(String... params) {
			String content = params[0];
			try {
				return the86.createPost(groupSlug, conversationId, content,
						inReplyToId);
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Post post) {
			postActivity.refreshData();
			dialog.dismiss();
		}
	}
}
