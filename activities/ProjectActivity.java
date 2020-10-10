package com.easynote.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easynote.objects.Project;
import com.easynote.tools.Database;

import java.util.ArrayList;

public class ProjectActivity extends Activity implements OnClickListener {
	Button addProjectButton, deleteProjectButton, editProjectButton,
			backToPreparationButton, lastTouchedButton;
	EditText projectNameEditText;
	LinearLayout linearProject;

	int idLastButton = -1;




	Database database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_activity);

		database = new Database(this);
		database.open();

		//Remove the focus on the EditText at the begining of the activity
		findViewById(R.id.parentEditText).requestFocus();



		addProjectButton = (Button) findViewById(R.id.addProjectButton);
		addProjectButton.setOnClickListener(this);

		deleteProjectButton = (Button) findViewById(R.id.deleteProjectButton);
		deleteProjectButton.setOnClickListener(this);

		editProjectButton = (Button) findViewById(R.id.editProjectButton);
		editProjectButton.setOnClickListener(this);

        backToPreparationButton = (Button) findViewById(R.id.backToPreparationButton);
        backToPreparationButton.setOnClickListener(this);

		linearProject = (LinearLayout) findViewById(R.id.linearProject);

		projectNameEditText = (EditText) findViewById(R.id.projectNameEditText);

		updateProjects();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        database.close();
    }

    /**
     * Change the color of the button
     * @param button the button we want to change
     */
	public void setColorButton(Button button) {
		button.getBackground().setColorFilter(Color.LTGRAY,
				PorterDuff.Mode.MULTIPLY);
	}


    /**
     * Update the list of the projects
     */
	public void updateProjects() {
		linearProject.removeAllViews();
        ArrayList<Project> projectsList = database.getProjects();

		for (Project project : projectsList) {
			Button projectButton = new Button(this);
            projectButton.setId(project.getId());
            projectButton.setText(project.getName());

            projectButton.setOnClickListener(new OnClickListener() {

    			@Override
				public void onClick(View arg1) {
					if (idLastButton == -1) {
						idLastButton = arg1.getId();
						arg1.getBackground().setColorFilter(Color.YELLOW,PorterDuff.Mode.MULTIPLY);
						lastTouchedButton = (Button) arg1;
					}

					if (idLastButton != arg1.getId() && idLastButton != -1) {
						idLastButton = arg1.getId();
						arg1.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
						setColorButton(lastTouchedButton);
						lastTouchedButton = (Button) arg1;
					}
				}
			});

			linearProject.addView(projectButton);
		}
	}

    /**
     * Check if the project's name already exists
     * @param projectName the project's name
     * @return true if it exists, false if not
     */
	public boolean doesProjectNameExist(String projectName) {
		Boolean doesProjectNameExist = false;
        ArrayList<Project> projectsList = database.getProjects();
		for (Project project : projectsList) {
			if (project.getName().equals(projectName)) {
                doesProjectNameExist = true;
			}
		}
		return doesProjectNameExist;
	}

    /**
     * Delete the selected project
     */
	private void deleteProject() {
		database.deleteProject(idLastButton);
		updateProjects();
		idLastButton = -1;
	}

    /**
     * Edit the project's name
     * @param projectName the new name of the project
     */
    private void editProjectName(String projectName) {
        Boolean doesProjectNameExist = doesProjectNameExist(projectName);

        if (!doesProjectNameExist) {
            Project project = new Project(idLastButton, projectName);
            database.updateProject(project);
            updateProjects();

            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.editConfirmationMessage), Toast.LENGTH_LONG).show();
        } else {
            Dialog d = new Dialog(this);
            d.setTitle(getString(R.string.nameAlreadyExistsMessage));
            d.show();
        }
    }

    /**
     * Action on the button
     * @param arg0 the touched button
     */
	public void onClick(View arg0) {
		switch (arg0.getId()) {

			case R.id.addProjectButton:
				try {
					String projectName = projectNameEditText.getText().toString();
					if (!projectName.isEmpty()) {
						Boolean doesProjectNameExist = doesProjectNameExist(projectName);

						if (!doesProjectNameExist) {
							Project project = new Project(projectName);
							database.createProject(project);
							linearProject.removeAllViews();
							updateProjects();

							projectNameEditText.setText("");

						} else {
							Dialog d = new Dialog(this);
							d.setTitle(getString(R.string.nameAlreadyExistsMessage));
							d.show();
						}
					}
				} catch (Exception e) {
					Dialog d = new Dialog(this);
					d.setTitle(getString(R.string.selectTypeProjectMessage));
					d.show();
				}
				break;


			case R.id.editProjectButton:
				if (idLastButton != -1) {

					AlertDialog.Builder builder = new AlertDialog.Builder(this);

					builder.setTitle(getString(R.string.editProjectType));
					builder.setMessage(getString(R.string.enterNewProjectName));

					Project project = database.getProjectById(idLastButton);

					final EditText editProjectName = new EditText(this);
					editProjectName.setText(project.getName());
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					editProjectName.setLayoutParams(lp);
					builder.setView(editProjectName);

					builder.setNegativeButton(getString(R.string.cancel), null);

					builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							editProjectName(editProjectName.getText().toString());
						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();

				} else {
					Dialog d = new Dialog(this);
					d.setTitle(getString(R.string.selectProjectMessage));
					d.show();
				}
				break;

			case R.id.deleteProjectButton:
				if(idLastButton != -1) {
					// Dialog popup building
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(getString(R.string.confirmation));
					builder.setMessage(getString(R.string.deleteProjectConfirmationMessage));
					builder.setNegativeButton(getString(R.string.no), null);
					builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							deleteProject();
						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					Dialog d = new Dialog(this);
					d.setTitle(getString(R.string.selectProjectMessage));
					d.show();
				}
				break;


			case R.id.backToPreparationButton:
				finish();
				break;
		}

	}

}
