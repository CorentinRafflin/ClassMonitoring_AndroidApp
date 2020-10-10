package com.easynote.activities;

import java.util.ArrayList;

import com.easynote.objects.Project;
import com.easynote.objects.Student;
import com.easynote.objects.Group;
import com.easynote.tools.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StudentsListActivity extends Activity implements OnClickListener {
	Button addStudentButton, deleteStudentButton,editStudentButton,
			lastTouchedButton, deleteAllGroupsButton, deleteSelectedGroupButton,
			backToPreparationButton, searchStudentButton;
	EditText firstNameTextView, lastNameTextView;
	TextView infoGroupTextView;
	LinearLayout studentsListView;
	LinearLayout groupsListView;
	int idLastButton = -1;

    ArrayList<Integer> selectedGroupsIds;

    private AutoCompleteTextView nameStudentSearchedACTextView = null;

    Database database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students_list_activity);

        database = new Database(this);
        // Database opening
        database.open();

        //Remove the focus on the EditText at the beginning of the activity
        findViewById(R.id.parentEditTextStudent).requestFocus();

		addStudentButton = (Button) findViewById(R.id.addStudentButton);
		addStudentButton.setOnClickListener(this);

		firstNameTextView = (EditText) findViewById(R.id.firstNameEditView);
		lastNameTextView = (EditText) findViewById(R.id.lastNameEditView);

        searchStudentButton = (Button) findViewById(R.id.searchStudentButton);
        searchStudentButton.setOnClickListener(this);

		infoGroupTextView = (TextView) findViewById(R.id.infoGroupTextView);

		deleteStudentButton = (Button) findViewById(R.id.deleteStudentButton);
		deleteStudentButton.setOnClickListener(this);

		editStudentButton = (Button) findViewById(R.id.editStudentButton);
		editStudentButton.setOnClickListener(this);

        deleteAllGroupsButton = (Button) findViewById(R.id.deleteAllGroupsButton);
        deleteAllGroupsButton.setOnClickListener(this);

        deleteSelectedGroupButton = (Button) findViewById(R.id.deleteSelectedGroupButton);
        deleteSelectedGroupButton.setOnClickListener(this);

		backToPreparationButton = (Button) findViewById(R.id.backToPreparationButton);
		backToPreparationButton.setOnClickListener(this);

        nameStudentSearchedACTextView = (AutoCompleteTextView) findViewById(R.id.nameStudentSearchedACTextView);
        nameStudentSearchedACTextView.setThreshold(0);

		studentsListView = (LinearLayout) findViewById(R.id.linearStudent);
		groupsListView = (LinearLayout) findViewById(R.id.linearGroup);

        selectedGroupsIds = new ArrayList<>();
        getAllStudents();

	}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Database closing
        database.close();
    }

    /**
     * Search the list of all the students for the autocompletion
     */
    public void getAllStudents() {
        String[] studentsArray = database.getAllStudentsName();
        Log.d("nbrStudents", studentsArray.length+"");
        for(String student: studentsArray) {
            Log.d("getAllStudents", "test " + student);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, studentsArray);
        nameStudentSearchedACTextView.setAdapter(adapter);
    }

	public void setColorButton(Button button, int color) {
		button.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
	}

    /**
     * Update the student button
     * @param student student we want to show
     */
	public void update(Student student) {

		try {
			studentsListView.removeAllViews();

            Button btn = new Button(this);
			btn.setId(student.getId());
			btn.setText(student.toStringWithLoginInParentheses());
			btn.setAllCaps(false);

			btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg1) {
                if (idLastButton == -1) {
	    			idLastButton = arg1.getId();
					arg1.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
					lastTouchedButton = (Button) arg1;
                    updateGroupsOfTheSelectedStudent();
				}

		    	if (idLastButton != arg1.getId() && idLastButton != -1) {
					idLastButton = arg1.getId();
					arg1.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
					setColorButton(lastTouchedButton, Color.GRAY);
					lastTouchedButton = (Button) arg1;
                    updateGroupsOfTheSelectedStudent();
				}
                Log.d("update", idLastButton+"");
				Student student = database.getStudentById(idLastButton);
				infoGroupTextView.setText(String.format(getString(R.string.studentInfoTitle), student.toStringWithLoginInParentheses()));
                infoGroupTextView.setVisibility(View.VISIBLE);
                groupsListView.setVisibility(View.VISIBLE);
				}
            });

            studentsListView.addView(btn);
            groupsListView.removeAllViews();
            infoGroupTextView.setVisibility(View.INVISIBLE);

		} catch (Exception e) {
			Dialog d = new Dialog(this);
			d.setTitle(e.toString());
			d.show();
		}
	}

    /**
     * Show all the groups the selected student belongs to
     * @param groupsList list of groups the student belongs to
     */
	public void showGroupsOfTheSelectedStudent(ArrayList<Group> groupsList) {
		try {
			for (Group group : groupsList) {
				Button groupButton = new Button(this);
                groupButton.setId(group.getId());
                Project project = database.getProjectById(group.getProjectId());
                String projectName = project.getName();
                groupButton.setText(String.format(projectName + " - " + getString(R.string.groupTitle), group.getGroupNumber()));
                groupButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg1) {
                    Button pressedButton = (Button) arg1;

					int groupId = arg1.getId();

					if (!selectedGroupsIds.contains(groupId)) {
                        selectedGroupsIds.add(groupId);
						setColorButton(pressedButton,Color.YELLOW);
					} else {
                        selectedGroupsIds.remove((Integer)groupId);
						setColorButton(pressedButton, Color.GRAY);
					}
					}
				});

				groupsListView.addView(groupButton);
			}

		} catch (Exception e) {
			Dialog d = new Dialog(this);
			d.setTitle(e.getMessage());
			d.show();

		}

	}

    /**
     * Update the groups the selected student belongs to
     */
	public void updateGroupsOfTheSelectedStudent() {
        ArrayList<Group> groupsList = database.getGroupsFromStudent(idLastButton);

		groupsListView.removeAllViews();
		showGroupsOfTheSelectedStudent(groupsList);

		selectedGroupsIds.clear();

	}

    /**
     * Create a login with the first name and the last name
     * @param firstName first name of the student
     * @param lastName last name of the student
     * @return the created login
     */
	public String createLogin(String firstName, String lastName) {
        String login = firstName.charAt(0) + lastName;
        login = login.toLowerCase();
        login = login.replaceAll(" ", "");

		Boolean loginExist = database.loginExist(login);

		String letterFirstName = "" + firstName.charAt(0);
		int i = 1;
		while (loginExist) {

			letterFirstName = letterFirstName + firstName.charAt(i);
			i = i + 1;

			String mlogin = letterFirstName + lastName;
			mlogin = mlogin.toLowerCase();
			mlogin = mlogin.replaceAll(" ", "");

			loginExist = database.loginExist(mlogin);
            login = mlogin;
		}
        return login;
	}

    /**
     * Delete the selected Student
     */
    private void deleteStudent() {
        try {
            database.deleteStudent(idLastButton);
            Button btnRemove = (Button) findViewById(idLastButton);
            studentsListView.removeView(btnRemove);

            String studentRemove = btnRemove.getText().toString();
            Toast.makeText(
                    getApplicationContext(),
                    String.format(getString(R.string.deleteStudentConfirmationMessage), studentRemove), Toast.LENGTH_LONG).show();

            infoGroupTextView.setVisibility(View.INVISIBLE);
            groupsListView.setVisibility(View.INVISIBLE);
            idLastButton = -1;
            selectedGroupsIds.clear();
            getAllStudents();

        } catch (Exception e) {
            Dialog d = new Dialog(this);
            d.setTitle(getString(R.string.selectStudentMessage));
            d.show();
        }
    }

    /**
     * Delete all the groups linked to the student
     */
    private void deleteAllGroups() {
        if (idLastButton != -1) {
            database.removeStudentFromAllGroups(idLastButton);
            updateGroupsOfTheSelectedStudent();
        } else {
            Dialog d = new Dialog(this);
            d.setTitle(getString(R.string.selectStudentMessage));
            d.show();
        }
    }

    /**
     * Delete the selected group linked to the student
     */
    private void deleteSelectedGroup() {
        if (selectedGroupsIds.size() != 0) {
            for(Integer grp_id: selectedGroupsIds) {
                database.removeStudentFromGroup(idLastButton, grp_id);
            }
            updateGroupsOfTheSelectedStudent();
        } else {
            Dialog d = new Dialog(this);
            d.setTitle(getString(R.string.selectGroupMessage));
            d.show();
        }
    }

    /**
     * Edit the names of the selected student
     * @param studentId id of the selected student
     * @param firstName the new first name
     * @param lastName the new last name
     */
    public void editStudentName(int studentId, String firstName, String lastName) {
        String updatedLogin = createLogin(firstName, lastName);
        database.updateStudent(studentId, lastName, firstName, updatedLogin);
        update(database.getStudentById(studentId));

        Toast.makeText(
                getApplicationContext(), getString(R.string.editStudentConfirmationMessage),
                Toast.LENGTH_LONG).show();
    }

    /**
     * Action on the button
     * @param arg0 the touched button
     */
	public void onClick(View arg0) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
		switch (arg0.getId()) {

            case R.id.addStudentButton:
                try {
                    String lastName = lastNameTextView.getText().toString();
                    String firstName = firstNameTextView.getText().toString();
                    if (!firstName.equals("") && !lastName.equals("")) {
                        String login = createLogin(firstName, lastName);
                        Student student = new Student(firstName, lastName, login);
                        long studentId = database.createStudent(student);
                        student.setId((int)studentId);

                        studentsListView.removeAllViews();
                        update(student);

                        firstNameTextView.setText("");
                        lastNameTextView.setText("");
                        getAllStudents();
                    } else {
                        Toast.makeText( getApplicationContext(),getString(R.string.addStudentError),Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Dialog d = new Dialog(this);
                    Log.d("creation", e.toString());
                    d.setTitle(e.toString());
                    d.show();

                }

                break;

            case R.id.searchStudentButton:
                String studentToAdd = nameStudentSearchedACTextView.getText().toString();
                String[] studentInfo = studentToAdd.split("\\s+");
                String login = studentInfo[studentInfo.length - 1]; //String

                try {
                    Log.d("searchedStudent", login);
                    Student searchedStudent = database.getStudentByLogin(login);
                    if (searchedStudent != null) {
                        Log.d("boucle", "ouais");
                        update(searchedStudent);
                        nameStudentSearchedACTextView.setText("");
                    } else {
                        Dialog d = new Dialog(this);
                        d.setTitle(getString(R.string.studentDoesntExistMessage));
                        d.show();
                    }
                } catch (Exception e) {
                    Dialog d = new Dialog(this);
                    d.setTitle(e.getMessage());
                    d.show();
                }
                break;

            case R.id.editStudentButton:
                if (idLastButton != -1) {
                    // Dialog popup building
                    builder.setTitle(getString(R.string.editStudentButton));
                    builder.setMessage(getString(R.string.enterNewStudentName));

                    Student student = database.getStudentById(idLastButton); //String

                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    final EditText editFirstName = new EditText(this);
                    final EditText editLastName = new EditText(this);
                    editFirstName.setText(student.getFirstName());
                    editLastName.setText(student.getLastName());

                    layout.setLayoutParams(lp);
                    layout.addView(editFirstName);
                    layout.addView(editLastName);
                    builder.setView(layout);

                    builder.setNegativeButton(getString(R.string.cancel), null);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editStudentName(idLastButton, editFirstName.getText().toString(), editLastName.getText().toString());
                        }
                    });
                    dialog = builder.create();
                    dialog.show();

                } else {
                    Dialog d = new Dialog(this);
                    d.setTitle(getString(R.string.selectStudentMessage));
                    d.show();
                }
                break;

            case R.id.deleteStudentButton:
                // Dialog popup building
                builder.setTitle(getString(R.string.confirmation));
                builder.setMessage(getString(R.string.deleteStudentConfirmationQuestion));
                builder.setNegativeButton(getString(R.string.no), null);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteStudent();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.deleteAllGroupsButton:
                // Dialog popup building
                builder.setTitle(getString(R.string.confirmation));
                builder.setMessage(getString(R.string.deleteGroupsConfirmationQuestion));
                builder.setNegativeButton(getString(R.string.no), null);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAllGroups();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.deleteSelectedGroupButton:
                // Dialog popup building
                builder.setTitle(getString(R.string.confirmation));
                builder.setMessage(getString(R.string.deleteGroupConfirmationQuestion));
                builder.setNegativeButton(getString(R.string.no), null);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteSelectedGroup();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.backToPreparationButton:
                Intent myIntent = new Intent(StudentsListActivity.this, PreparationActivity.class);
                StudentsListActivity.this.startActivity(myIntent);
                break;
        }
	}
}
