package com.podling.podroid;

import org.the86.ConversationService;
import org.the86.exception.The86Exception;
import org.the86.model.Conversation;

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

public class CreateConversationDialogFragment extends DialogFragment {
	private ConversationService the86;
	private String groupSlug;

	public static CreateConversationDialogFragment newInstance(String groupSlug) {
		CreateConversationDialogFragment dialog = new CreateConversationDialogFragment();
		Bundle args = new Bundle();
		args.putString("groupSlug", groupSlug);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		groupSlug = getArguments().getString("groupSlug");

		the86 = ((PodroidApplication) getActivity().getApplicationContext())
				.getThe86();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.create_conversation_dialog, null);
		final EditText content = (EditText) view
				.findViewById(R.id.create_conversation_content);
		builder.setView(view)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new CreateConversationTask(getActivity())
								.execute(content.getText().toString());
					}
				})
				.setNegativeButton("nope",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});

		return builder.create();
	}

	class CreateConversationTask extends AsyncTask<String, Void, Conversation> {
		protected ProgressDialog dialog;

		public CreateConversationTask(Context context) {
			// create a progress dialog
			dialog = new ProgressDialog(context);
			dialog.setMessage("creating conversation");
			dialog.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		protected Conversation doInBackground(String... params) {
			String content = params[0];
			try {
				return the86.createConversation(groupSlug, content);
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Conversation conversations) {
			// populate(conversations);
			dialog.dismiss();
		}

	}
}
