package com.podling.podroid;

import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Conversation;
import org.the86.model.Post;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.podling.podroid.adapter.ConversationAdapter;

public class ConversationsActivity extends ListActivity {
	private String groupSlug;
	private The86 the86;

	public static Intent newInstance(Context context, String groupSlug) {
		Intent intent = new Intent(context, ConversationsActivity.class);
		intent.putExtra("groupSlug", groupSlug);
		return intent;
	}

	private void populate(List<Conversation> conversations) {
		String[] cols = { "_id", "poster", "post", "avatar_url", "conversationId" };
		MatrixCursor cursor = new MatrixCursor(cols);

		for (Conversation c : conversations) {
			Post p = c.getPosts().get(0);
			cursor.addRow(new String[] { p.getId(), p.getUser().getName(),
					p.getContent(), p.getUser().getAvatarUrl(), c.getId() });

		}

		setListAdapter(new ConversationAdapter(this, cursor));
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		the86 = ((PodroidApplication) getApplicationContext()).getThe86();

		Bundle extras = getIntent().getExtras();
		groupSlug = extras.getString("groupSlug");
		new RetrieveConversationsTask(this).execute();

		setContentView(R.layout.conversation_list);
	}
	
	protected void onListItemClick(ListView l, View v, int position,
			long groupId) {
		super.onListItemClick(l, v, position, groupId);
		Cursor c = (Cursor) getListAdapter().getItem(position);
		String conversationId = c.getString(4);
		startActivity(PostsActivity.newInstance(this, groupSlug, conversationId));
	}
	
	class RetrieveConversationsTask extends
			AsyncTask<Void, Void, List<Conversation>> {
		protected ProgressDialog dialog;

		public RetrieveConversationsTask(Context context) {
			// create a progress dialog
			dialog = new ProgressDialog(context);
			dialog.setMessage("loading conversations");
			dialog.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		protected List<Conversation> doInBackground(Void... params) {
			try {
				return the86.getGroupConversations(groupSlug);
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(List<Conversation> conversations) {
			populate(conversations);
			dialog.dismiss();
		}

	}

}
