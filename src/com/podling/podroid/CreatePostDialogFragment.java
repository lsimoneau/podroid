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

public class CreatePostDialogFragment extends DialogFragment {
	private PostService the86;
	private String groupSlug;
	private String conversationId;

	public static CreatePostDialogFragment newInstance(String groupSlug,
			String conversationId) {
		CreatePostDialogFragment dialog = new CreatePostDialogFragment();
		Bundle args = new Bundle();
		args.putString("groupSlug", groupSlug);
		args.putString("conversationId", conversationId);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		groupSlug = getArguments().getString("groupSlug");
		conversationId = getArguments().getString("conversationId");

		the86 = ((PodroidApplication) getActivity().getApplicationContext())
				.getThe86();

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
								// User cancelled the dialog
							}
						});

		return builder.create();
	}

	class CreatePostTask extends AsyncTask<String, Void, Post> {
		protected ProgressDialog dialog;

		public CreatePostTask(Context context) {
			// create a progress dialog
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
				return the86.createPost(groupSlug, conversationId, content);
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Post post) {
			// populate(conversations);
			dialog.dismiss();
		}

	}
}
