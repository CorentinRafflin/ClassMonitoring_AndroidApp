package com.easynote.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.easynote.objects.Group;
import com.easynote.objects.Meeting;
import com.easynote.objects.MeetingType;
import com.easynote.objects.Observation;
import com.easynote.objects.Project;
import com.easynote.tools.Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by coren_000 on 16/03/2018.
 */

public class ModificationMeetingActivity extends Activity implements OnClickListener  {
    Button selectProjectButton, selectMeetingTypeButton, selectGroupButton, createMeetingButton, backToPreparationButton;
    TextView titleTextView, infoProjectTextView, infoMeetingTypeTextView, infoGroupTextView;
    EditText meetingNameEditText;
    DatePicker datePicker;
    TimePicker timePickerBeginning, timePickerEnding;
    Resources system;
    Database database;

    //for the SingleChoice Project
    int selectedProjectId = -1;
    int oldSelectedProjectId;
    Project oldProject;
    Project selectedProject;
    ArrayList<Project> projectList;
    String[] projectArray;

    //for the SingleChoice MeetingType
    int selectedMeetingTypeId = -1;
    int oldSelectedMeetingTypeId;
    MeetingType oldMeetingType;
    MeetingType selectedMeetingType;
    ArrayList<MeetingType> meetingTypeList;
    String[] meetingTypeArray;

    //for the MultiChoice Groups
    ArrayList<Group> selectedGroups = new ArrayList<>();  // Where we track the selected items
    ArrayList<Group> groupsList;
    boolean[] checkedGroups;
    String[] groupsArray;
    ArrayList<Integer> selectedGroupsId = new ArrayList<>();;
    String recapGroups = "";

    Meeting meeting;
    int meetingId;
    Observation observation;
    int observationId;
    MeetingType meetingType;
    int[] eventTypeIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modification_meeting_activity);

        database = new Database(this);
        database.open();


        //on récupère l'observation lancée
        observation = getIntent().getExtras().getParcelable("observation");
        observationId=observation.getId();
        //on récupère les informations communes à toutes les observations
        meeting = database.getMeetingByObservation(observationId);
        meetingId = meeting.getId();
        meetingType = database.getMeetingTypeByMeeting(meetingId);

        titleTextView = (TextView) findViewById(R.id.titleModificationMeeting);
        titleTextView.setText(String.format("Modification de la séance : %s", meeting.getName()));

        selectProjectButton = (Button) findViewById(R.id.selectProjectButton);
        selectProjectButton.setOnClickListener(this);

        selectMeetingTypeButton = (Button) findViewById(R.id.selectMeetingTypeButton);
        selectMeetingTypeButton.setOnClickListener(this);

        selectGroupButton = (Button) findViewById(R.id.selectGroupButton);
        selectGroupButton.setOnClickListener(this);

        createMeetingButton = (Button) findViewById(R.id.createMeetingButton);
        createMeetingButton.setOnClickListener(this);

        backToPreparationButton = (Button) findViewById(R.id.backToPreparationButton);
        backToPreparationButton.setOnClickListener(this);


        infoProjectTextView = (TextView) findViewById(R.id.infoProjectTextView);
        infoMeetingTypeTextView = (TextView) findViewById(R.id.infoMeetingTypeTextView);
        infoGroupTextView = (TextView) findViewById(R.id.infoGroupTextView);

        meetingNameEditText = (EditText) findViewById(R.id.meetingNameEditText);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePickerBeginning= (TimePicker) findViewById(R.id. timePickerBeginning);
        timePickerEnding = (TimePicker) findViewById(R.id.timePickerEnding);
        timePickerBeginning.setIs24HourView(true);
        timePickerEnding.setIs24HourView(true);

        //for the choices dialogs (except Groups because it depends on the project)
        projectList = database.getProjects();
        projectArray = database.getProjectsArray();
        meetingTypeList = database.getMeetingTypes();
        meetingTypeArray = database.getMeetingTypeArray();


        selectedProject = database.getProjectFromMeetingId(meetingId);
        infoProjectTextView.setText(String.format(getString(R.string.projectInfoTitle), selectedProject.getName()));
        selectedMeetingType=meetingType;
        infoMeetingTypeTextView.setText(String.format(getString(R.string.meetingTypeChoice), selectedMeetingType.getName()));
        groupsList = database.getGroupsFromProject(selectedProjectId);
        groupsArray = database.getGroupsFromProjectArray(selectedProjectId);
        //update the view of the selected groups
        infoGroupTextView.setText(recapGroups);

        update();


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Database closing
        database.close();
    }


    /**
     * Update the groups depending on the selectedProject
     */
    public void update() {
        if (selectedProjectId != -1) {
            //update the lists of the groups
            groupsList = database.getGroupsFromProject(selectedProjectId);
            groupsArray = database.getGroupsFromProjectArray(selectedProjectId);
            //update the view of the selected groups
            infoGroupTextView.setText(recapGroups);
        }
    }


    public void onClick(View button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        switch (button.getId()) {

            case R.id.selectProjectButton:
                try {
                    //stock the old project in case of a cancel
                    oldProject = selectedProject;
                    oldSelectedProjectId = selectedProjectId;

                    //may be unnecessary
                    selectedProject = null;
                    selectedProjectId = -1;

                    builder.setTitle(getResources().getString(R.string.selectProjectButton));
                    // the first element is check by default
                    builder.setSingleChoiceItems(projectArray, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedProject = projectList.get(which);
                                }
                            });

                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //in case the user doesn't check a item, we take the first one by default
                            //in fact we take the same as the "checkedItem" overhead
                            if (selectedProject == null) {
                                selectedProject = projectList.get(0);
                            }

                            infoProjectTextView.setText(String.format(getString(R.string.projectInfoTitle), selectedProject.getName()));
                            selectedProjectId = selectedProject.getId();

                            //we rebuild the groups and reset the view
                            recapGroups = "";
                            update();

                            //we initialise the checked groups by default
                            checkedGroups = new boolean[groupsArray.length];
                            for (int i = 0; i < checkedGroups.length; i++) {
                                checkedGroups[i] = false;
                            }

                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //we take the old data
                            selectedProject = oldProject;
                            selectedProjectId = oldSelectedProjectId;
                            update();
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


            case R.id.selectMeetingTypeButton:
                try {
                    oldMeetingType = selectedMeetingType;
                    oldSelectedMeetingTypeId = selectedMeetingTypeId;

                    selectedMeetingType = null;
                    selectedMeetingTypeId = -1;

                    builder.setTitle(getResources().getString(R.string.selectMeetingTypeButton));
                    builder.setSingleChoiceItems(meetingTypeArray, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedMeetingType = meetingTypeList.get(which);
                                }
                            });

                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (selectedMeetingType == null) {
                                selectedMeetingType = meetingTypeList.get(0);
                            }

                            infoMeetingTypeTextView.setText(String.format(getString(R.string.meetingTypeChoice), selectedMeetingType.getName()));
                            selectedMeetingTypeId = selectedMeetingType.getId();

                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            selectedMeetingType = oldMeetingType;
                            selectedMeetingTypeId = oldSelectedMeetingTypeId;

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


            case R.id.selectGroupButton:
                if (selectedProjectId == -1) {
                    Toast.makeText( getApplicationContext(),getString(R.string.selectGroupError),Toast.LENGTH_LONG).show();
                }
                try {
                    //we clean the selectedGroups for no repetition
                    selectedGroups.clear();

                    builder.setTitle(getString(R.string.pickGroups));

                    // we save the checkedGroups
                    builder.setMultiChoiceItems(groupsArray, checkedGroups,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    checkedGroups[which] = isChecked;
                                }
                            });

                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //for each group checked, we add it to the list
                            for (int i = 0; i < checkedGroups.length; i++) {
                                if (checkedGroups[i]) {
                                    selectedGroups.add(groupsList.get(i));
                                    selectedGroupsId.add(groupsList.get(i).getId());
                                }
                            }
                            //we reinitialise the view and build the String view of the groupe
                            recapGroups = "";
                            for (Group group : selectedGroups) {
                                recapGroups = recapGroups + "Groupe " + group.getGroupNumber() + "\n";
                            }
                            //we update this view
                            update();
                        }
                    });

                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

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


            case R.id.createMeetingButton:
                //we build the two dates depending on the pickers


                //we take the name
                String meetingName = meetingNameEditText.getText().toString();

                //we test if all the data are here
                if (meetingName.equals("") || selectedGroups.isEmpty()
                        || selectedMeetingTypeId==-1 || selectedProjectId==-1 ) {
                    Toast.makeText( getApplicationContext(),getString(R.string.addMeetingError),Toast.LENGTH_LONG).show();
                }
                //if it's good we create the meeting and add it to the database
                else {
                    //Meeting meeting = new Meeting(selectedProjectId, selectedMeetingTypeId, meetingName, date, endingDate);
                    long meetingId = database.createMeeting(meeting);
                    meeting.setId((int) meetingId);

                    //supprimer parce qu'on crée déjà les observations après
                    //database.addGroupsToMeeting((int) meetingId, groupsList);

                    //we also create each observation
                    for (Group group : selectedGroups) {
                        Observation observation = new Observation(group.getId(), (int) meetingId);
                        long observationId = database.createObservation(observation);
                        observation.setId( (int) observationId); //est-ce utile?
                    }

                    //Clean
                    selectedProject=null;
                    selectedProjectId=-1;

                    selectedMeetingType=null;
                    selectedMeetingTypeId=-1;


                    recapGroups = "";
                    infoProjectTextView.setText("");
                    infoMeetingTypeTextView.setText("");
                    infoGroupTextView.setText(recapGroups);
                    meetingNameEditText.setText("");

                    Toast.makeText( getApplicationContext(),getString(R.string.meetingCreated),Toast.LENGTH_LONG).show();

                }
                break;

            case R.id.backToPreparationButton :
                finish();
                break;

        }
    }

/*    private void set_timepicker_text_colour(){
        system = Resources.getSystem();
        int hour_numberpicker_id = system.getIdentifier("hour", "id", "android");
        int minute_numberpicker_id = system.getIdentifier("minute", "id", "android");
        int ampm_numberpicker_id = system.getIdentifier("amPm", "id", "android");

        NumberPicker hour_numberpicker = (NumberPicker) time_picker.findViewById(hour_numberpicker_id);
        NumberPicker minute_numberpicker = (NumberPicker) time_picker.findViewById(minute_numberpicker_id);
        NumberPicker ampm_numberpicker = (NumberPicker) time_picker.findViewById(ampm_numberpicker_id);

        set_numberpicker_text_colour(hour_numberpicker);
        set_numberpicker_text_colour(minute_numberpicker);
        set_numberpicker_text_colour(ampm_numberpicker);
    }

    private void set_numberpicker_text_colour(NumberPicker number_picker){
        final int count = number_picker.getChildCount();
        final int color = getResources().getColor(R.color.text);

        for(int i = 0; i < count; i++){
            View child = number_picker.getChildAt(i);

            try{
                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
                wheelpaint_field.setAccessible(true);

                ((Paint)wheelpaint_field.get(number_picker)).setColor(color);
                ((EditText)child).setTextColor(color);
                number_picker.invalidate();
            }
            catch(Exception e) {
                Log.d("exception ", e.toString());
            }
        }
    }*/
}
