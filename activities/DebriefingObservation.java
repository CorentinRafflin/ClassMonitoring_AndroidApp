package com.easynote.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easynote.objects.Debriefing;
import com.easynote.objects.Event;
import com.easynote.objects.EventType;
import com.easynote.objects.Group;
import com.easynote.objects.Meeting;
import com.easynote.objects.MeetingType;
import com.easynote.objects.Observation;
import com.easynote.objects.Student;
import com.easynote.tools.Database;
import com.easynote.tools.Writer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by coren_000 on 16/04/2018.
 */

public class DebriefingObservation extends Activity implements View.OnClickListener {

    // Buttons
    Button endDebriefingButton, changeEventsTypesButton, statisticsButton, exportButton,
            backToDebriefingMeetingButton;

    TextView historicTextView;

    Database database;

    Meeting meeting;
    MeetingType meetingType;
    Observation observation;
    int observationId;
    Group group;
    ArrayList<Student> students;

    ArrayList<Event> events;
    ArrayList<EventType> displayedEventType;

    ArrayList<Student> displayedStudents;
    ArrayList<Integer> eventsTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debriefing_observation);

        database = new Database(this);
        database.open();

        historicTextView = (TextView) findViewById(R.id.historicTextView);

        //on récupère l'observation
        observation = getIntent().getExtras().getParcelable("observation");
        observationId = observation.getId();
        meeting= database.getMeetingByObservation(observationId);
        group = database.getGroupFromObservation(observationId);
        students = database.getStudentsFromGroup(group.getId());
        displayedStudents=database.getStudentsFromGroup(group.getId());
        events = database.getEventsFromObservation(observationId);
        meetingType = database.getMeetingTypeByMeeting(meeting.getId());
        displayedEventType=database.getEventTypeFromMeetingTypeList(meetingType.getId());

        endDebriefingButton = (Button) findViewById(R.id.endDebriefingButton);
        endDebriefingButton.setOnClickListener(this);

        changeEventsTypesButton = (Button) findViewById(R.id.changeEventsTypesButton);
        changeEventsTypesButton.setOnClickListener(this);

        statisticsButton = (Button) findViewById(R.id.statisticsButton);
        statisticsButton.setOnClickListener(this);

        //exportButton = (Button) findViewById(R.id.exportButton);
        //exportButton.setOnClickListener(this);

        backToDebriefingMeetingButton = (Button) findViewById(R.id.backToDebriefingMeetingButton);
        backToDebriefingMeetingButton.setOnClickListener(this);

        observation.setEventList(database.getEventsFromObservation(observationId));
        String historic = observation.getHistoric(this);
        historicTextView.setText(historic);
        historicTextView.setTextSize(20);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    /**
     * Action on the button
     *
     * @param arg0 the touched button
     */
    public void onClick(View arg0) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        Intent intent;
        switch (arg0.getId()) {

            case R.id.endDebriefingButton:
                // Dialog popup building
                builder.setTitle(getString(R.string.confirmation));
                builder.setMessage(getString(R.string.endDebriefingConfirmationMessage));
                builder.setNegativeButton(getString(R.string.no), null);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                dialog = builder.create();
                dialog.show();
                break;

            case R.id.changeEventsTypesButton:
                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.events_students_filter_layout, null);
                builder.setView(dialoglayout);

                final ArrayList<String> eventsNames = new ArrayList<String>();
                final ArrayList<String> checkedEventsNames = new ArrayList<String>();

                final ArrayList<String> studentsNames = new ArrayList<String>();
                final ArrayList<String> studentsLogin = new ArrayList<String>();
                final ArrayList<String> checkedStudentsLogins = new ArrayList<String>();

                for (Event event : database.getEventsFromObservation(observationId)) {
                    if (!eventsNames.contains(event.getName(this))) {
                        eventsNames.add(event.getName(this));
                    }
                }

                for (Student s : database.getStudentsFromGroup(group.getId())) {
                    if (!studentsNames.contains(s.getLastName() + " " + s.getFirstName())) {
                        studentsNames.add(s.getLastName() + " " + s.getFirstName());
                        studentsLogin.add(s.getLogin());
                    }
                }

                for (EventType eventType : displayedEventType) {
                    checkedEventsNames.add(eventType.getName());
                }

                for (Student s : displayedStudents) {
                    checkedStudentsLogins.add(s.getLogin());
                }

                final ListView eventsFilterListView = (ListView) dialoglayout.findViewById(R.id.eventsChoiceListView);
                final ListView studentsFilterListView = (ListView) dialoglayout.findViewById(R.id.studentsChoiceListView);
                Button validationButton = (Button) dialoglayout.findViewById(R.id.filterChoiceValidationButton);

                validationButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Events
                        checkedEventsNames.clear();
                        displayedEventType = new ArrayList<>();
                        for (int k = 0; k < eventsNames.size(); k++) {
                            if (eventsFilterListView.getCheckedItemPositions().get(k)) {
                                checkedEventsNames.add(eventsNames.get(k)); //UTILE ????????????
                                EventType eventTypeAssociate= database.getEventTypeByName(eventsNames.get(k));
                                if (!displayedEventType.contains(eventTypeAssociate)) {
                                    displayedEventType.add(eventTypeAssociate);
                                }
                            }
                        }


                        

                        //Students
                        checkedStudentsLogins.clear();
                        displayedStudents = new ArrayList<>();

                        for (int k = 0; k < studentsLogin.size(); k++) {
                            if (studentsFilterListView.getCheckedItemPositions().get(k)) {
                                checkedStudentsLogins.add(studentsLogin.get(k));
                                Student studentAssociate = database.getStudentByLogin(studentsLogin.get(k));
                                if (!displayedStudents.contains(studentAssociate)) {
                                    displayedStudents.add(studentAssociate);
                                }
                            }
                        }


                        

                        observation.setEventList(database.getEventsFromObservationStudentEventTypeLists(observationId, 
                                displayedStudents, displayedEventType));
                        String historic = observation.getHistoric(DebriefingObservation.this);
                        historicTextView.setText(historic);

                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.eventsTypesSaved), Toast.LENGTH_SHORT).show();

                    }
                });

                eventsFilterListView.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_multiple_choice,
                        eventsNames));


                for (int k = 0; k < eventsNames.size(); k++) {
                    if (checkedEventsNames.contains(eventsNames.get(k))) {
                        eventsFilterListView.setItemChecked(k, true);
                    }
                }

                studentsFilterListView.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_multiple_choice,
                        studentsNames));

                for (int k = 0; k < studentsLogin.size(); k++) {
                    if (checkedStudentsLogins.contains(studentsLogin.get(k))) {
                        studentsFilterListView.setItemChecked(k, true);
                    }
                }

                builder.create().show();
                break;

            case R.id.statisticsButton:
                intent = new Intent(DebriefingObservation.this, StatisticsActivity.class);
                intent.putExtra("observation", observation);
                startActivity(intent);
                break;

            /*case R.id.exportButton:
                export(null);

                Toast.makeText(
                        getApplicationContext(),
                        "Données exportées !", Toast.LENGTH_SHORT).show();
                break;*/

            case R.id.backToDebriefingMeetingButton:
                intent = new Intent(DebriefingObservation.this, DebriefingMeetingActivity.class);
                intent.putExtra("observation", observation);
                startActivity(intent);
                break;
        }
    }

    /**
     * exports the data as a csv
     * whose format is Event name ; Date ; Student1 ; Student2 ; ... ; Studentn
     *
     * @param comment
     *             the comment written by the user
     */
    private void export(String comment) {

        Writer writer = new Writer();

        String message = new String();

        String meetingName = meeting.getName();
        String projectName = database.getProjectFromMeetingId(meeting.getId()).getName();
        int groupNumber = database.getGroup(group.getId()).getGroupNumber();

        Calendar calendar = new GregorianCalendar();

        calendar.setTime(Debriefing.getMeeting().getDate());

        String day = ((Integer) calendar.get(Calendar.DAY_OF_MONTH)).toString();
        String month = ((Integer) calendar.get(Calendar.MONTH)).toString();
        String year = ((Integer) calendar.get(Calendar.YEAR)).toString();

        String meetingDate = year + "-" + month + "-" + day;

        message+="ProjectName ; GroupNumber ; Meeting\n"
                +projectName+";"+groupNumber+";"+meetingName+"\n\n"
                +"Event name ; Date ; Students\n";

        for(Event e :Debriefing.getEvents()){
            message += e.toExport(this) + "\n";
        }


        writer.write(message+comment,"debriefing_"+projectName+"_g"+
                groupNumber+"_"+meetingName+"_"+meetingDate+".csv");
    }
    
}
