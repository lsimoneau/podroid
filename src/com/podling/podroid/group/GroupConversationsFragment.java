package com.podling.podroid.group;

import java.util.ArrayList;
import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.Conversation;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.podling.podroid.CreateConversationDialogFragment;
import com.podling.podroid.R;
import com.podling.podroid.adapter.ConversationAdapter;
import com.podling.podroid.posts.PostsActivity;

public class GroupConversationsFragment extends GroupFragment {
	private LinearLayout progress;
	private boolean fetched = false;
	private boolean allowRefresh = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setListAdapter(new ConversationAdapter(getActivity(),
				new ArrayList<Conversation>()));
	}

	@Override
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
		progress = (LinearLayout) v
				.findViewById(R.id.conversation_loading_progress);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.group_conversations_menu, menu);
		MenuItem refresh = menu.findItem(R.id.refresh_conversations_menu_item);
		refresh.setEnabled(allowRefresh);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_create_conversation) {
			CreateConversationDialogFragment dialog = CreateConversationDialogFragment
					.newInstance(groupSlug);
			FragmentManager fragmentManager = getFragmentManager();
			dialog.show(fragmentManager, "create_conversation");
		} else if (item.getItemId() == R.id.refresh_conversations_menu_item) {
			fetchConversations();
		}
		return true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!fetched) {
			fetchConversations();
		}
	}

	private void fetchConversations() {
		setRefreshable(false);
		((ConversationAdapter) getListAdapter()).clear();
		new RetrieveConversationsTask().execute();
	}

	private void populate(List<Conversation> conversations) {
		((ConversationAdapter) getListAdapter()).addAll(conversations);
		setRefreshable(true);
	}

	private void setRefreshable(boolean allowRefresh) {
		this.allowRefresh = allowRefresh;
		getActivity().invalidateOptionsMenu();
	}

	class RetrieveConversationsTask extends
			AsyncTask<Void, Void, List<Conversation>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<Conversation> doInBackground(Void... params) {
			try {
				return the86.getGroupConversations(groupSlug);
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Conversation> conversations) {
			populate(conversations);
			fetched = true;
			progress.setVisibility(View.GONE);
		}

	}
}
