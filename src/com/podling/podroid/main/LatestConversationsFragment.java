package com.podling.podroid.main;

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

import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.adapter.ConversationAdapter;
import com.podling.podroid.posts.PostsActivity;

public class LatestConversationsFragment extends ListFragment {
	private The86 the86;
	protected LinearLayout progress;
	private boolean fetched;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fetched = false;
		setHasOptionsMenu(true);
		the86 = ((PodroidApplication) getActivity().getApplicationContext())
				.getThe86();
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
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// check for fetched to stop double refresh
		if (fetched && item.getItemId() == R.id.refresh_conversations_menu_item) {
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
		getListView().setVisibility(View.GONE);
		new RetrieveLatestConversationsTask().execute();
	}

	private void populate(List<Conversation> conversations) {
		setListAdapter(new ConversationAdapter(getActivity(), conversations));
		getListView().setVisibility(View.VISIBLE);
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
