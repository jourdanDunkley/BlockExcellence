package com.excellence.user.blockx.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.excellence.user.blockx.R;
import com.excellence.user.blockx.adapters.EventListAdapter;
import com.excellence.user.blockx.models.Event;
import com.excellence.user.blockx.util.AppConfig;
import com.excellence.user.blockx.util.MultipartUtility;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 2/19/2017.
 */
public class CalendarFragment extends Fragment {

    private View view;
    private MaterialCalendarView mvc;
    private TextView add;
    private static String eventDescription;
    private static String timeString;
    private EditText descriptionText;
    private RecyclerView recyclerView;
    private EventListAdapter eventListAdapter;
    private List<Event> events;
    private static String selectedDate;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_calendar, container, false);

        initViews();

        setUpViews();

        return view;
    }

    private void initViews() {
        mvc = (MaterialCalendarView)view.findViewById(R.id.calendarView);
        add = (TextView)view.findViewById(R.id.addButton);
        add.setClickable(false);
        recyclerView = (RecyclerView)view.findViewById(R.id.events_recycler);
        events = new ArrayList<Event>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventListAdapter = new EventListAdapter(events);
        recyclerView.setAdapter(eventListAdapter);
    }

    private void setUpViews(){
        mvc.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                add.setTextColor(Color.WHITE);
                add.setClickable(true);
                selectedDate = mvc.getSelectedDate().toString();
                Toast.makeText(getActivity(),selectedDate, Toast.LENGTH_LONG).show();
                events.clear();
                loadEvents();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }
        });

    }

    private void loadEvents() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    AppConfig.URL_EVENTFETCH, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    setUpRecyclerView(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);

    }

    private void setUpRecyclerView(JSONObject response){
        String selectedDate2 = mvc.getSelectedDate().toString();
        try{
            JSONArray feedArray = response.getJSONArray("eventinfo");
            JSONObject obj = null;
            for(int i = 0; i<feedArray.length();i++){

                obj = (JSONObject)feedArray.get(i);

                if(selectedDate2.equals(obj.getString("eventDate").trim())){
                    Event event = new Event();
                    event.setEventName(obj.getString("eventName"));
                    event.setEventTime(obj.getString("eventTime"));

                    events.add(0, event);

                }
            }
            eventListAdapter.update(events);


        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_event, null))
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog f = (AlertDialog)dialogInterface;
                        descriptionText = (EditText)f.findViewById(R.id.descriptionField);
                        eventDescription = descriptionText.getText().toString().trim();
                        DialogFragment timePickFragment = new TimePickerFragment();
                        timePickFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            timeString = hourOfDay + ":" + minute;
            uploadText();
        }

        private void uploadText() {
            class UploadText extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
//                    Toast.makeText(getActivity(),s, Toast.LENGTH_LONG).show();
                }

                @Override
                protected String doInBackground(Void... params) {
                    StringBuilder sb = new StringBuilder();
                    try {
                        MultipartUtility multipartUtility = new MultipartUtility(AppConfig.URL_EVENT, "UTF-8");
                        multipartUtility.addFormField("description", eventDescription);
                        multipartUtility.addFormField("time", timeString);
                        multipartUtility.addFormField("date", selectedDate);
                        List<String> response = multipartUtility.finish();
                        for (String line : response) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return sb.toString();
                }
            }
            UploadText u = new UploadText();
            u.execute();
        }
    }
}
