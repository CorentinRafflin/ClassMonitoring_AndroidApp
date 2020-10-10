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

public class DebriefingManagementActivity extends Activity implements OnClickListener {
    Button searchMeetingButton, debriefingMeetingButton, backToMainViewButton, lastTouchedButton;
    EditText meetingNameTextView;

    LinearLayout meetingsListView;

    int idLastButton=-1;

    private AutoCompleteTextView nameMeetingSearchedACTextView = null;

    Meeting selectedMeeting;
    Meeting meetingSearched;


    Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debriefing_management_activity);

        database = new Database(this);
        database.open();

        //Remove the focus on the EditText at the beginning of the activity
        findViewById(R.id.parentEditTextMeetingSearched).requestFocus();

        searchMeetingButton = (Button) findViewById(R.id.searchMeetingButton);
        searchMeetingButton.setOnClickListener(this);

        debriefingMeetingButton = (Button) findViewById(R.id.debriefingMeetingButton);
        debriefingMeetingButton.setOnClickListener(this);

        backToMainViewButton = (Button) findViewById(R.id.backToMainViewButton);
        backToMainViewButton.setOnClickListener(this);

        meetingNameTextView = (EditText) findViewById(R.id. meetingNameEditText);

        nameMeetingSearchedACTextView = (AutoCompleteTextView) findViewById(R.id.nameMeetingSearchedACTextView);
        nameMeetingSearchedACTextView.setThreshold(0);

        meetingsListView = (LinearLayout) findViewById(R.id.linearMeeting);

        getAllMeetings();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    /**
     * Search the list of all the meetings for the autocompletion
     */
    public void getAllMeetings() {
        String[] meetingsArray = database.getAllMeetingNameArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, meetingsArray);
        nameMeetingSearchedACTextView.setAdapter(adapter);
    }

    public void setColorButton(Button button, int color) {
        button.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Update the meeting button
     * @param meeting meeting we want to show
     */
    public void updateMeeting(Meeting meeting) {

        try {
            meetingsListView.removeAllViews();

            Button btn = new Button(this);
            btn.setId(meeting.getId());
            btn.setText(meeting.getName());
            btn.setAllCaps(false);

            btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg1) {
                    if (idLastButton == -1) {
                        idLastButton = arg1.getId();
                        arg1.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                        lastTouchedButton = (Button) arg1;
                    }

                    if (idLastButton != arg1.getId() && idLastButton != -1) {
                        idLastButton = arg1.getId();
                        arg1.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                        setColorButton(lastTouchedButton, Color.GRAY);
                        lastTouchedButton = (Button) arg1;
                    }
                    Meeting meeting = database.getMeetingById(idLastButton);
                }
            });

            meetingsListView.addView(btn);


        } catch (Exception e) {
            Dialog d = new Dialog(this);
            d.setTitle(e.toString());
            d.show();
        }
    }




    /**
     * Action on the button
     * @param arg0 the touched button
     */
    public void onClick(View arg0) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        switch (arg0.getId()) {

            case R.id.searchMeetingButton:
                String nameMeetingSearched= nameMeetingSearchedACTextView.getText().toString();
                try {
                    meetingSearched = database.getMeetingByName(nameMeetingSearched);
                    if (meetingSearched != null) {
                        updateMeeting(meetingSearched);
                        nameMeetingSearchedACTextView.setText("");
                    } else {
                        Dialog d = new Dialog(this);
                        d.setTitle(getString(R.string.meetingDoesntExistMessage));
                        d.show();
                    }
                } catch (Exception e) {
                    Dialog d = new Dialog(this);
                    d.setTitle(e.getMessage());
                    d.show();
                }
                break;


            case R.id.debriefingMeetingButton:
                //gérer le cas ou c'est pas rempli... et les dates
                selectedMeeting=database.getMeetingById(idLastButton);
                ArrayList<Group> groupList = database.getGroupsFromMeetingId(selectedMeeting.getId());
                Group randomGroup = groupList.get(0); //traiter le cas sans groupe
                Observation observation = database.getObservationByMeetingAndGroupId(selectedMeeting.getId(), randomGroup.getId());
                Intent intent = new Intent(DebriefingManagementActivity.this, DebriefingObservation.class);
                intent.putExtra("observation", observation);
                startActivity(intent);

                break;



            case R.id.backToMainViewButton:
                Intent myIntent = new Intent(DebriefingManagementActivity.this, HomeActivity.class);
                startActivity(myIntent);
                break;
        }
    }
}