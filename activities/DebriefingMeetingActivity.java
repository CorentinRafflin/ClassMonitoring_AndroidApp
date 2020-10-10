package com.easynote.activities;

import java.util.ArrayList;

import com.easynote.objects.Observation;
import com.easynote.objects.Project;
import com.easynote.objects.Student;
import com.easynote.objects.Group;
import com.easynote.tools.Database;
import com.easynote.objects.Meeting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by coren_000 on 23/03/2018.
 */

public class DebriefingMeetingActivity extends Activity implements OnClickListener {
    Button selectDebriefingButton, backToHomeButton;

    //for the SingleChoice Group
    int selectedGroupId = -1;
    int oldSelectedGroupId;
    Group oldGroup;
    Group selectedGroup;
    ArrayList<Group> groupList;
    String[] groupArray;

    Observation observation;
    int observationId;
    Meeting meeting;
    int meetingId;

    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debriefing_meeting_activity);

        database = new Database(this);
        database.open();


        //on récupère l'observation qui n'a que pour but de récupérer le meeting
        observation = getIntent().getExtras().getParcelable("observation");
        observationId = observation.getId();
        //on récupère le meeting
        meeting = database.getMeetingByObservation(observationId);
        meetingId = meeting.getId();


        selectDebriefingButton = (Button) findViewById(R.id.selectDebriefingButton);
        selectDebriefingButton.setOnClickListener(this);

        backToHomeButton = (Button) findViewById(R.id.backToHomeButton);
        backToHomeButton.setOnClickListener(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Database closing
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
        switch (arg0.getId()) {

            case R.id.selectDebriefingButton:
                //for the choice dialog
                groupList = database.getGroupsFromMeetingId(meetingId);
                groupArray = database.getGroupsArrayFromMeetingId(meetingId);

                try {
                    //stock the old group in case of a cancel
                    oldGroup = selectedGroup;
                    oldSelectedGroupId = selectedGroupId;

                    builder.setTitle(getResources().getString(R.string.selectOneGroupButton));
                    // the first element is check by default
                    builder.setSingleChoiceItems(groupArray, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedGroup = groupList.get(which);
                                }
                            });

                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //in case the user doesn't check a item, we take the first one by default
                            //in fact we take the same as the "checkedItem" overhead
                            if (selectedGroup == null) {
                                selectedGroup = groupList.get(0);
                            }
                            selectedGroupId = selectedGroup.getId();

                            observation = database.getObservationByMeetingAndGroupId(meetingId, selectedGroupId);
                            Intent intent = new Intent(DebriefingMeetingActivity.this, DebriefingObservation.class);
                            intent.putExtra("observation", observation);
                            startActivity(intent);

                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //we take the old data
                            selectedGroup = oldGroup;
                            selectedGroupId = oldSelectedGroupId;
                        }
                    });

                    dialog = builder.create();
                    dialog.show();
                } catch (Exception e) {
                    Dialog d = new Dialog(this);
                    d.setTitle("Une erreur est survenue : " + e.toString());
                    d.show();
                }
                break;
            case R.id.backToHomeButton:
                Intent myIntent = new Intent(DebriefingMeetingActivity.this, HomeActivity.class);
                DebriefingMeetingActivity.this.startActivity(myIntent);
                break;
        }
    }
}