package com.easynote.activities;

import com.easynote.objects.Equipment;
import com.easynote.objects.EventType;
import com.easynote.objects.MeetingType;
import com.easynote.tools.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by coren_000 on 13/03/2018.
 */

public class EventsActivity extends Activity implements OnClickListener {
    Button addEventTypeButton, addEventTypeEquipmentButton,
            deleteEventTypeButton,editEventTypeButton,
            lastTouchedButton, backToPreparationButton, searchEventTypeButton;
    EditText eventTypeNameTextView;
    LinearLayout eventTypesListView;

    String eventTypeName;

    //for the SingleChoice Equipment
    int selectedEquipmentId = -1;
    Equipment selectedEquipment;
    ArrayList<Equipment> equipmentList = new ArrayList<>();
    String[] equipmentArray;

    //TODO if you create new equipments or modify their properties 
    Equipment board = new Equipment("tableau");
    Equipment computer = new Equipment("ordinateur");
    Equipment projector = new Equipment("projecteur");
    Equipment tutor = new Equipment("tuteur");
    

    
    int idLastButton = -1;

    private AutoCompleteTextView nameEventTypeSearchedACTextView = null;

    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_type_activity);

        database = new Database(this);
        database.open();

        //we add the equipment we created
        equipmentList.add(board);
        equipmentList.add(projector);
        equipmentList.add(computer);
        equipmentList.add(tutor);
        equipmentArray = new String[] {"tableau", "projecteur", "ordinateur", "tuteur"} ;

        //Remove the focus on the EditText at the beginning of the activity
        findViewById(R.id.parentEditTextEventType).requestFocus();

        addEventTypeButton = (Button) findViewById(R.id.addEventTypeButton);
        addEventTypeButton.setOnClickListener(this);

        addEventTypeEquipmentButton = (Button) findViewById(R.id.addEventTypeEquipmentButton);
        addEventTypeEquipmentButton.setOnClickListener(this);
        
        searchEventTypeButton = (Button) findViewById(R.id.searchEventTypeButton);
        searchEventTypeButton.setOnClickListener(this);

        deleteEventTypeButton = (Button) findViewById(R.id.deleteEventTypeButton);
        deleteEventTypeButton.setOnClickListener(this);

        editEventTypeButton = (Button) findViewById(R.id.editEventTypeButton);
        editEventTypeButton.setOnClickListener(this);

        backToPreparationButton = (Button) findViewById(R.id.backToPreparationButton);
        backToPreparationButton.setOnClickListener(this);

        eventTypeNameTextView = (EditText) findViewById(R.id.eventTypeNameEditView);

        nameEventTypeSearchedACTextView = (AutoCompleteTextView) findViewById(R.id.nameEventTypeSearchedACTextView);
        nameEventTypeSearchedACTextView.setThreshold(0);

        eventTypesListView = (LinearLayout) findViewById(R.id.linearEventType);

        getAllEventTypes();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Database closing
        database.close();
    }

    /**
     * Search the list of all the eventTypes for the autocompletion
     */
    public void getAllEventTypes() {
        String[] eventTypesArray = database.getAllEventTypesArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, eventTypesArray);
        nameEventTypeSearchedACTextView.setAdapter(adapter);
    }

    /**
     * Change the color of the button
     * @param button button we want to modify
     * @param color new color
     */
    public void setColorButton(Button button, int color) {
        button.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Update the eventType button
     * @param eventType eventType we want to show
     */
    public void update(EventType eventType) {

        try {
            eventTypesListView.removeAllViews();

            Button btn = new Button(this);
            btn.setId(eventType.getId());
            btn.setText(eventType.getName());
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

            eventTypesListView.addView(btn);

        } catch (Exception e) {
            Dialog d = new Dialog(this);
            d.setTitle(e.toString());
            d.show();
        }
    }



    /**
     * Delete the selected EventType
     */
    private void deleteEventType() {
        try {
            database.deleteEventType(idLastButton);
            Button btnRemove = (Button) findViewById(idLastButton);
            eventTypesListView.removeView(btnRemove);

            String eventTypeRemove = btnRemove.getText().toString();
            Toast.makeText(
                    getApplicationContext(),
                    String.format(getString(R.string.deleteEventTypeConfirmationMessage), eventTypeRemove), Toast.LENGTH_LONG).show();

            idLastButton = -1;
            getAllEventTypes();

        } catch (Exception e) {
            Dialog d = new Dialog(this);
            d.setTitle(getString(R.string.selectEventTypeMessage));
            d.show();
        }
    }


    /**
     * Edit the names of the selected eventType
     * @param eventTypeId id of the selected eventType
     * @param eventTypeName the new name
     */
    public void editEventTypeName(int eventTypeId, String eventTypeName) {
        database.updateEventType(eventTypeId, eventTypeName);
        update(database.getEventTypeById(eventTypeId));

        Toast.makeText(
                getApplicationContext(), getString(R.string.editEventTypeConfirmationMessage),
                Toast.LENGTH_LONG).show();
    }


    /**
     * Action on the button
     * @param arg0 the touched button
     */
    public void onClick(View arg0) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog;
        //useful to close the keyboard
        final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        switch (arg0.getId()) {

            case R.id.addEventTypeButton:
                //we close the keyboard on the click on the button
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                try {
                    String eventTypeName = eventTypeNameTextView.getText().toString();
                    //we check that there is a name
                    if (!eventTypeName.equals("")) {
                        //we create the eventType
                        EventType eventType = new EventType(eventTypeName);
                        long eventTypeId = database.createEventType(eventType);
                        eventType.setId((int)eventTypeId);

                        eventTypesListView.removeAllViews();
                        update(eventType);

                        eventTypeNameTextView.setText("");
                        getAllEventTypes();
                    } else {
                        Toast.makeText( getApplicationContext(),getString(R.string.addEventTypeError),Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Dialog d = new Dialog(this);
                    d.setTitle("Une erreur est survenue : " + e.toString());
                    d.show();

                }
                break;

            case R.id.addEventTypeEquipmentButton:
                try {
                    eventTypeName = eventTypeNameTextView.getText().toString();
                    if (eventTypeName.equals("")) {
                        Toast.makeText( getApplicationContext(),getString(R.string.addEventTypeError),Toast.LENGTH_LONG).show();
                        break;
                    }
                    selectedEquipment = null;
                    selectedEquipmentId = -1;

                    builder.setTitle(getResources().getString(R.string.selectEquipmentTitle));
                    builder.setSingleChoiceItems(equipmentArray, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedEquipment = equipmentList.get(which);
                                }
                            });

                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (selectedEquipment == null) {
                                selectedEquipment = equipmentList.get(0);
                            }
                            eventTypeName = eventTypeName + " (" + selectedEquipment.getName() + ")";
                            EventType eventType = new EventType(eventTypeName, selectedEquipment.getName());
                            long eventTypeId = database.createEventType(eventType);
                            eventType.setId((int)eventTypeId);

                            eventTypesListView.removeAllViews();
                            update(eventType);

                            eventTypeNameTextView.setText("");
                            getAllEventTypes();

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
                
            case R.id.searchEventTypeButton:
                String eventTypeName = nameEventTypeSearchedACTextView.getText().toString();
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                try {
                    EventType searchedEventType = database.getEventTypeByName(eventTypeName);
                    if (searchedEventType != null) {
                        update(searchedEventType);
                        nameEventTypeSearchedACTextView.setText("");
                    } else {
                        Dialog d = new Dialog(this);
                        d.setTitle(getString(R.string.eventTypeDoesntExistMessage));
                        d.show();
                    }
                } catch (Exception e) {
                    Dialog d = new Dialog(this);
                    d.setTitle(e.getMessage());
                    d.show();
                }
                break;


            case R.id.editEventTypeButton:
                if (idLastButton != -1) {

                    builder.setTitle(getString(R.string.editEventTypeButton));
                    builder.setMessage(getString(R.string.enterNewEventTypeName));

                    EventType eventType = database.getEventTypeById(idLastButton);

                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    final EditText editName = new EditText(this);

                    editName.setText(eventType.getName());

                    layout.setLayoutParams(lp);
                    layout.addView(editName);
                    builder.setView(layout);


                    builder.setNegativeButton(getString(R.string.cancel),  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            editEventTypeName(idLastButton, editName.getText().toString());
                        }
                    });
                    dialog = builder.create();
                    dialog.show();

                } else {
                    Dialog d = new Dialog(this);
                    d.setTitle(getString(R.string.selectEventTypeMessage));
                    d.show();
                }
                break;

            case R.id.deleteEventTypeButton:
                // Dialog popup building
                builder.setTitle(getString(R.string.confirmation));
                builder.setMessage(getString(R.string.deleteEventTypeConfirmationQuestion));
                builder.setNegativeButton(getString(R.string.no), null);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteEventType();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;


            case R.id.backToPreparationButton:
                finish();
                break;
        }
    }
}

