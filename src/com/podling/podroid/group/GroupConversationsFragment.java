package com.podling.podroid.group;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.Conversation;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.podling.podroid.CreateConversationDialogFragment;
import com.podling.podroid.R;
import com.podling.podroid.adapter.ConversationAdapter;
import com.podling.podroid.posts.PostsActivity;

public class GroupConversationsFragment extends GroupFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		new RetrieveConversationsTask(getActivity()).execute();
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Conversation conversation = (Conversation) getListAdapter().getItem(
				position);
		startActivity(PostsActivity.newInstance(getActivity(), groupSlug,
				conversation.getId()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.conversations, container, false);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.group_conversations_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_create_conversation) {
			CreateConversationDialogFragment dialog = CreateConversationDialogFragment
					.newInstance(groupSlug);
			FragmentManager fragmentManager = getFragmentManager();
			dialog.show(fragmentManager, "create_conversation");
		}
		return true;
	}

	private void populate(List<Conversation> conversations) {
		setListAdapter(new ConversationAdapter(getActivity(), conversations));
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
