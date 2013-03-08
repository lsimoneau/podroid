package com.podling.podroid.main;

import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Conversation;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.podling.podroid.PodroidApplication;
import com.podling.podroid.PostsActivity;
import com.podling.podroid.R;
import com.podling.podroid.adapter.ConversationAdapter;

public class LatestConversationsFragment extends ListFragment {
	private The86 the86;
	protected LinearLayout progress;
	private boolean fetched;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fetched = false;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!fetched) {
			new RetrieveLatestConversationsTask().execute();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Conversation conversation = (Conversation) getListAdapter().getItem(
				position);
		startActivity(PostsActivity.newInstance(getActivity(), conversation));
	}

	private void populate(List<Conversation> conversations) {
		setListAdapter(new ConversationAdapter(getActivity(), conversations));
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
