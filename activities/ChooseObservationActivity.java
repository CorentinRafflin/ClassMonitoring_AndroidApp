package com.easynote.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
 * Created by coren_000 on 19/03/2018.
 */

public class ChooseObservationActivity extends Activity implements OnClickListener {
    Button searchMeetingButton, selectOneGroupButton, editMeetingButton, observeGroupButton, backToMainViewButton,
            lastTouchedButton;
    EditText meetingNameTextView;
    TextView infoGroupTextView;
    LinearLayout meetingsListView;

    int idLastButton = -1;

    private AutoCompleteTextView nameMeetingSearchedACTextView = null;

    //for the SingleChoice Group
    int selectedGroupId = -1;
    int oldSelectedGroupId;
    Group oldGroup;
    Group selectedGroup;
    ArrayList<Group> groupList;
    String[] groupArray;

    Meeting selectedMeeting;
    Meeting meetingSearched;

    Observation observation;

    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_observation_activity);

        database = new Database(this);
        database.open();

        //Remove the focus on the EditText at the beginning of the activity
        findViewById(R.id.parentEditTextMeetingSearched).requestFocus();

        searchMeetingButton = (Button) findViewById(R.id.searchMeetingButton);
        searchMeetingButton.setOnClickListener(this);

        selectOneGroupButton = (Button) findViewById(R.id.selectOneGroupButton);
        selectOneGroupButton.setOnClickListener(this);

        editMeetingButton = (Button) findViewById(R.id.editMeetingButton);
        editMeetingButton.setOnClickListener(this);

        observeGroupButton = (Button) findViewById(R.id.observeGroupButton);
        observeGroupButton.setOnClickListener(this);

        backToMainViewButton = (Button) findViewById(R.id.backToMainViewButton);
        backToMainViewButton.setOnClickListener(this);

        meetingNameTextView = (EditText) findViewById(R.id. meetingNameEditText);

        infoGroupTextView = (TextView) findViewById(R.id.infoGroupTextView);

        nameMeetingSearchedACTextView = (AutoCompleteTextView) findViewById(R.id.nameMeetingSearchedACTextView);
        nameMeetingSearchedACTextView.setThreshold(0);

        meetingsListView = (LinearLayout) findViewById(R.id.linearMeeting);

        getAllMeetings();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Database closing
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

            case R.id.editMeetingButton:
                if (idLastButton != -1) {
                    // Dialog popup building
                    selectedMeeting=database.getMeetingById(idLastButton);
                    ArrayList<Group> groupList = database.getGroupsFromMeetingId(selectedMeeting.getId());
                    Group randomGroup = groupList.get(0); //traiter le cas sans groupe
                    Observation observation = database.getObservationByMeetingAndGroupId(selectedMeeting.getId(), randomGroup.getId());
                    Intent intent = new Intent(ChooseObservationActivity.this, ModificationMeetingActivity.class);
                    intent.putExtra("observation", observation);

                    startActivity(intent);

                } else {
                    Dialog d = new Dialog(this);
                    d.setTitle(getString(R.string.selectMeetingMessage));
                    d.show();
                }
                break;

            case R.id.selectOneGroupButton:
                if (idLastButton != -1) {
                    selectedMeeting=database.getMeetingById(idLastButton);
                    //for the choice dialog
                    groupList = database.getGroupsFromMeetingId(idLastButton);
                    groupArray = database.getGroupsArrayFromMeetingId(idLastButton);

                    try {
                        //stock the old group in case of a cancel
                        oldGroup = selectedGroup;
                        oldSelectedGroupId = selectedGroupId;

                        //may be unnecessary
                        selectedGroup = null;
                        selectedGroupId = -1;

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

                                infoGroupTextView.setText(String.format(getString(R.string.OneGroupInfoTitle), selectedGroup.getName()));
                                selectedGroupId = selectedGroup.getId();

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
                } else {
                    Dialog d = new Dialog(this);
                    d.setTitle(getString(R.string.selectMeetingMessage));
                    d.show();
                }
                break;

            case R.id.observeGroupButton:
                if (selectedMeeting==null) {
                    Toast.makeText( getApplicationContext(),getString(R.string.selectMeetingMessage),Toast.LENGTH_LONG).show();
                    break;
                }
                if (selectedGroup==null) {
                    Toast.makeText( getApplicationContext(),getString(R.string.selectGroupMessage),Toast.LENGTH_LONG).show();
                    break;
                }

                Date currentTime = Calendar.getInstance().getTime();
                Date beginning = selectedMeeting.getDate();
                Date ending = selectedMeeting.getEndingDate();

                observation = database.getObservationByMeetingAndGroupId(selectedMeeting.getId(), selectedGroupId);

                if (currentTime.before(beginning)) {
                    builder.setTitle(getResources().getString(R.string.meetingHasNotBegun));
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ChooseObservationActivity.this, ObservationActivity.class);
                            intent.putExtra("observation", observation);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    dialog = builder.create();
                    dialog.show();
                }

                else if (currentTime.after(ending)) {
                    builder.setTitle(getResources().getString(R.string.meetingFinished));
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ChooseObservationActivity.this, DebriefingMeetingActivity.class);
                            intent.putExtra("observation", observation);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    dialog = builder.create();
                    dialog.show();
                }
                else {
                    Intent intent = new Intent(ChooseObservationActivity.this, ObservationActivity.class);
                    intent.putExtra("observation", observation);
                    startActivity(intent);
                }
                break;



            case R.id.backToMainViewButton:
                Intent myIntent = new Intent(ChooseObservationActivity.this, HomeActivity.class);
                ChooseObservationActivity.this.startActivity(myIntent);
                break;
        }
    }
}
