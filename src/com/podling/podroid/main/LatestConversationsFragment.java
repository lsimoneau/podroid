package com.podling.podroid.main;

import java.util.ArrayList;
import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Conversation;

import android.app.ListFragment;
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

import com.podling.podroid.R;
import com.podling.podroid.adapter.ConversationAdapter;
import com.podling.podroid.posts.PostsActivity;
import com.podling.podroid.util.The86Util;

public class LatestConversationsFragment extends ListFragment {
	private The86 the86;
	private LinearLayout progress;
	private boolean fetched = false;
	private boolean allowRefresh = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setListAdapter(new ConversationAdapter(getActivity(),
				new ArrayList<Conversation>()));
		the86 = The86Util.get(getActivity());
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
		inflater.inflate(R.menu.conversations_menu, menu);
		MenuItem refresh = menu.findItem(R.id.refresh_conversations_menu_item);
		refresh.setEnabled(allowRefresh);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh_conversations_menu_item) {
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Conversation conversation = (Conversation) getListAdapter().getItem(
				position);
		startActivity(PostsActivity.newInstance(getActivity(), conversation));
	}

	private void fetchConversations() {
		setRefreshable(false);
		((ConversationAdapter) getListAdapter()).clear();
		new RetrieveLatestConversationsTask().execute();
	}

	private void populate(List<Conversation> conversations) {
		((ConversationAdapter) getListAdapter()).addAll(conversations);
		setRefreshable(true);
	}

	private void setRefreshable(boolean allowRefresh) {
		this.allowRefresh = allowRefresh;
		getActivity().invalidateOptionsMenu();
	}

	class RetrieveLatestConversationsTask extends
			AsyncTask<Void, Void, List<Conversation>> {

		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}

		protected List<Conversation> doInBackground(Void... params) {
			try {
				return the86.getUserConversations();
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(List<Conversation> conversations) {
			populate(conversations);
			progress.setVisibility(View.GONE);
			fetched = true;
		}
	}
}
