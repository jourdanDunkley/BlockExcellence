package com.excellence.user.blockx.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.excellence.user.blockx.R;
import com.excellence.user.blockx.models.Event;

import java.util.List;

/**
 * Created by User on 5/3/2017.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.PostHolder> {

    private List<Event> events;

    public EventListAdapter(List<Event> events){
        this.events = events;
    }


    @Override
    public EventListAdapter.PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(EventListAdapter.PostHolder holder, int position) {
        Event event = events.get(holder.getAdapterPosition());

        holder.eventTime.setText(event.getEventTime());
        holder.eventName.setText(event.getEventName());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    class PostHolder extends RecyclerView.ViewHolder{

        TextView eventName, eventTime;

        public PostHolder(View itemView) {
            super(itemView);
            eventName = (TextView)itemView.findViewById(R.id.eventName);
            eventTime = (TextView)itemView.findViewById(R.id.eventTime);
        }
    }

    public void update(List<Event> events){
        this.events = events;
        notifyDataSetChanged();
    }
}
