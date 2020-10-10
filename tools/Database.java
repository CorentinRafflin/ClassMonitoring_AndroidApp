package com.easynote.tools;

import com.easynote.objects.Equipment;
import com.easynote.objects.Event;
import com.easynote.objects.EventType;
import com.easynote.objects.Group;
import com.easynote.objects.Meeting;
import com.easynote.objects.MeetingType;
import com.easynote.objects.Observation;
import com.easynote.objects.Project;
import com.easynote.objects.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Pair;

public class Database {


    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE);

    // Project Table
    private static final String ProjectTable = "Project";
    public static final String PJT_ID = "PJT_ID";
    public static final String PJT_name = "PJT_name";

    // Group Table
    private static final String GroupTable = "Groupe";
    public static final String GRP_ID = "GRP_ID";
    public static final String GRP_num = "GRP_num";
    public static final String GRP_PJT_ID = "GRP_PJT_ID";

    // Student Table
    private static final String StudentTable = "Student";
    public static final String STD_ID = "STD_ID";
	public static final String STD_lastname = "STD_lastname";
	public static final String STD_firstname = "STD_firstname";
	public static final String STD_login = "STD_login";

    // JunctionGroupStudent Table
    private static final String JunctionGroupStudentTable = "JunctionGroupStudent";
    public static final String JCT_GRPSTD_ID = "JCT_GRPSTD_ID";
    public static final String JCT_GRPSTD_STD_ID = "JCT_GRPSTD_STD_ID";
    public static final String JCT_GRPSTD_GRP_ID = "JCT_GRPSTD_GRP_ID";

    // MeetingType Table
    private static final String MeetingTypeTable = "MeetingType";
    public static final String TMT_ID = "TMT_ID";
    public static final String TMT_name = "TMT_name";

    // Meeting Table
    private static final String MeetingTable = "Meeting";
    public static final String MTG_ID = "MTG_ID";
    public static final String MTG_name = "MTG_name";
    public static final String MTG_date = "MTG_date";
    public static final String MTG_date_end = "MTG_date_end";
    public static final String MTG_PJT_ID = "MTG_PJT_ID";
    public static final String MTG_TMT_ID = "MTG_TMT_ID";

    // EventType Table
    private static final String EventTypeTable = "EventType";
    public static final String TEV_ID = "TEV_ID";
    public static final String TEV_name = "TEV_name";
    public static final String TEV_equipmentName = "TEV_equipmentName";

    //JunctionMeetingTypeEventType Table
    private static final String JunctionMeetingTypeEventTypeTable = "JunctionMeetingTypeEventType";
    public static final String JCT_TMTTEV_ID = "JCT_TMTTEV_ID";
    public static final String JCT_TMTTEV_TMT_ID = "JCT_TMTTEV_TMT_ID";
    public static final String JCT_TMTTEV_TEV_ID = "JCT_TMTTEV_TEV_ID";

    // ObservationTable = JunctionMeetingGroupTable
    private static final String ObservationTable = "Observation";
    public static final String OBS_ID = "OBS_ID";
    public static final String OBS_GRP_ID = "OBS_GRP_ID";
    public static final String OBS_MTG_ID = "OBS_MTG_ID";

    // Event Table
    private static final String EventTable = "Event";
    public static final String EVT_ID = "EVT_ID";
    public static final String EVT_TEV_ID = "EVT_TEV_ID";
    public static final String EVT_date = "EVT_date";
    public static final String EVT_comment = "EVT_comment";
    public static final String EVT_OBS_ID = "EVT_OBS_ID";
    public static final String EVT_equipmentName = "EVT_equipmentName";
    public static final String EVT_STD_ID = "EVT_STD_ID";


    private static Context ourContext;

	public Database(Context c) {
		ourContext = c;
	}

	public Database open() throws SQLException {
		return this;
	}

	public void close() {
	}


/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* STUDENT METHOD */


    /**
     * Create a student in database
     * @param student student we want to save
     * @return the row id of the newly group inserted
     */
    public long createStudent(Student student) {
        Student databaseStudent = getStudentByLogin(student.getLogin());
        if (databaseStudent == null) {
            CreateStudentClass task = new CreateStudentClass();
            try {
                long studentId = task.execute(student.getFirstName(), student.getLastName(), student.getLogin()).get();
                Log.d("studentid", "" + studentId);


                return studentId;
            }
            catch(Exception e) {
                Log.d("createStudent", e.toString());
            }
        }
        return -1;
    }

    static class CreateStudentClass extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... strings) {
            Long idLong=0L;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "createStudent");
            params.put(STD_firstname, strings[0]);
            params.put(STD_lastname, strings[1]);
            params.put(STD_login, strings[2]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                Log.d("*****", json.toString());
                idLong=json.getLong("id");
                Log.d("l'id est", ""+idLong);
            } catch (Exception e) {
                Log.d(">>", e.toString());
            }
            return idLong;
        }
    }


    /**
     * Update a student
     * @param studentId id of the student
     * @param studentLastName last name of the student
     * @param studentFirstName first name of the student
     * @param studentLogin login of the student
     * @throws SQLException
     */
    public void updateStudent(int studentId, String studentLastName,
                              String studentFirstName, String studentLogin) throws SQLException {
        UpdateStudentAsync task = new UpdateStudentAsync();
        try {
            task.execute(studentId +"", studentLastName, studentFirstName, studentLogin);
        }
        catch(Exception e) {
            Log.d("updateStudent", e.toString());
        }
    }

    static class UpdateStudentAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "updateStudent");
            params.put(STD_ID, strings[0]);
            params.put(STD_lastname, strings[1]);
            params.put(STD_firstname, strings[2]);
            params.put(STD_login, strings[3]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("updateStudentAsync", e.toString());
            }
            return null;
        }
    }

    /**
     * Delete a student
     * @param id id of the student to delete
     * @throws SQLException
     */
    public void deleteStudent(int id) throws SQLException {
        DeleteStudentAsync task = new DeleteStudentAsync();
        task.execute(id+"");
    }

    static class DeleteStudentAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "deleteStudent");
            params.put(STD_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteStudentASYNC", e.toString());
            }
            return null;
        }
    }
    /**
     * Remove the student from all the groups he belongs to
     * @param studentID id of the student
     * @throws SQLException
     */
    public void removeStudentFromAllGroups(int studentID) throws SQLException {
        RemoveStudentFromAllGroupsAsync task = new RemoveStudentFromAllGroupsAsync();
        task.execute(studentID+"");
    }

    static class RemoveStudentFromAllGroupsAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "removeStudentFromAllGroups");
            params.put(STD_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("removeStudentAllGrpsAsy", e.toString());
            }
            return null;
        }
    }

    /**
     * Check if a login already exists
     * @param login the login to test
     * @return true if the login already exists, false if not
     */
    public boolean loginExist(String login) {
        boolean loginExist=true;
        LoginExistAsync task = new LoginExistAsync();
        try {
            loginExist = task.execute(login).get();
        }
        catch(Exception e) {
            Log.d("loginExist", "erreur");
        }
        return loginExist;
    }

    static class LoginExistAsync extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... strings) {
            Boolean loginExist=null;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "loginExist");
            params.put(STD_login, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                loginExist=json.getBoolean("loginExist");
            } catch (Exception e) {
                Log.d(">>", e.toString());
            }
            return loginExist;
        }
    }

    /**
     * Add a student to a group
     * @param idLastGroup id of the group
     * @param idStudent id of the student
     */
    public void addStudentToGroup(int idLastGroup, int idStudent) {
        AddStudentToGroupAsync task = new AddStudentToGroupAsync();
        try {
            task.execute(idLastGroup + "", idStudent + "").get();
        }
        catch(Exception e) {
            Log.d("addStudentToGroup", e.toString());
        }
    }

    static class AddStudentToGroupAsync extends AsyncTask<String, Void, Void> {
        //TODO vérifier qu'il n'est pas dans un autre groupe du meme projet
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "addStudentToGroup");
            params.put(GRP_ID, strings[0]);
            params.put(STD_ID, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("addStudentToGrpAsync", e.toString());
            }
            return null;
        }
    }

    /**
     * Search the student with id idLastButton
     * @param idLastButton : id of the student we search
     * @return the student with the id idLastButton
     * @throws SQLException
     */
    public Student getStudentById(int idLastButton) throws SQLException {
        GetStudentByIdAsync task = new GetStudentByIdAsync();
        Student student=null;
        try {
            String[] studentData = task.execute(""+idLastButton).get();
            for(String s: studentData) {
                Log.d("****", s);
            }
            student= new Student(Integer.parseInt(studentData[0]), studentData[1], studentData[2], studentData[3]);
        }
        catch(Exception e) {

            Log.d("ExceptionGetStudentByID", e.toString());
        }
        return student;
    }

    static class GetStudentByIdAsync extends AsyncTask<String, Void, String[]> {
        protected String[] doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getStudentById");
            Log.d("-----------", strings[0]);
            params.put(STD_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            String[] studentData= new String[4];
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                studentData[0] = json.getString(STD_ID);
                studentData[1] = json.getString(STD_firstname);
                studentData[2] = json.getString(STD_lastname);
                studentData[3] = json.getString(STD_login);
            } catch (Exception e) {
                Log.d(">>>>>>>>", e.toString());
            }
            return studentData;
        }
    }

    /**
     * Search the student with his login
     * @param login : login of the student we search
     * @return the student with the login
     * @throws SQLException
     */
    public Student getStudentByLogin(String login) throws SQLException {
        GetStudentByLoginAsync task = new GetStudentByLoginAsync();
        Student student=null;
        try {
            String[] studentData = task.execute(login).get();
            student= new Student(Integer.parseInt(studentData[0]), studentData[1], studentData[2], studentData[3]);
        }
        catch(Exception e) {
            Log.d("ExcGetStudentByLogin", e.toString());
        }
        return student;
    }

    static class GetStudentByLoginAsync extends AsyncTask<String, Void, String[]> {
        protected String[] doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getStudentByLogin");
            params.put(STD_login, strings[0]);
            JSONParser jsonParser = new JSONParser();
            String[] studentData= new String[4];
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                studentData[0] = json.getString(STD_ID);
                studentData[1] = json.getString(STD_firstname);
                studentData[2] = json.getString(STD_lastname);
                studentData[3] = json.getString(STD_login);
            } catch (Exception e) {
                Log.d("studentbyloginasync", e.toString());
            }
            return studentData;
        }
    }
    /**
     * Search all students of a group
     * @param groupID id of the group
     * @return a list of the group's students
     */
    public ArrayList<Student> getStudentsFromGroup(int groupID) {
        ArrayList<Student> students=null;
        GetStudentsFromGroupAsync task = new GetStudentsFromGroupAsync();
        try {
            students = task.execute(""+groupID).get();
        }
        catch(Exception e) {
            Log.d("getStudentsFromGroup", "error");
        }
        return students;
    }

    static class GetStudentsFromGroupAsync extends AsyncTask<String, Void, ArrayList<Student>> {
        protected ArrayList<Student> doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getStudentsFromGroup");
            params.put(GRP_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            ArrayList<Student> students= new ArrayList<Student>();
            int id_student;
            String firstname;
            String lastname;
            String login;
            Student student;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrGrps= json.getInt("a"+0);
                for(int i=1; i<nbrGrps; i++) {
                    Log.d("i=", i+"");
                    id_student=json.getInt("a"+i+"_"+STD_ID);
                    firstname = json.getString("a"+i+"_"+STD_firstname);
                    lastname = json.getString("a"+i+"_"+STD_lastname);
                    login = json.getString("a"+i+"_"+STD_login);
                    student = new Student(id_student, firstname, lastname, login);
                    students.add(student);
                }

            } catch (Exception e) {
                Log.d("GetStudentsFromGroup", e.toString());
            }
            return students;
        }
    }



    /**
     * Search all students linked to an event
     * @param eventId id of the event
     * @return a list of the students
     */
    static private Student getStudentFromEvent(int eventId) throws SQLException {
        GetStudentFromEventAsync task = new GetStudentFromEventAsync();
        Student student=null;
        try {
            student = task.execute(eventId+"").get();
        }
        catch(Exception e) {
            Log.d("ExcGetStudentByLogin", e.toString());
        }
        return student;
    }

    static class GetStudentFromEventAsync extends AsyncTask<String, Void, Student> {
        protected Student doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getStudentFromEvent");
            params.put(EVT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            int id;
            String firstname;
            String lastname;
            String login;
            Student student = null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                id = json.getInt(STD_ID);
                firstname = json.getString(STD_firstname);
                lastname = json.getString(STD_lastname);
                login = json.getString(STD_login);
                student = new Student(id, firstname, lastname, login);
            } catch (Exception e) {
                Log.d("getStudFromEventAsync", e.toString());
            }
            return student;
        }
    }



    /**
     * Return names of all the students
     * @return an array of all students' names
     */
    public String[] getAllStudentsName() throws SQLException {
        GetAllStudentsNameAsync task = new GetAllStudentsNameAsync();
        String[] students=null ;
        try {
            students = task.execute().get();
        }
        catch(Exception e) {
            Log.d("getAllStudentsName", e.toString());
        }
        return students;
    }

    static class GetAllStudentsNameAsync extends AsyncTask<Void, Void, String[]> {
            protected String[] doInBackground(Void... v) {
                Map<String, String> params = new HashMap<>();
                params.put("fonction", "getAllStudentsName");
                JSONParser jsonParser = new JSONParser();
                String[] students=null;
                try {
                    JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                    int nbrStudents= json.getInt("a"+0);
                    students = new String[nbrStudents-2];
                    for(int i=1; i<nbrStudents; i++) {
                        Log.d("i=", i+"");
                        students[i-1] = json.getString("a"+i);
                    }
                } catch (Exception e) {
                    Log.d("getAllStudentsNameAsync", e.toString());
                }
                return students;
            }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*  GROUP METHOD */

	/**
	 * Create a group in database
	 * @param groupNumber group's number of the group we want to create
	 * @param idProject project's id of the project linked to the group
	 * @return the row id of the newly group inserted
	 */
    public long createGroup(long groupNumber, int idProject) {
        CreateGroupAsync task = new CreateGroupAsync();
        long grpId = 0;
        try {
            grpId = task.execute(groupNumber +"", idProject + "").get();

        }
        catch(Exception e) {
            Log.d("createGroup", e.toString());
        }
        return grpId;
    }

    static class CreateGroupAsync extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... strings) {
            Long idLong=0L;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "createGroup");
            params.put(GRP_num, strings[0]);
            params.put(GRP_PJT_ID, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                idLong = json.getLong(GRP_ID);
            } catch (Exception e) {
                Log.d("createGroupAsync", e.toString());
            }
            return idLong;
        }
    }


    /**
     * Update the group's number of the group
     * @param groupID group we want to update
     * @param groupNumber the new group's number
     * @throws SQLException
     */
    public void updateGroupNumber(int groupID, String groupNumber) throws SQLException {
        UpdateGroupNumberAsync task = new UpdateGroupNumberAsync();
        try {
            task.execute(groupID +"", groupNumber +"");
        }
        catch(Exception e) {
            Log.d("createStudent", e.toString());
        }
    }

    static class UpdateGroupNumberAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "updateGroupNumber");
            params.put(GRP_ID, strings[0]);
            params.put(GRP_num, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("updateGroupNumberAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Delete the group with id id
     * @param id id of the group we want to delete
     * @throws SQLException
     */
    public void deleteGroup(int id) throws SQLException {
        //TODO verifier le on delete cascade
        DeleteGroupAsync task = new DeleteGroupAsync();
        task.execute(id+"");
    }

    static class DeleteGroupAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "deleteGroup");
            params.put(GRP_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteGroupAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Remove the student form the specified groups
     * @param studentID id of the student
     * @param grpID id of the group
     * @throws SQLException
     */
    public void removeStudentFromGroup(int studentID, int grpID) throws SQLException {
        RemoveStudentFromGroupAsync task = new RemoveStudentFromGroupAsync();
        task.execute(studentID+"", grpID + "");
    }

    static class RemoveStudentFromGroupAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "removeStudentFromGroup");
            params.put(STD_ID, strings[0]);
            params.put(GRP_ID, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("rmStudentFromGroupAsync", e.toString());
            }
            return null;
        }
    }

    /**
     * Remove all students from a group
     * @param groupID the group we want to empty
     * @throws SQLException
     */
    public void removeAllStudentsFromGroup(int groupID) throws SQLException {
        RemoveAllStudentsFromGroupAsync task = new RemoveAllStudentsFromGroupAsync();
        task.execute(groupID + "");
    }

    static class RemoveAllStudentsFromGroupAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "removeAllStudentsFromGroup");
            params.put(GRP_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("rmAllStudentFromGrpAsy", e.toString());
            }
            return null;
        }
    }


    /**
     * Get a group with its id
     * @param groupId id of the group
     * @return the group
     */
    public Group getGroup(int groupId) {
        GetGroupAsync task = new GetGroupAsync();
        Group group = null;
        try {
            group = task.execute(groupId + "").get();
        } catch (Exception e) {
            Log.d("getGroup", "error");
        }
        return group;
    }


    static class GetGroupAsync extends AsyncTask<String, Void, Group> {
        protected Group doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getGroup");
            params.put(GRP_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            Group group = null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                group = new Group(json.getInt(GRP_ID), json.getInt(GRP_num));
            } catch (Exception e) {
                Log.d("getGroupAsync", e.toString());
            }
            return group;
        }
    }
    /**
     * Get a group from an observation
     * @param observationId id of the observation
     * @return the group
     */
    public Group getGroupFromObservation(int observationId) {
        GetGroupFromObservationAsync task = new GetGroupFromObservationAsync ();
        Group group = null;
        try {
            group = task.execute(observationId + "").get();
        } catch (Exception e) {
            Log.d("getGroupFromObs", e.toString());
        }
        return group;
    }


    static class GetGroupFromObservationAsync  extends AsyncTask<String, Void, Group> {
        protected Group doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getGroupFromObservation");
            params.put(OBS_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            Group group = null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                group = new Group(json.getInt(GRP_ID), json.getInt(GRP_num));
            } catch (Exception e) {
                Log.d("getGroupFromObsAsync", e.toString());
            }
            return group;
        }
    }

    /**
     * Search all the groups to which a student belongs
     * @param studentId : the id of the student
     * @return a list of groups to which the student belongs
     */
    public ArrayList<Group> getGroupsFromStudent(int studentId) {
        GetGroupsFromStudentAsync task = new GetGroupsFromStudentAsync();
        ArrayList<Group> groupsList = new ArrayList<>();
        try {
            groupsList = task.execute(studentId+"").get();
        }
        catch(Exception e) {
            Log.d("getGroupsFromStudent", e.toString());
        }
        return groupsList;
    }

    static class GetGroupsFromStudentAsync extends AsyncTask<String, Void, ArrayList<Group>> {
        protected ArrayList<Group> doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getGroupsFromStudent");
            params.put(STD_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            ArrayList<Group> groups = new ArrayList<Group>();
            int id_grp;
            int grp_num;
            Group group;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrGrps= json.getInt("a"+0);
                for(int i=1; i<nbrGrps; i++) {
                    Log.d("i=", i + "");
                    id_grp = json.getInt("a" + i + "_" + GRP_ID);
                    grp_num = json.getInt("a" + i + "_" + GRP_num);
                    group = new Group(id_grp, grp_num);
                    groups.add(group);
                }
            }
            catch (Exception e) {
                Log.d("getGroupFromStudentAsyn", e.toString());
            }
            return groups;
        }
    }

    /**
     * Get the name of the groups linked to the project
     * @param idProject id of the project
     * @return an ArrayList of the groups linked to the project
     */
    public ArrayList<Group> getGroupsFromProject(int idProject) {
        GetGroupsFromProjectAsync task = new GetGroupsFromProjectAsync();
        ArrayList<Group> groupsList = new ArrayList<>();
        try {
            groupsList = task.execute(idProject+"").get();
        }
        catch(Exception e) {
            Log.d("getGroupsFromProject", e.toString());
        }
        return groupsList;
    }

    static class GetGroupsFromProjectAsync extends AsyncTask<String, Void, ArrayList<Group>> {
        protected ArrayList<Group> doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getGroupsFromProject");
            params.put(PJT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            ArrayList<Group> groups = new ArrayList<Group>();
            int id_grp;
            int grp_num;
            Group group;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrGrps= json.getInt("a"+0);
                for(int i=1; i<nbrGrps; i++) {
                    Log.d("i=", i + "");
                    id_grp = json.getInt("a" + i + "_" + GRP_ID);
                    grp_num = json.getInt("a" + i + "_" + GRP_num);
                    group = new Group(id_grp, grp_num);
                    groups.add(group);
                }
            }
            catch (Exception e) {
                Log.d("getGroupById", e.toString());
            }
            return groups;
        }
    }
    /**
     * Get the name of the groups linked to the project
     * @param idProject id of the project
     * @return a StringArray of the gorups linked to the project
     */
    public String[] getGroupsFromProjectArray(int idProject) {
        ArrayList<Group> groups = getGroupsFromProject(idProject);
        String[] numGroups = new String[groups.size()];
        int i=0;
        for(Group g : groups) {
            numGroups[i] = g.getGroupNumber() + "";
            i++;
        }
        return numGroups;
    }
    /**
     * Search all groups of a meeting
     * @param meetingId id of the meeting
     * @return a list of the groups of the meeting
     */
    public ArrayList<Group> getGroupsFromMeetingId(int meetingId) {
        GetGroupsFromMeetingIdAsync task = new GetGroupsFromMeetingIdAsync();
        ArrayList<Group> groupsList = new ArrayList<>();
        try {
            groupsList = task.execute(meetingId+"").get();
        }
        catch(Exception e) {
            Log.d("getGroupsFromMeeting", e.toString());
        }
        return groupsList;
    }

    static class GetGroupsFromMeetingIdAsync extends AsyncTask<String, Void, ArrayList<Group>> {
        protected ArrayList<Group> doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getGroupsFromMeetingId");
            params.put(MTG_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            ArrayList<Group> groups = new ArrayList<Group>();
            int id_grp;
            int grp_num;
            int projet_id;
            Group group;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrGrps= json.getInt("a"+0);
                for(int i=1; i<nbrGrps; i++) {
                    Log.d("i=", i + "");
                    id_grp = json.getInt("a" + i + "_" + GRP_ID);
                    grp_num = json.getInt("a" + i + "_" + GRP_num);
                    projet_id = json.getInt("a" + i + "_" + PJT_ID);
                    group = new Group(id_grp, grp_num, projet_id);
                    groups.add(group);
                }
            }
            catch (Exception e) {
                Log.d("getGroupsMeetingAsync", e.toString());
            }
            return groups;
        }
    }
    /**
     * Get the name of the groups linked to the meeting
     * @param idMeeting id of the meeting
     * @return a StringArray of the groups linked to the meeting
     */
    public String[] getGroupsArrayFromMeetingId(int idMeeting) {
        ArrayList<Group> groups = getGroupsFromMeetingId(idMeeting);
        String[] numGroups = new String[groups.size()];
        int i=0;
        for(Group g : groups) {
            numGroups[i] = "Groupe " + g.getGroupNumber();
            i++;
        }
        return numGroups;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*  PROJECT METHOD */

    /**
     * Create a project in database
     * @param project Project we want to create
     * @return the row id of the newly project inserted
     */
	public long createProject(Project project) {
        CreateProjectAsync task = new CreateProjectAsync();
        long pjtId = 0;
        try {
            pjtId = task.execute(project.getName()).get();

        }
        catch(Exception e) {
            Log.d("createProjet", e.toString());
        }
        return pjtId;
    }

    static class CreateProjectAsync extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... strings) {
            Long idLong=0L;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "createProject");
            params.put(PJT_name, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                idLong = json.getLong(PJT_ID);
            } catch (Exception e) {
                Log.d("createProjectAsync", e.toString());
            }
            return idLong;
        }
    }
    /**
     * Update the title of the project
     * @param project the project we want to edit
     * @throws SQLException
     */
    public void updateProject(Project project) throws SQLException {
        UpdateProjectAsync task = new UpdateProjectAsync();
        try {
            task.execute(project.getId() + "", project.getName());
        }
        catch(Exception e) {
            Log.d("updateProject", e.toString());
        }
    }

    static class UpdateProjectAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "updateProject");
            params.put(PJT_ID, strings[0]);
            params.put(PJT_name, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("updateProjectAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Delete the project with id id
     * @param id id of the project we want to delete
     * @throws SQLException
     */
    public void deleteProject(int id) throws SQLException {
        DeleteProjectAsync task = new DeleteProjectAsync();
        task.execute(id + "");
    }

    static class DeleteProjectAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "deleteProject");
            params.put(PJT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteprojectAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Get a project with its id
     * @param projectId id of the project
     * @return the project
     */
    public Project getProjectById(int projectId) {
        GetProjectByIdAsync task = new GetProjectByIdAsync();
        Project project =null;
        try {
            project = task.execute(projectId+"").get();
        }
        catch(Exception e) {
            Log.d("getProjectById", e.toString());
        }
        return project;
    }


    static class GetProjectByIdAsync extends AsyncTask<String, Void, Project> {
        protected Project doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getProjectById");
            params.put(PJT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            int project_id;
            String project_name;
            Project project=null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                project_id = json.getInt(PJT_ID);
                project_name = json.getString(PJT_name);
                project = new Project(project_id, project_name);
            }
            catch (Exception e) {
                Log.d("getProjectByIdAsync", e.toString());
            }
            return project;
        }
    }
    /**
     * Get a project from a meeting Id
     * @param meetingId id of the meeting
     * @return the project
     */
    public Project getProjectFromMeetingId(int meetingId) {
        GetProjectFromMeetingIdAsync task = new GetProjectFromMeetingIdAsync();
        Project project =null;
        try {
            project = task.execute(meetingId+"").get();
        }
        catch(Exception e) {
            Log.d("getProjectFromMeetingId", e.toString());
        }
        return project;
    }


    static class GetProjectFromMeetingIdAsync extends AsyncTask<String, Void, Project> {
        protected Project doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getProjectById");
            params.put(MTG_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            int project_id;
            String project_name;
            Project project=null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                project_id = json.getInt(PJT_ID);
                project_name = json.getString(PJT_name);
                project = new Project(project_id, project_name);
            }
            catch (Exception e) {
                Log.d("getProjectMeetingAsync", e.toString());
            }
            return project;
        }
    }
    /**
     * Return an ArrayList of all the projects
     * @return an array of the projects
     */
    public ArrayList<Project> getProjects() {
        GetProjectsAsync task = new GetProjectsAsync();
        ArrayList<Project> projectsList = new ArrayList<>();
        try {
            projectsList = task.execute().get();
        }
        catch(Exception e) {
            Log.d("getProjects", e.toString());
        }
        return projectsList;
    }

    static class GetProjectsAsync extends AsyncTask<Void, Void, ArrayList<Project>> {
        protected ArrayList<Project> doInBackground(Void ... v) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getProjects");
            JSONParser jsonParser = new JSONParser();
            ArrayList<Project> projects = new ArrayList<>();
            int project_id;
            String project_name;
            Project project;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrProjects= json.getInt("a"+0);
                for(int i=1; i<nbrProjects; i++) {
                    Log.d("i=", i + "");
                    project_id = json.getInt("a" + i + "_" + PJT_ID);
                    project_name = json.getString("a" + i + "_" + PJT_name);
                    project = new Project(project_id, project_name);
                    projects.add(project);
                }
            }
            catch (Exception e) {
                Log.d("getProjectsAsync", e.toString());
            }
            return projects;
        }
    }

    /**
     * Return a String Array of all the projects
     * @return a String Array of all the projects
     */
    public String[] getProjectsArray() {
        ArrayList<Project> projects = getProjects();
        String[] projectsNames = new String[projects.size()];
        int i=0;
        for(Project p : projects) {
            projectsNames[i] = p.getName();
            i++;
        }
        return projectsNames;
    }





/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*  MEETINGTYPE METHOD */

    /**
     * Create a meetingType in database
     * @param meetingType MeetingType we want to create
     * @return the row id of the newly project inserted
     */
    public long createMeetingType(MeetingType meetingType) {
        CreateMeetingTypeAsync task = new CreateMeetingTypeAsync();
        long tmtId = 0;
        try {
            tmtId = task.execute(meetingType.getName()).get();

        }
        catch(Exception e) {
            Log.d("createMeetingType", e.toString());
        }
        return tmtId;
    }

    static class CreateMeetingTypeAsync extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... strings) {
            Long idLong=0L;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "createMeetingType");
            params.put(TMT_name, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                idLong = json.getLong(TMT_ID);
            } catch (Exception e) {
                Log.d("createMeetingTypeAsync", e.toString());
            }
            return idLong;
        }
    }
    /**
     * Update the name of the meeting type
     * @param meetingTypeId id of the meeting type we want to rename
     * @param name new name
     * @throws SQLException
     */
    public void updateMeetingType(int meetingTypeId, String name) throws SQLException {
        UpdateMeetingTypeAsync task = new UpdateMeetingTypeAsync();
        try {
            task.execute(meetingTypeId + "", name);
        }
        catch(Exception e) {
            Log.d("updateMeetingType", e.toString());
        }
    }

    static class UpdateMeetingTypeAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "updateMeetingType");
            params.put(TMT_ID, strings[0]);
            params.put(TMT_name, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("updateMeetingTypeAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * delete a meeting type from its id
     * @param id id of the meeting type we want to delete
     */
    public void deleteMeetingType(int id) {
        //TODO verifier cascade
        DeleteMeetingTypeAsync task = new DeleteMeetingTypeAsync();
        task.execute(id + "");
    }

    static class DeleteMeetingTypeAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "deleteMeetingType");
            params.put(TMT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteMeetingTypeAsync", e.toString());
            }
            return null;
        }
    }

    /**
     * Add event types to meeting type
     * @param eventType eventType we want to add
     * @param idMeetingType id of the meetingType
     */
     public void addEventTypeToMeetingType(int idMeetingType, EventType eventType) {
        AddEventTypeToMeetingTypeAsync task = new AddEventTypeToMeetingTypeAsync();
        try {
            task.execute(idMeetingType + "", eventType.getId() + "").get();
        }
        catch(Exception e) {
            Log.d("addEventTypeToTMT", e.toString());
        }
    }

    static class AddEventTypeToMeetingTypeAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "addEventTypeToMeetingType");
            params.put(TMT_ID, strings[0]);
            params.put(TEV_ID, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("addTEVtoTMTAsync", e.toString());
            }
            return null;
        }
    }

    /**
     * Get a meeting's type with its id
     * @param meetingTypeId id of the meeting's type
     * @return the meeting's type
     */
    public MeetingType getMeetingTypeById(int meetingTypeId) {
        GetMeetingTypeByIdAsync task = new GetMeetingTypeByIdAsync();
        MeetingType meetingType = null;
        try {
            meetingType = task.execute("" + meetingTypeId).get();
        }
        catch(Exception e) {
            Log.d("getMeetingTypeById", e.toString());
        }
        return meetingType;
    }

    static class GetMeetingTypeByIdAsync extends AsyncTask<String, Void, MeetingType> {
        protected MeetingType doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getMeetingTypeById");
            params.put(TMT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            MeetingType meetingType=null;
            int id;
            String name;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                id = json.getInt(TMT_ID);
                name = json.getString(TMT_name);
                meetingType = new MeetingType(id, name);
            }
            catch (Exception e) {
                Log.d("getMeetingTypeByIdAsync", e.toString());
            }
            return meetingType;
        }
    }


    /**
     * get MeetingType by its name
     * @param name name of the meeting type we want to get
     * @return meetingtype
     */
    public MeetingType getMeetingTypeByName(String name) {
        GetMeetingTypeByNameAsync task = new GetMeetingTypeByNameAsync();
        MeetingType meetingType = null;
        try {
            meetingType = task.execute("" + name).get();
        }
        catch(Exception e) {
            Log.d("getMeetingTypeByName", e.toString());
        }
        return meetingType;
    }

    static class GetMeetingTypeByNameAsync extends AsyncTask<String, Void, MeetingType> {
        protected MeetingType doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getMeetingTypeByName");
            params.put(TMT_name, strings[0]);
            JSONParser jsonParser = new JSONParser();
            MeetingType meetingType=null;
            int id;
            String name;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                id = json.getInt(TMT_ID);
                name = json.getString(TMT_name);
                meetingType = new MeetingType(id, name);
            }
            catch (Exception e) {
                Log.d("getMeetTypeByNameAsync", e.toString());
            }
            return meetingType;
        }
    }


    /**
     * Search all meeting's types
     * @return a list of meeting's types
     */
    public ArrayList<MeetingType> getMeetingTypes() {
        GetMeetingTypesAsync task = new GetMeetingTypesAsync();
        ArrayList<MeetingType> meetingTypesList = new ArrayList<>();
        try {
            meetingTypesList = task.execute().get();
        }
        catch(Exception e) {
            Log.d("getMeetingTypes", e.toString());
        }
        return meetingTypesList;
    }

    static class GetMeetingTypesAsync extends AsyncTask<Void, Void, ArrayList<MeetingType>> {
        protected ArrayList<MeetingType> doInBackground(Void ... v) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getMeetingTypes");
            JSONParser jsonParser = new JSONParser();
            ArrayList<MeetingType> meetingTypes = new ArrayList<>();
            int meetingType_id;
            String meetingType_name;
            MeetingType meetingType;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrProjects= json.getInt("a"+0);
                for(int i=1; i<nbrProjects; i++) {
                    Log.d("i=", i + "");
                    meetingType_id = json.getInt("a" + i + "_" + TMT_ID);
                    meetingType_name = json.getString("a" + i + "_" + TMT_name);
                    meetingType = new MeetingType(meetingType_id, meetingType_name);
                    meetingTypes.add(meetingType);
                }
            }
            catch (Exception e) {
                Log.d("getProjectsAsync", e.toString());
            }
            return meetingTypes;
        }
    }

    /**
     * Get all the meeting types in a String Array
     * @return a String Array of all the meeting types
     */
    public String[] getMeetingTypeArray() {
        ArrayList<MeetingType> meetingTypesList = getMeetingTypes();
        String[] meetingTypesNames = new String[meetingTypesList.size()];
        int i=0;
        for(MeetingType tmt : meetingTypesList) {
            meetingTypesNames[i] = tmt.getName();
            i++;
        }
        return meetingTypesNames;
    }

    /**
     * Get a meeting's type with its meeting associated
     * @param meetingId id of the meeting
     * @return the meetingType
     */
    public MeetingType getMeetingTypeByMeeting(int meetingId) {
        GetMeetingTypeByMeetingAsync task = new GetMeetingTypeByMeetingAsync();
        MeetingType meetingType = null;
        try {
            meetingType = task.execute("" + meetingId).get();
        }
        catch(Exception e) {
            Log.d("getMeetingTypeByMeeting", e.toString());
        }
        return meetingType;
    }

    static class GetMeetingTypeByMeetingAsync extends AsyncTask<String, Void, MeetingType> {
        protected MeetingType doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getMeetingTypeByMeeting");
            params.put(MTG_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            MeetingType meetingType=null;
            int id;
            String name;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                id = json.getInt(TMT_ID);
                name = json.getString(TMT_name);
                meetingType = new MeetingType(id, name);
            }
            catch (Exception e) {
                Log.d("getMeetTypByMeetAsync", e.toString());
            }
            return meetingType;
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*  MEETING METHOD */


    /**
     * Create a meeting in database
     * @param meeting Meeting we want to create
     * @return the row id of the newly meeting inserted
     */
    public long createMeeting(Meeting meeting) {
        CreateMeetingAsync task = new CreateMeetingAsync();
        long mtgId = 0;
        try {
            mtgId = task.execute(meeting.getName(), dateFormat.format(meeting.getDate()), dateFormat.format(meeting.getEndingDate()),
                    meeting.getTypeId()+"", meeting.getProjectId()+"").get();

        }
        catch(Exception e) {
            Log.d("createMeeting", e.toString());
        }
        return mtgId;
    }

    static class CreateMeetingAsync extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... strings) {
            Long idLong=0L;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "createMeeting");
            params.put(MTG_name, strings[0]);
            params.put(MTG_date, strings[1]);
            params.put(MTG_date_end, strings[2]);
            params.put(MTG_TMT_ID, strings[3]);
            params.put(MTG_PJT_ID, strings[4]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                idLong = json.getLong(MTG_ID);
            } catch (Exception e) {
                Log.d("createMeetingAsync", e.toString());
            }
            return idLong;
        }
    }


    //TODO updateMeeting

    /**
     * Delete a meeting
     * @param id id of the meeting
     * @throws SQLException
     */
    public void deleteMeeting(int id) throws SQLException {
        //TODO verifier cascade
        DeleteMeetingAsync task = new DeleteMeetingAsync();
        task.execute(id + "");
    }

    static class DeleteMeetingAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "deleteMeeting");
            params.put(MTG_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteMeetingAsync", e.toString());
            }
            return null;
        }
    }

    /**
     * Modify the ending date of a meeting
     */
    public void setMeetingEnd(Meeting meeting) throws SQLException {
        SetMeetingEndAsync task = new SetMeetingEndAsync();
        try {
            task.execute(meeting.getId()+ "", dateFormat.format(meeting.getEndingDate()));
        }
        catch(Exception e) {
            Log.d("updateMeetingType", e.toString());
        }
    }

    static class SetMeetingEndAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "setMeetingEnd");
            params.put(MTG_ID, strings[0]);
            params.put(MTG_date_end, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("setMeetingEndAsync", e.toString());
            }
            return null;
        }
    }

    /**
     * Get a meeting with its id
     * @param meetingId id of the meeting
     * @return the meeting
     */
    public Meeting getMeetingById(int meetingId) {
        GetMeetingByIdAsync task = new GetMeetingByIdAsync();
        Meeting meeting = null;
        try {
            meeting = task.execute("" + meetingId).get();
        }
        catch(Exception e) {
            Log.d("getMeetingById", e.toString());
        }
        return meeting;
    }

    static class GetMeetingByIdAsync extends AsyncTask<String, Void, Meeting> {
        protected Meeting doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getMeetingById");
            params.put(MTG_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            Meeting meeting=null;
            int id;
            int project_id;
            int meetingType_id;
            String name;
            String date;
            String date_ending;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                id = json.getInt(MTG_ID);
                project_id = json.getInt(MTG_PJT_ID);
                meetingType_id = json.getInt(MTG_TMT_ID);
                name = json.getString(MTG_name);
                date = json.getString(MTG_date);
                date_ending = json.getString(MTG_date_end);
                meeting = new Meeting(id, project_id, meetingType_id, name, dateFormat.parse(date), dateFormat.parse(date_ending));
            }
            catch (Exception e) {
                Log.d("getMeetingByIdAsync", e.toString());
            }
            return meeting;
        }
    }
    /**
     * Get a meeting with its name
     * @param name id of the meeting
     * @return the meeting
     */
    public Meeting getMeetingByName(String name) {
        GetMeetingByNameAsync task = new GetMeetingByNameAsync();
        Meeting meeting = null;
        try {
            meeting = task.execute(name).get();
        }
        catch(Exception e) {
            Log.d("getMeetingByName", e.toString());
        }
        return meeting;
    }

    static class GetMeetingByNameAsync extends AsyncTask<String, Void, Meeting> {
        protected Meeting doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getMeetingByName");
            params.put(MTG_name, strings[0]);
            JSONParser jsonParser = new JSONParser();
            Meeting meeting=null;
            int id;
            int project_id;
            int meetingType_id;
            String name;
            String date;
            String date_ending;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                id = json.getInt(MTG_ID);
                project_id = json.getInt(MTG_PJT_ID);
                meetingType_id = json.getInt(MTG_TMT_ID);
                name = json.getString(MTG_name);
                date = json.getString(MTG_date);
                date_ending = json.getString(MTG_date_end);
                meeting = new Meeting(id, project_id, meetingType_id, name, dateFormat.parse(date), dateFormat.parse(date_ending));
            }
            catch (Exception e) {
                Log.d("getMeetingByNameAsync", e.toString());
            }
            return meeting;
        }
    }
    /**
    /**
     * Get a meeting with its observation
     * @param observationId id of observation of the meeting
     * @return the meeting
     */
    public Meeting getMeetingByObservation(int observationId) {
        GetMeetingByObservationAsync task = new GetMeetingByObservationAsync();
        Meeting meeting = null;
        try {
            meeting = task.execute(observationId + "").get();
        }
        catch(Exception e) {
            Log.d("getMeetingByObservation", e.toString());
        }
        return meeting;
    }

    static class GetMeetingByObservationAsync extends AsyncTask<String, Void, Meeting> {
        protected Meeting doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getMeetingByObservation");
            params.put(OBS_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            Meeting meeting=null;
            int id;
            int project_id;
            int meetingType_id;
            String name;
            String date;
            String date_ending;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                id = json.getInt(MTG_ID);
                project_id = json.getInt(MTG_PJT_ID);
                meetingType_id = json.getInt(MTG_TMT_ID);
                name = json.getString(MTG_name);
                date = json.getString(MTG_date);
                date_ending = json.getString(MTG_date_end);
                meeting = new Meeting(id, project_id, meetingType_id, name, dateFormat.parse(date), dateFormat.parse(date_ending));
            }
            catch (Exception e) {
                Log.d("getMeetingByObsAsync", e.toString());
            }
            return meeting;
        }
    }


    //utiliser seulement dans getMeetingArray
    public ArrayList<Meeting> getMeetings() {
        GetMeetingsAsync task = new GetMeetingsAsync();
        ArrayList<Meeting> meetingsList = new ArrayList<>();
        try {
            meetingsList = task.execute().get();
        }
        catch(Exception e) {
            Log.d("getMeetings", e.toString());
        }
        return meetingsList;
    }

    static class GetMeetingsAsync extends AsyncTask<Void, Void, ArrayList<Meeting>> {
        protected ArrayList<Meeting> doInBackground(Void ... v) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getMeetings");
            JSONParser jsonParser = new JSONParser();
            ArrayList<Meeting> meetings = new ArrayList<>();
            int id;
            int project_id;
            int meetingType_id;
            String name;
            String date;
            String date_ending;
            Meeting meeting;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrMeetings= json.getInt("a"+0);
                for(int i=1; i<nbrMeetings; i++) {
                    id = json.getInt("a" + i + "_" + MTG_ID);
                    project_id = json.getInt("a" + i + "_" + MTG_PJT_ID);
                    meetingType_id = json.getInt("a" + i + "_" + MTG_TMT_ID);
                    name = json.getString("a" + i + "_" + MTG_name);
                    date = json.getString("a" + i + "_" + MTG_date);
                    date_ending = json.getString("a" + i + "_" + MTG_date_end);
                    meeting = new Meeting(id, project_id, meetingType_id, name, dateFormat.parse(date), dateFormat.parse(date_ending));

                    meetings.add(meeting);
                }
            }
            catch (Exception e) {
                Log.d("getProjectsAsync", e.toString());
            }
            return meetings;
        }
    }

    public String[] getAllMeetingNameArray() {
        ArrayList<Meeting> meetingsList = getMeetings();
        String[] meetingsNames = new String[meetingsList.size()];
        int i=0;
        for(Meeting mtg : meetingsList) {
            meetingsNames[i] = mtg.getName();
            i++;
        }
        return meetingsNames;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*  EVENT METHOD */

    /**
     * Create an event in database
     * @param event event we want to create
     * @param obsId id of the meeting
     * @param equipment equipment used
     */
    public void createEvent(Event event, int obsId, Equipment equipment){
        CreateEventAsync task = new CreateEventAsync();
        String equipmentName="";
        try {
            if (event.getStudent()!=null) {

                if (equipment!=null) {
                    equipmentName = equipment.getName();
                }
                String EVT_date = dateFormat.format(event.getDate());
                String EVT_OBS_ID = obsId + "";
                String EVT_comment = event.getComment();
                String EVT_TEV_ID = event.getType() + "";
                String EVT_STD_ID = event.getStudent().getId() + "";
                task.execute(EVT_OBS_ID, EVT_date, EVT_STD_ID, EVT_TEV_ID, EVT_comment, equipmentName);

            }
            else {
                if (equipment!=null) {
                    equipmentName = equipment.getName();
                }
                String EVT_date = dateFormat.format(event.getDate());
                String EVT_OBS_ID = obsId + "";
                String EVT_comment = event.getComment();
                String EVT_TEV_ID = event.getType() + "";
                task.execute(EVT_OBS_ID, EVT_date, null, EVT_TEV_ID, EVT_comment, equipmentName);
            }
        }
        catch(Exception e) {
            Log.d("createEvent", e.toString());
        }
    }

    static class CreateEventAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Long idLong=0L;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "createEvent");
            params.put(EVT_OBS_ID, strings[0]);
            params.put(EVT_date, strings[1]);
            params.put(EVT_STD_ID, strings[2]);
            params.put(EVT_TEV_ID, strings[3]);
            params.put(EVT_comment, strings[4]);
            params.put(EVT_equipmentName, strings[5]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("createEventStudentAsync", e.toString());
            }
            return null;
        }
    }

    /**
     * Search the events linked to a meeting
     * @param obsId the id of th meeting
     * @return a list of the events
     */
    public ArrayList<Event> getEventsFromObservation(int obsId) {
        GetEventsFromObservationAsync task = new GetEventsFromObservationAsync();
        ArrayList<Event> eventsList = new ArrayList<>();
        try {
            eventsList = task.execute(obsId+"").get();
        }
        catch(Exception e) {
            Log.d("getEventsFromObs", e.toString());
        }
        return eventsList;
    }

    static class GetEventsFromObservationAsync extends AsyncTask<String, Void, ArrayList<Event>> {
        protected ArrayList<Event> doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getEventsFromObservation");
            JSONParser jsonParser = new JSONParser();
            ArrayList<Event> events = new ArrayList<>();
            int event_id;
            int eventType_id;
            String date;
            String comment;
            String equipmentName;
            Event event;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrEvents= json.getInt("a"+0);
                for(int i=1; i<nbrEvents; i++) {
                    event_id = json.getInt("a" + i + "_" + EVT_ID);
                    eventType_id= json.getInt("a" + i + "_" + EVT_TEV_ID);
                    date = json.getString("a" + i + "_" + EVT_date);
                    comment = json.getString("a" + i + "_" + EVT_comment);
                    equipmentName = json.getString("a" + i + "_" + EVT_equipmentName);
                    event = new Event(event_id, eventType_id, dateFormat.parse(date), comment, getStudentFromEvent(event_id), equipmentName);
                    events.add(event);
                }
            }
            catch (Exception e) {
                Log.d("getEventsFromObsAsync", e.toString());
            }
            return events;
        }
    }

    /**
     * Search the events linked to a meeting
     * @param obsId the id of th meeting
     * @return a list of the events
     */
    public ArrayList<Event> getEventsFromObservationStudentEventTypeLists(int obsId, ArrayList<Student> students,
                                                                          ArrayList<EventType> eventTypes) {
        GetEventFromObsStudEventTypeAsync task = new GetEventFromObsStudEventTypeAsync();
        ArrayList<Event> eventsList = new ArrayList<>();
        Event event;
        try {
            for(Student student: students) {
                for(EventType eventType : eventTypes) {
                    event = task.execute(obsId+"", student.getId()+"", eventType.getId()+"").get();
                    eventsList.add(event);
                }
            }
        }
        catch(Exception e) {
            Log.d("getEventFrmObsStdEvtTy", e.toString());
        }
        return eventsList;
    }

    static class GetEventFromObsStudEventTypeAsync extends AsyncTask<String, Void, Event> {
        protected Event doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getEventFromObsStudEventType");
            params.put(OBS_ID, strings[0]);
            params.put(STD_ID, strings[1]);
            params.put(TEV_ID, strings[2]);
            JSONParser jsonParser = new JSONParser();
            int event_id;
            int eventType_id;
            String date;
            String comment;
            String equipmentName;
            Event event=null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                event_id = json.getInt(EVT_ID);
                eventType_id= json.getInt(EVT_TEV_ID);
                date = json.getString(EVT_date);
                comment = json.getString(EVT_comment);
                equipmentName = json.getString(EVT_equipmentName);
                event = new Event(event_id, eventType_id, dateFormat.parse(date), comment, getStudentFromEvent(event_id), equipmentName);
            }
            catch (Exception e) {
                Log.d("EventFrmObsStudTevAsync", e.toString());
            }
            return event;
        }
    }

    public String getEventTypeNameFromEvent(int event_id) {
        GetEventTypeNameFromEventAsync task = new GetEventTypeNameFromEventAsync();
        String name=null;
        try {
            name = task.execute(event_id+"").get();
        }
        catch(Exception e) {
            Log.d("getEventTypeNamFrmEvent", e.toString());
        }
        return name;
    }

    static class GetEventTypeNameFromEventAsync extends AsyncTask<String, Void, String> {
        protected String doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getEventTypeNameFromEvent");
            params.put(EVT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            String name=null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                name = json.getString(TEV_name);
            }
            catch (Exception e) {
                Log.d("EventTypNamFrmEventAsy", e.toString());
            }
            return name;
        }
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*  EVENTTYPE METHOD */

    /**
     * Create en EventType in the database
     * @param eventType eventType we want to add to the database
     * @return id of the event Type created
     */
    public long createEventType(EventType eventType) {
        CreateEventTypeAsync task = new CreateEventTypeAsync();
        long id = 0;
        try {
            id = task.execute(eventType.getName(), eventType.getEquipmentName()).get();

        }
        catch(Exception e) {
            Log.d("createEventType", e.toString());
        }
        return id;
    }

    static class CreateEventTypeAsync extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... strings) {
            Long idLong=0L;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "createEventType");
            params.put(TEV_name, strings[0]);
            params.put(TEV_equipmentName, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                idLong = json.getLong(TEV_ID);
            } catch (Exception e) {
                Log.d("createEventTypeAsync", e.toString());
            }
            return idLong;
        }
    }
    /**
     * change the name of the eventtype
     * @param eventTypeId id of the event type we want to rename
     * @param eventTypeName new name
     * @throws SQLException
     */
    public void updateEventType(int eventTypeId, String eventTypeName) throws SQLException {
        UpdateEventTypeAsync task = new UpdateEventTypeAsync();
        try {
            task.execute(eventTypeId + "", eventTypeName);
        }
        catch(Exception e) {
            Log.d("updateEventType", e.toString());
        }
    }

    static class UpdateEventTypeAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "updateEventType");
            params.put(TEV_ID, strings[0]);
            params.put(TEV_name, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("updateEventTypeAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Delete an eventType
     * @param id id of the eventType to delete
     * @throws SQLException
     */
    public void deleteEventType(int id) throws SQLException {
        //TODO verifier cascade
        DeleteEventTypeAsync task = new DeleteEventTypeAsync();
        task.execute(id + "");
    }

    static class DeleteEventTypeAsync extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "deleteEventType");
            params.put(TEV_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteEventTypeAsync", e.toString());
            }
            return null;
        }
    }


    /**
     * Get an event's type with its id
     * @param id id of the event's type
     * @return the eventType
     */
    public EventType getEventTypeById(int id) {
        GetEventTypeByIdAsync task = new GetEventTypeByIdAsync();
        EventType eventType = null;
        try {
            eventType = task.execute(id + "").get();
        } catch (Exception e) {
            Log.d("getEventTypeByID", e.toString());
        }
        return eventType;
    }

    static class GetEventTypeByIdAsync extends AsyncTask<String, Void, EventType> {
        protected EventType doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getEventTypeById");
            params.put(TEV_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            EventType eventType = null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                eventType = new EventType(json.getInt(TEV_ID), json.getString(TEV_name), json.getString(TEV_equipmentName));
            } catch (Exception e) {
                Log.d("getEventTypeByIdAsync", e.toString());
            }
            return eventType;
        }
    }

    /**
     * Get Event Type by its name
     * @param name name of the event type we want
     * @return event type
     * @throws SQLException
     */
    public EventType getEventTypeByName(String name) {
        GetEventTypeByNameAsync task = new GetEventTypeByNameAsync();
        EventType eventType = null;
        try {
            eventType = task.execute(name).get();
        } catch (Exception e) {
            Log.d("getEventTypeByName", e.toString());
        }
        return eventType;
    }

    static class GetEventTypeByNameAsync extends AsyncTask<String, Void, EventType> {
        protected EventType doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getEventTypeByName");
            params.put(TEV_name, strings[0]);
            JSONParser jsonParser = new JSONParser();
            EventType eventType = null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                eventType = new EventType(json.getInt(TEV_ID), json.getString(TEV_name), json.getString(TEV_equipmentName));
            } catch (Exception e) {
                Log.d("getEventTypeByNameAsync", e.toString());
            }
            return eventType;
        }
    }



    public ArrayList<EventType> getEventTypeListByEquipment(Equipment equipment) {
        GetEventTypeListByEquipmentAsync task = new GetEventTypeListByEquipmentAsync();
        ArrayList<EventType> eventTypesList = new ArrayList<>();
        try {
            eventTypesList = task.execute(equipment.getName()).get();
        }
        catch(Exception e) {
            Log.d("getEventTypeByEquipment", e.toString());
        }
        return eventTypesList;
    }

    static class GetEventTypeListByEquipmentAsync extends AsyncTask<String, Void, ArrayList<EventType>> {
        protected ArrayList<EventType> doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getEventTypeListByEquipment");
            params.put(TEV_equipmentName, strings[0]);
            JSONParser jsonParser = new JSONParser();
            ArrayList<EventType> eventTypes = new ArrayList<>();
            int eventType_id;
            String name;
            String equipmentName;
            EventType eventType;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrEventTypes= json.getInt("a"+0);
                for(int i=1; i<nbrEventTypes; i++) {
                    eventType_id= json.getInt("a" + i + "_" + TEV_ID);
                    name = json.getString("a" + i + "_" + TEV_name);
                    equipmentName = json.getString("a" + i + "_" + TEV_equipmentName);
                    eventType = new EventType(eventType_id, name, equipmentName);
                    eventTypes.add(eventType);
                }
            }
            catch (Exception e) {
                Log.d("GetEventTypeByEquipAsy", e.toString());
            }
            return eventTypes;
        }
    }

    public String[] getEventTypeArrayByEquipment(Equipment equipment) {
        ArrayList<EventType> eventTypeList = getEventTypeListByEquipment(equipment);
        String[] eventTypeNames = new String[eventTypeList.size()];
        int i=0;
        for(EventType eventType: eventTypeList) {
            eventTypeNames[i] = eventType.getName();
            i++;
        }
        return eventTypeNames;
    }


    /**
     * Search all the event types of the meeting
     * @param meetingTypeId id of the meeting
     * @return an ArrayList of all the events type
     */
    public ArrayList<EventType> getEventTypeFromMeetingTypeList(int meetingTypeId) {
        GetEventTypeFromMeetingTypeListAsync task = new GetEventTypeFromMeetingTypeListAsync();
        ArrayList<EventType> eventTypesList = new ArrayList<>();
        try {
            eventTypesList = task.execute(meetingTypeId + "").get();
        }
        catch(Exception e) {
            Log.d("getEventTypeFromTMT", e.toString());
        }
        return eventTypesList;
    }

    static class GetEventTypeFromMeetingTypeListAsync extends AsyncTask<String, Void, ArrayList<EventType>> {
        protected ArrayList<EventType> doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getEventTypeFromMeetingTypeList");
            params.put(TMT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            ArrayList<EventType> eventTypes = new ArrayList<>();
            int eventType_id;
            String name;
            String equipmentName;
            EventType eventType;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrEventTypes= json.getInt("a"+0);
                for(int i=1; i<nbrEventTypes; i++) {
                    eventType_id= json.getInt("a" + i + "_" + TEV_ID);
                    name = json.getString("a" + i + "_" + TEV_name);
                    equipmentName = json.getString("a" + i + "_" + TEV_equipmentName);
                    eventType = new EventType(eventType_id, name, equipmentName);
                    eventTypes.add(eventType);
                }
            }
            catch (Exception e) {
                Log.d("getEventTypeFromTMTAsyn", e.toString());
            }
            return eventTypes;
        }
    }



    public ArrayList<EventType> getEventTypesWithoutEquipmentByMeetingType(int meetingTypeID) {
        GetEventTypesWithoutEquipmentByMeetingTypeAsync task = new GetEventTypesWithoutEquipmentByMeetingTypeAsync();
        ArrayList<EventType> eventTypes = null;
        try {
            eventTypes = task.execute(meetingTypeID + "").get();
        }
        catch(Exception e) {
            Log.d("getEventTypesWithout", e.toString());
        }
        return eventTypes;
    }

    static class GetEventTypesWithoutEquipmentByMeetingTypeAsync extends AsyncTask<String, Void, ArrayList<EventType>> {
        protected ArrayList<EventType> doInBackground(String ... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getEventTypesWithoutEquipmentByMeetingType");
            params.put(TMT_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            ArrayList<EventType> eventTypes = new ArrayList<>();
            int eventType_id;
            String name;
            String equipmentName;
            EventType eventType;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrEventTypes= json.getInt("a"+0);
                for(int i=1; i<nbrEventTypes; i++) {
                    eventType_id= json.getInt("a" + i + "_" + TEV_ID);
                    name = json.getString("a" + i + "_" + TEV_name);
                    equipmentName = json.getString("a" + i + "_" + TEV_equipmentName);
                    eventType = new EventType(eventType_id, name, equipmentName);
                    eventTypes.add(eventType);
                }
            }
            catch (Exception e) {
                Log.d("getEventTypesWthoAsyn", e.toString());
            }
            return eventTypes;
        }
    }

    /**
     * Return an array of eventType's id of a meetingType
     * @param meetingTypeID id of the meetingType
     * @return an array of the id
     */
    public int[] getEventTypesIdWithoutEquipmentByMeetingType(int meetingTypeID) {
        ArrayList<EventType> eventTypes = getEventTypesWithoutEquipmentByMeetingType(meetingTypeID);
        int[] ids = new int[eventTypes.size()];
        int i=0;
        for(EventType e : eventTypes) {
            ids[i]=e.getId();
            i++;
        }
        return ids;
    }

    /**
     * Get all the name of the event types of the meeting
     * @param meetingTypeId id of the meeting
     * @return a String Array of all the event types
     */
    public String[] getEventTypeWithoutEquipmentFromMeetingTypeArray(int meetingTypeId) {
        ArrayList<EventType> eventTypeList = getEventTypesWithoutEquipmentByMeetingType(meetingTypeId);
        String[] eventTypeNames = new String[eventTypeList.size()];
        int i=0;
        for(EventType eventType: eventTypeList) {
            eventTypeNames[i] = eventType.getName();
            i++;
        }
        return eventTypeNames;
    }


    /**
     *  Get All the event type in an arrayList
     * @return an arraylist of all the event type
     */
    public ArrayList<EventType> getAllEventTypesList() {
        GetAllEventTypesListAsync task = new GetAllEventTypesListAsync();
        ArrayList<EventType> eventTypesList = new ArrayList<>();
        try {
            eventTypesList = task.execute().get();
        }
        catch(Exception e) {
            Log.d("getAllEventTypesList", e.toString());
        }
        return eventTypesList;
    }

    static class GetAllEventTypesListAsync extends AsyncTask<Void, Void, ArrayList<EventType>> {
        protected ArrayList<EventType> doInBackground(Void ... v) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getAllEventTypes");
            JSONParser jsonParser = new JSONParser();
            ArrayList<EventType> eventTypes = new ArrayList<>();
            int eventType_id;
            String name;
            String equipmentName;
            EventType eventType;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                int nbrEventTypes= json.getInt("a"+0);
                for(int i=1; i<nbrEventTypes; i++) {
                    eventType_id= json.getInt("a" + i + "_" + TEV_ID);
                    name = json.getString("a" + i + "_" + TEV_name);
                    equipmentName = json.getString("a" + i + "_" + TEV_equipmentName);
                    eventType = new EventType(eventType_id, name, equipmentName);
                    eventTypes.add(eventType);
                }
            }
            catch (Exception e) {
                Log.d("getAllEventTypesAsync", e.toString());
            }
            return eventTypes;
        }
    }

    /**
     * Search all the event types
     * @return a string array
     */
    public String[] getAllEventTypesArray() {
        ArrayList<EventType> eventTypeList = getAllEventTypesList();
        String[] eventTypeNames = new String[eventTypeList.size()];
        int i=0;
        for(EventType eventType: eventTypeList) {
            eventTypeNames[i] = eventType.getName();
            i++;
        }
        return eventTypeNames;
    }



/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*  OBSERVATION METHOD */

    /**
     * Create an observation in database
     * @param observation observation  we want to create
     * @return the row id of the newly project inserted
     */
    public long createObservation(Observation observation) {
        CreateObservationAsync task = new CreateObservationAsync();
        long obsId = 0;
        try {
            obsId = task.execute(observation.getIdGroup() + "", observation.getIdMeeting()+"").get();

        }
        catch(Exception e) {
            Log.d("createObservation", e.toString());
        }
        return obsId;
    }

    static class CreateObservationAsync extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... strings) {
            Long idLong=0L;
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "createObservation");
            params.put(OBS_GRP_ID, strings[0]);
            params.put(OBS_MTG_ID, strings[1]);
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                idLong = json.getLong(OBS_ID);
            } catch (Exception e) {
                Log.d("createObservationAsync", e.toString());
            }
            return idLong;
        }
    }

    /**
     * Search the observation
     * @param idMeeting id meeting of the observation
     * @param idGroup id group of the observation
     * @return the observation
     * @throws SQLException
     */
    public Observation getObservationByMeetingAndGroupId(int idMeeting, int idGroup) {
        GetObservationByMeetingAndGroupAsync task = new GetObservationByMeetingAndGroupAsync();
        Observation obs = null;
        try {
            obs = task.execute(idMeeting + "", idGroup + "").get();
        } catch (Exception e) {
            Log.d("getObserv", e.toString());
        }
        return obs;
    }

    static class GetObservationByMeetingAndGroupAsync extends AsyncTask<String, Void, Observation> {
        protected Observation doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getObservation");
            params.put(MTG_ID, strings[0]);
            params.put(GRP_ID, strings[1]);
            JSONParser jsonParser = new JSONParser();
            Observation obs = null;
            int id;
            int groupId;
            int meetingId;
            try {
                JSONObject json = jsonParser.makeHttpRequest("10.29.236.205/easyobs/database.php", "POST", params);
                id = json.getInt(OBS_ID);
                groupId = json.getInt(OBS_GRP_ID);
                meetingId = json.getInt(OBS_MTG_ID);
                obs = new Observation(id, groupId, meetingId);
            } catch (Exception e) {
                Log.d("getObsAsync", e.toString());
            }
            return obs;
        }
    }


}