package com.easynote.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.easynote.objects.Equipment;
import com.easynote.objects.Event;
import com.easynote.objects.EventType;
import com.easynote.objects.Group;
import com.easynote.objects.Meeting;
import com.easynote.objects.MeetingType;
import com.easynote.objects.Observation;
import com.easynote.objects.Project;
import com.easynote.objects.Student;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DatabaseSQLite {


	private static final String DATABASE_NAME = "DatabaseEasyNotes";
	private static final int DATABASE_VERSION = 37;

    //to see log comments more easily in the logcat
    private static String mark = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";

    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE); //avant :ss

    // Project Table
    private static final String ProjectTable = "Project";
    public static final String PJT_ID = "PJT_ID";
    public static final String PJT_name = "PJT_name";
    public static final String CreateProjectTable = "CREATE TABLE "
            + ProjectTable + " ("
            + PJT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PJT_name  + " TEXT NOT NULL "
            + ");" ;

    // Group Table
    private static final String GroupTable = "Groupe";
    public static final String GRP_ID = "GRP_ID";
    public static final String GRP_num = "GRP_num";
    public static final String GRP_PJT_ID = "GRP_PJT_ID";
    public static final String CreateGroupTable = "CREATE TABLE "
            + GroupTable + " ("
            + GRP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GRP_num + " INTEGER NOT NULL, "
            + GRP_PJT_ID + " INTEGER, "
            + "FOREIGN KEY(" + GRP_PJT_ID + ") REFERENCES " + ProjectTable + "(" + PJT_ID + ")"
            + ");";

    // Student Table
    private static final String StudentTable = "Student";
    public static final String STD_ID = "STD_ID";
	public static final String STD_lastname = "STD_lastname";
	public static final String STD_firstname = "STD_firstname";
	public static final String STD_login = "STD_login";
    public static final String CreateStudentTable = "CREATE TABLE "
            + StudentTable + " ("
            + STD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STD_lastname + " TEXT NOT NULL, "
            + STD_firstname + " TEXT NOT NULL, "
            + STD_login + " TEXT NOT NULL UNIQUE"
            + ");";

    // JunctionGroupStudent Table
    private static final String JunctionGroupStudentTable = "JunctionGroupStudent";
    public static final String JCT_GRPSTD_ID = "JCT_GRPSTD_ID";
    public static final String JCT_GRPSTD_STD_ID = "JCT_GRPSTD_STD_ID";
    public static final String JCT_GRPSTD_GRP_ID = "JCT_GRPSTD_GRP_ID";
    public static final String CreateJunctionGroupStudentTable = "CREATE TABLE "
            + JunctionGroupStudentTable + " ("
            + JCT_GRPSTD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + JCT_GRPSTD_STD_ID  + " INTEGER, "
            + JCT_GRPSTD_GRP_ID + " INTEGER, "
            + "FOREIGN KEY(" + JCT_GRPSTD_STD_ID + ") REFERENCES " + StudentTable + "(" + STD_ID + ") "
            + "FOREIGN KEY(" + JCT_GRPSTD_GRP_ID + ") REFERENCES " + GroupTable + "(" + GRP_ID + ")"
            + ");";




    // MeetingType Table
    private static final String MeetingTypeTable = "MeetingType";
    public static final String TMT_ID = "TMT_ID";
    public static final String TMT_name = "TMT_name";
    public static final String CreateMeetingTypeTable = "CREATE TABLE "
            + MeetingTypeTable + " ("
            + TMT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TMT_name + " TEXT NOT NULL "
            + ");";



    // Meeting Table
    private static final String MeetingTable = "Meeting";
    public static final String MTG_ID = "MTG_ID";
    public static final String MTG_name = "MTG_name";
    public static final String MTG_date = "MTG_date";
    public static final String MTG_date_end = "MTG_date_end";
    public static final String MTG_PJT_ID = "MTG_PJT_ID";
    public static final String MTG_TMT_ID = "MTG_TMT_ID";
    public static final String CreateMeetingTable = "CREATE TABLE "
            + MeetingTable + " ("
            + MTG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MTG_name + " TEXT NOT NULL, "
            + MTG_date + " TEXT NOT NULL, "
            + MTG_date_end + " TEXT NOT NULL, "
            + MTG_PJT_ID  + " INTEGER, "
            + MTG_TMT_ID + " INTEGER, "
            + "FOREIGN KEY(" + MTG_PJT_ID + ") REFERENCES " + ProjectTable + "(" + PJT_ID + "), "
            + "FOREIGN KEY(" + MTG_TMT_ID + ") REFERENCES " + MeetingTypeTable + "(" + MTG_ID + ")"
            + ");";

    // EventType Table
    private static final String EventTypeTable = "EventType";
    public static final String TEV_ID = "TEV_ID";
    public static final String TEV_name = "TEV_name";
    public static final String TEV_equipmentName = "TEV_equipmentName";
    public static final String CreateEventTypeTable = "CREATE TABLE "
            + EventTypeTable + " ("
            + TEV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEV_name + " TEXT NOT NULL, "
            + TEV_equipmentName + " TEXT"
            + ");";

    //JunctionMeetingTypeEventType Table
    private static final String JunctionMeetingTypeEventTypeTable = "JunctionMeetingTypeEventType";
    public static final String JCT_TMTTEV_ID = "JCT_TMTTEV_ID";
    public static final String JCT_TMTTEV_TMT_ID = "JCT_TMTTEV_TMT_ID";
    public static final String JCT_TMTTEV_TEV_ID = "JCT_TMTTEV_TEV_ID";
    public static final String CreateJunctionMeetingTypeEventTypeTable = "CREATE TABLE "
            + JunctionMeetingTypeEventTypeTable + " ("
            + JCT_TMTTEV_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + JCT_TMTTEV_TMT_ID  + " INTEGER, "
            + JCT_TMTTEV_TEV_ID + " INTEGER, "
            + "FOREIGN KEY(" + JCT_TMTTEV_TMT_ID + ") REFERENCES " + MeetingTypeTable + "(" + TMT_ID + ") "
            + "FOREIGN KEY(" + JCT_TMTTEV_TEV_ID + ") REFERENCES " + EventTypeTable + "(" + TEV_ID + ")"
            + ");";


    // ObservationTable = JunctionMeetingGroupTable
    private static final String ObservationTable = "Observation";
    public static final String OBS_ID = "OBS_ID";
    public static final String OBS_GRP_ID = "OBS_GRP_ID";
    public static final String OBS_MTG_ID = "OBS_MTG_ID";
    public static final String CreateObservationTable =
            "CREATE TABLE " + ObservationTable + " ("
            + OBS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + OBS_GRP_ID + " INTEGER, "
            + OBS_MTG_ID + " INTEGER, "
            + "FOREIGN KEY(" + OBS_GRP_ID + ") REFERENCES " + GroupTable + "(" + GRP_ID + ") "
            + "FOREIGN KEY(" + OBS_MTG_ID + ") REFERENCES " + MeetingTable + "(" + MTG_ID + ")"
            + ");";

    // Event Table
    private static final String EventTable = "Event";
    public static final String EVT_ID = "EVT_ID";
    public static final String EVT_TEV_ID = "EVT_TEV_ID";
    public static final String EVT_date = "EVT_date";
    public static final String EVT_comment = "EVT_comment";
    public static final String EVT_OBS_ID = "EVT_OBS_ID";
    public static final String EVT_equipmentName = "EVT_equipmentName";
    public static final String EVT_STD_ID = "EVT_STD_ID";
    public static final String CreateEventTable = "CREATE TABLE "
            + EventTable + " ("
            + EVT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EVT_date + " TEXT NOT NULL, "
            + EVT_TEV_ID + " INTEGER, "
            + EVT_OBS_ID + " INTEGER, "
            + EVT_comment + " TEXT, "
            + EVT_equipmentName + " TEXT, "
            + EVT_STD_ID + " INTEGER, "
            + "FOREIGN KEY(" + EVT_TEV_ID + ") REFERENCES " + EventTypeTable + "(" + TEV_ID + "), "
            + "FOREIGN KEY(" + EVT_OBS_ID + ") REFERENCES " + ObservationTable+ "(" + OBS_ID + ")"
            + "FOREIGN KEY(" + EVT_STD_ID + ") REFERENCES " + StudentTable + "(" + STD_ID + ")"
            + ");";



    private DbHelper ourHelper;
	private static Context ourContext; // change ?
	private SQLiteDatabase ourDatabase;




    private static class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
            db.execSQL(CreateProjectTable);
            db.execSQL(CreateGroupTable);
            db.execSQL(CreateStudentTable);
            db.execSQL(CreateJunctionGroupStudentTable);
            db.execSQL(CreateEventTypeTable);
            db.execSQL(CreateMeetingTable);
            db.execSQL(CreateMeetingTypeTable);
            db.execSQL(CreateEventTable);
            db.execSQL(CreateJunctionMeetingTypeEventTypeTable);
            db.execSQL(CreateObservationTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS '" + StudentTable + "'");
			db.execSQL("DROP TABLE IF EXISTS '" + GroupTable + "'");
			db.execSQL("DROP TABLE IF EXISTS '" + JunctionGroupStudentTable + "'");
			db.execSQL("DROP TABLE IF EXISTS '" + ProjectTable + "'");
			db.execSQL("DROP TABLE IF EXISTS '" + EventTypeTable + "'");
			db.execSQL("DROP TABLE IF EXISTS '" + MeetingTable + "'");
			db.execSQL("DROP TABLE IF EXISTS '" + MeetingTypeTable + "'");
            db.execSQL("DROP TABLE IF EXISTS '" + CreateJunctionMeetingTypeEventTypeTable + "'");
            db.execSQL("DROP TABLE IF EXISTS '" + CreateObservationTable + "'");
			onCreate(db);
		}

	}

	public DatabaseSQLite(Context c) {
		ourContext = c;
	}

	public DatabaseSQLite open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

    /**
     * Delete all the tables
     */
    public void delete() {
        ourDatabase.delete(StudentTable, null, null);
        ourDatabase.delete(GroupTable, null, null);
        ourDatabase.delete(JunctionGroupStudentTable, null, null);
        ourDatabase.delete(ProjectTable, null, null);
        ourDatabase.delete(EventTypeTable, null, null);
        ourDatabase.delete(MeetingTable, null, null);
        ourDatabase.delete(MeetingTypeTable, null, null);
        ourDatabase.delete(JunctionMeetingTypeEventTypeTable, null, null);
        ourDatabase.delete(ObservationTable, null, null);
    }

    /**
     * Insert data in the database
     * @param insertDatabase data we want to insert in the database
     * @throws android.database.sqlite.SQLiteException
     */
    public void insert(String insertDatabase) throws android.database.sqlite.SQLiteException {
        ourDatabase.execSQL(insertDatabase);
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
	public long createStudentOld(Student student) {
        Student databaseStudent = getStudentByLogin(student.getLogin());
        if (databaseStudent == null) {
            ContentValues cv = new ContentValues();
            cv.put(STD_lastname, student.getLastName());
            cv.put(STD_firstname, student.getFirstName());
            cv.put(STD_login, student.getLogin());
            return ourDatabase.insert(StudentTable, null, cv);
        }
        return -1;
	}


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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void updateStudentOld(int studentId, String studentLastName,
                              String studentFirstName, String studentLogin) throws SQLException {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(STD_lastname, studentLastName);
        cvUpdate.put(STD_firstname, studentFirstName);
        cvUpdate.put(STD_login, studentLogin);
        ourDatabase.update(StudentTable, cvUpdate, STD_ID + "=" + studentId, null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void deleteStudentOld(int id) throws SQLException {
        ourDatabase.delete(StudentTable, STD_ID + "=" + id, null);
        ourDatabase.delete(JunctionGroupStudentTable, JCT_GRPSTD_STD_ID + "=" + id, null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void removeStudentFromAllGroupsOld(int studentID) throws SQLException {
        ourDatabase.delete(JunctionGroupStudentTable, JCT_GRPSTD_STD_ID + "=" + studentID,
                null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("removeStudentAllGrpsAsy", e.toString());
            }
            return null;
        }
    }

    /**
     * Check if a login already exists
     * @param loginToTest the login to test
     * @return true if the login already exists, false if not
     */
    public boolean loginExistOld(String loginToTest) {
        Boolean loginExist = false;

        String[] columns = new String[] { STD_ID, STD_lastname, STD_firstname,
                STD_login };
        Cursor c = ourDatabase.query(StudentTable, columns, null, null, null,
                null, null);

        int iLogin = c.getColumnIndex(STD_login);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if (loginToTest.equals(c.getString(iLogin)))
                loginExist = true;
        }

        c.close();
        return loginExist;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
     * @param login login of the student
     */
    public void addStudentToGroupOld(int idLastGroup,
                                  String login) {
        String[] columns = new String[] { STD_ID, STD_lastname, STD_firstname,
                STD_login };
        Cursor c = ourDatabase.query(StudentTable, columns, STD_login + "='"
                + login + "'", null, null, null, null);

        c.moveToFirst();
        int personID = c.getInt(0);
        c.close();

        Boolean lineDoesntExist = true;

        String[] columns2 = new String[] { JCT_GRPSTD_ID, JCT_GRPSTD_STD_ID, JCT_GRPSTD_GRP_ID };
        Cursor c2 = ourDatabase.query(JunctionGroupStudentTable, columns2, null,
                null, null, null, null);

        int iStdId = c2.getColumnIndex(JCT_GRPSTD_STD_ID);
        int iGrpId = c2.getColumnIndex(JCT_GRPSTD_GRP_ID);

        for (c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext()) {
            if (c2.getInt(iStdId) == personID
                    && c2.getInt(iGrpId) == idLastGroup)
                lineDoesntExist = false;
        }
        c2.close();
        ContentValues cv = new ContentValues();

        if (lineDoesntExist) {
            cv.put(JCT_GRPSTD_STD_ID, personID);
            cv.put(JCT_GRPSTD_GRP_ID, idLastGroup);
            ourDatabase.insert(JunctionGroupStudentTable, null, cv);

        }
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public Student getStudentByIdOld(int idLastButton) throws SQLException {

        String[] columns = new String[] { STD_ID, STD_lastname, STD_firstname, STD_login };
        Cursor c = ourDatabase.query(StudentTable, columns, STD_ID + '='
                + idLastButton, null, null, null, null);

        c.moveToFirst();
        Student student = new Student(c.getInt(0), c.getString(2), c.getString(1), c.getString(3));
        c.close();
        return student;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public Student getStudentByLoginOld(String login) throws SQLException {
        //TODO CAS NULL
        String MY_QUERY = "SELECT * FROM "+ StudentTable
                +" WHERE "+ STD_login +" = " + " ? ";

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{login});

        c.moveToFirst();
        Student student=null;
        if (c.getCount()!=0) {
            int iId = c.getColumnIndex(STD_ID);
            int iFirstName = c.getColumnIndex(STD_firstname);
            int iLastName = c.getColumnIndex(STD_lastname);
            int iLogin = c.getColumnIndex(STD_login);
            student = new Student(c.getInt(iId), c.getString(iFirstName), c.getString(iLastName), c.getString(iLogin));
        }
        c.close();
        return student;

      /*  String[] columns = new String[] { STD_ID, STD_lastname, STD_firstname, STD_login};
        Cursor c = ourDatabase.query(StudentTable, columns, STD_login + "= \"" + login + "\"", null, null, null, null);

        c.moveToFirst();
        Student student=null;
        if (c.getCount()!=0) {
            student = new Student(c.getInt(0), c.getString(2), c.getString(1), c.getString(3));
        }
        c.close();
        return student; */
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public ArrayList<Student> getStudentsFromGroupOld(int groupID) {
        String MY_QUERY = "SELECT * FROM "+ StudentTable
                +" INNER JOIN "+ JunctionGroupStudentTable
                +" ON "+ StudentTable +"."+ STD_ID +"="+ JunctionGroupStudentTable +"."+ JCT_GRPSTD_STD_ID
                +" WHERE "+ JunctionGroupStudentTable +"."+ JCT_GRPSTD_GRP_ID +"= ? "
                + " ORDER BY "+ StudentTable +"."+ STD_lastname +", "+ StudentTable +"."+ STD_firstname +", "+ StudentTable +"."+ STD_login +" ASC ";

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(groupID)});

        c.moveToFirst();
        int iId = c.getColumnIndex(STD_ID);
        int iFirstName = c.getColumnIndex(STD_firstname);
        int iLastName = c.getColumnIndex(STD_lastname);
        int iLogin = c.getColumnIndex(STD_login);

        ArrayList<Student> students = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            Student student = new Student(c.getInt(iId), c.getString(iFirstName), c.getString(iLastName), c.getString(iLogin));
            students.add(student);
        }
        c.close();
        return students;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public ArrayList<Student> getStudentsFromEventOld(int eventId) {
        String MY_QUERY = "SELECT * FROM " + StudentTable
                + " INNER JOIN " + EventTable
                + " ON " + StudentTable  + "." + STD_ID + "=" + EventTable + "." + EVT_STD_ID
                + " WHERE " + EVT_ID + "=" + " ? "
                + " ORDER BY " + StudentTable + ". "+ STD_lastname + ", "+ StudentTable + "."+ STD_firstname
                + ", " + StudentTable + "."+ STD_login + " ASC ";


        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(eventId)});

        c.moveToFirst();
        int iId = c.getColumnIndex(STD_ID);
        int iFirstName = c.getColumnIndex(STD_firstname);
        int iLastName = c.getColumnIndex(STD_lastname);
        int iLogin = c.getColumnIndex(STD_login);

        ArrayList<Student> students = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            Student student = new Student(c.getInt(iId), c.getString(iFirstName), c.getString(iLastName), c.getString(iLogin));
            students.add(student);
        }
        c.close();
        return students;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public String[] getAllStudentsOld() {
        String[] columns = new String[] { STD_ID, STD_lastname, STD_firstname,
                STD_login };
        Cursor c = ourDatabase.query(StudentTable, columns, null, null, null,
                null, STD_lastname + " ASC, " + STD_firstname + " ASC, "
                        + STD_login + " ASC");

        int iLastName = c.getColumnIndex(STD_lastname);
        int iFirstName = c.getColumnIndex(STD_firstname);
        int iLogin = c.getColumnIndex(STD_login);

        int nbPeople = c.getCount();
        String[] array = new String[nbPeople];
        int i = 0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            array[i] = c.getString(iFirstName) + " " + c.getString(iLastName)
                    + " " + c.getString(iLogin);
            i++;
        }
        c.close();
        return array;
    }

    public String[] getAllStudents() throws SQLException {
        GetAllStudentsAsync task = new GetAllStudentsAsync();
        String[] students=null ;
        try {
            students = task.execute().get();
        }
        catch(Exception e) {
            Log.d("getAllStudents", e.toString());
        }
        return students;
    }

    static class GetAllStudentsAsync extends AsyncTask<Void, Void, String[]> {
            protected String[] doInBackground(Void... v) {
                Map<String, String> params = new HashMap<>();
                params.put("fonction", "getAllStudents");
                JSONParser jsonParser = new JSONParser();
                String[] students=null;
                try {
                    JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
                    int nbrStudents= json.getInt("a"+0);
                    students = new String[nbrStudents-2];
                    for(int i=1; i<nbrStudents; i++) {
                        Log.d("i=", i+"");
                        students[i-1] = json.getString("a"+i);
                    }
                } catch (Exception e) {
                    Log.d("getAllStudentsAsync", e.toString());
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
	public long createGroupOld(long groupNumber, int idProject) {
		ContentValues cv = new ContentValues();
		cv.put(GRP_num, groupNumber);
		cv.put(GRP_PJT_ID, idProject);
		return ourDatabase.insert(GroupTable, null, cv);
	}

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void updateGroupNumberOld(int groupID, String groupNumber) throws SQLException {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(GRP_num, groupNumber);
        ourDatabase.update(GroupTable, cvUpdate, GRP_ID + "=" + groupID, null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void deleteGroupOld(int id) throws SQLException {
        String MY_QUERY2 = "SELECT * FROM "+ MeetingTable +" INNER JOIN "+ GroupTable +" ON "+ MeetingTable +"."+ MTG_PJT_ID +"="+ GroupTable +"."+ GRP_ID +" WHERE "+ GroupTable +"."+ GRP_ID +"= ?";
        Cursor c2 = ourDatabase.rawQuery(MY_QUERY2,
                new String[] { String.valueOf(id) });
        int iMeetingID = c2.getColumnIndex(MTG_ID);

        for (c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext()) {
            deleteMeeting(c2.getInt(iMeetingID));
        }

        ourDatabase.delete(GroupTable, GRP_ID + "=" + id, null);
        ourDatabase.delete(JunctionGroupStudentTable, JCT_GRPSTD_GRP_ID + "=" + id, null);
        c2.close();
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteGroupAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Remove the student form the specified groups
     * @param studentID id of the student
     * @param groupIDs ids of the groups
     * @throws SQLException
     */
    public void removeStudentFromGroupsOld(int studentID, ArrayList<Integer> groupIDs) throws SQLException {
        for (Integer groupId : groupIDs) {
            ourDatabase.delete(JunctionGroupStudentTable, JCT_GRPSTD_GRP_ID + "="
                            + groupId + " AND " + JCT_GRPSTD_STD_ID + "=" + studentID,
                    null);
        }
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("rmStudentFromGroupAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Remove the students from a group
     * @param studentId ids of the student we want to remove
     * @param groupID id of the group
     * @throws SQLException
     */
    /*public void removeStudentsFromGroup(ArrayList<Integer> studentId, int groupID)
            throws SQLException {
        for (Integer id : studentId) {
            ourDatabase.delete(JunctionGroupStudentTable, JCT_GRPSTD_STD_ID + "=" + id + " AND " + JCT_GRPSTD_GRP_ID + "=" + groupID, null);
        }
    }*/

    /**
     * Remove all students from a group
     * @param groupID the group we want to empty
     * @throws SQLException
     */
    public void removeAllStudentsFromGroupOld(int groupID) throws SQLException {
        ourDatabase.delete(JunctionGroupStudentTable, JCT_GRPSTD_GRP_ID + "=" + groupID,
                null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("rmAllStudentFromGrpAsy", e.toString());
            }
            return null;
        }
    }

     /**
     * Add groupes to meeting
     * @param groupList ArrayList of groups we want to add
     * @param idMeeting id of the meeting
     */
    public void addGroupsToMeeting(int idMeeting, ArrayList<Group> groupList) {
        ContentValues cv = new ContentValues();
        for (Group group : groupList ) {
            cv.put(OBS_MTG_ID, idMeeting);
            cv.put(OBS_GRP_ID, group.getId());
            ourDatabase.insert(ObservationTable, null, cv);
        }
    }


    /**
     * Get a group with its id
     * @param groupId id of the group
     * @return the group
     */
    public Group getGroupByIdOld(int groupId) {
        String MY_QUERY = "SELECT * FROM " + GroupTable + " WHERE "+ GRP_ID +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(groupId)});

        c.moveToFirst();
        int iGroupId = c.getColumnIndex(GRP_ID);
        int iGroupName = c.getColumnIndex(GRP_num);
        Group group = new Group(c.getInt(iGroupId), c.getInt(iGroupName));
        c.close();
        return group;
    }


    public Group getGroupById(int groupId) {
        GetGroupByIdAsync task = new GetGroupByIdAsync();
        Group group = null;
        try {
            group = task.execute(groupId + "").get();
        } catch (Exception e) {
            Log.d("getGroupByID", "error");
        }
        return group;
    }


    static class GetGroupByIdAsync extends AsyncTask<String, Void, Group> {
        protected Group doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            params.put("fonction", "getGroupById");
            params.put(GRP_ID, strings[0]);
            JSONParser jsonParser = new JSONParser();
            Group group = null;
            try {
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
                group = new Group(json.getInt(GRP_ID), json.getInt(GRP_num));
            } catch (Exception e) {
                Log.d("getGroupById", e.toString());
            }
            return group;
        }
    }
    /**
     * Get a group from an observation
     * @param observationId id of the observation
     * @return the group
     */
    public Group getGroupFromObservationOld(int observationId) {
        String MY_QUERY = "SELECT * FROM " + GroupTable
                + " INNER JOIN " + ObservationTable
                + " ON " + ObservationTable + "." + OBS_GRP_ID + "=" + GroupTable + "." + GRP_ID
                + " WHERE "+ OBS_ID +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(observationId)});

        c.moveToFirst();
        int iGroupId = c.getColumnIndex(GRP_ID);
        int iGroupName = c.getColumnIndex(GRP_num);
        Group group = new Group(c.getInt(iGroupId), c.getInt(iGroupName));
        c.close();
        return group;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public ArrayList<Group> getGroupsFromStudentOld(int studentId) {
        String MY_QUERY = "SELECT * FROM " + GroupTable + " INNER JOIN "+ JunctionGroupStudentTable
                +" ON "+ GroupTable +"."+ GRP_ID +"="+ JunctionGroupStudentTable +"."+ JCT_GRPSTD_GRP_ID
                +" WHERE "+ JunctionGroupStudentTable +"."+ JCT_GRPSTD_STD_ID +"= ? ORDER BY "+ GroupTable +"."+ GRP_num +" ASC  ";

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(studentId)});

        int iGroupName = c.getColumnIndex(GRP_num);
        int iGroupId = c.getColumnIndex(GRP_ID);
        int iProjectId = c.getColumnIndex(GRP_PJT_ID);
        ArrayList<Group> groupsList = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Group group = new Group(c.getInt(iGroupId), c.getInt(iGroupName), c.getInt(iProjectId));
            groupsList.add(group);
        }
        c.close();
        return groupsList;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public ArrayList<Group> getGroupsFromProjectOld(int idProject) {
        String[] columns = new String[] { GRP_ID, GRP_num, GRP_PJT_ID };
        Cursor c = ourDatabase.query(GroupTable, columns, GRP_PJT_ID + "="
                + idProject, null, null, null, null);

        int iId = c.getColumnIndex(GRP_ID);
        int iGroupNumber = c.getColumnIndex(GRP_num);
        ArrayList<Group> groups = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Group group = new Group(c.getInt(iId), c.getInt(iGroupNumber));
            groups.add(group);
        }
        c.close();
        return groups;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public String[] getGroupsFromProjectArrayOld(int idProject) {
        String[] columns = new String[] { GRP_ID, GRP_num, GRP_PJT_ID };
        Cursor c = ourDatabase.query(GroupTable, columns, GRP_PJT_ID + "="
                + idProject, null, null, null, null);

        int iId = c.getColumnIndex(GRP_ID);
        int iGroupNumber = c.getColumnIndex(GRP_num);
        String[] groups = new String[c.getCount()];
        int i=0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            groups[i] = "" + c.getInt(iGroupNumber);
            i++;

        }
        c.close();
        return groups;
    }

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
    public ArrayList<Group> getGroupsFromMeetingIdOld(int meetingId) {
        String MY_QUERY = "SELECT * FROM "+ GroupTable 
                +" INNER JOIN "+ ObservationTable +" ON "
                + GroupTable +"."+ GRP_ID +"="+ ObservationTable +"."+ OBS_GRP_ID
                +" WHERE "+ ObservationTable +"."+ OBS_MTG_ID +"= ? "
                + "ORDER BY "+ GroupTable +"."+ GRP_num;

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingId)});

        c.moveToFirst();
        int iId = c.getColumnIndex(GRP_ID);
        int iNumber = c.getColumnIndex(GRP_num);
        int iProjetId = c.getColumnIndex(GRP_PJT_ID);

        ArrayList<Group> groups = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            Group group = new Group(c.getInt(iId), c.getInt(iNumber), c.getInt(iProjetId));
            groups.add(group);
        }
        c.close();
        return groups;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public String[] getGroupsArrayFromMeetingIdOld(int idMeeting) {
        String MY_QUERY = "SELECT * FROM "+ GroupTable
                +" INNER JOIN "+ ObservationTable +" ON "
                + GroupTable +"."+ GRP_ID +"="+ ObservationTable +"."+ OBS_GRP_ID
                +" WHERE "+ ObservationTable +"."+ OBS_MTG_ID +"= ? "
                + "ORDER BY "+ GroupTable +"."+ GRP_num;

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(idMeeting)});

        c.moveToFirst();
        int iId = c.getColumnIndex(GRP_ID);
        int iNumber = c.getColumnIndex(GRP_num);
        int iProjetId = c.getColumnIndex(GRP_PJT_ID);

        String[] groups = new String[c.getCount()];
        int i=0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            groups[i] = "Groupe " + c.getInt(iNumber);
            i++;

        }
        c.close();
        return groups;
    }

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
	public long createProjectOld(Project project) {
		ContentValues cv = new ContentValues();
		cv.put(PJT_name, project.getName());
        //cv.put(PJT_TPJ_ID, project.getTypeId());
		return ourDatabase.insert(ProjectTable, null, cv);
	}

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void updateProjectOld(Project project)
            throws SQLException {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(PJT_name, project.getName());
        ourDatabase.update(ProjectTable, cvUpdate, PJT_ID + "=" + project.getId(), null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void deleteProjectOld(int id) throws SQLException {
        String MY_QUERY = "SELECT * FROM "+ GroupTable + " INNER JOIN "+ ProjectTable +" ON "+ GroupTable +"."+ GRP_PJT_ID +"="+ ProjectTable +"."+ PJT_ID +" WHERE "+ ProjectTable +"."+ PJT_ID +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[] { String.valueOf(id) });
        int iGrpID = c.getColumnIndex(GRP_ID);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            deleteGroup(c.getInt(iGrpID));
        }
        c.close();
        ourDatabase.delete(ProjectTable, PJT_ID + "=" + id, null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public Project getProjectByIdOld(int projectId) {
        String MY_QUERY = "SELECT * FROM "+ ProjectTable +" WHERE "+ PJT_ID +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(projectId)});

        c.moveToFirst();
        int iProjectId = c.getColumnIndex(PJT_ID);
        int iProjectName = c.getColumnIndex(PJT_name);
        //int iProjectType = c.getColumnIndex(PJT_TPJ_ID);
        Project project = new Project(c.getInt(iProjectId), c.getString(iProjectName)); //, c.getInt(iProjectType));
        c.close();
        return project;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public Project getProjectFromMeetingIdOld(int meetingId) {
        String MY_QUERY = "SELECT * FROM "+ ProjectTable
                + " INNER JOIN " + MeetingTable + " ON "
                + MeetingTable + "." + MTG_PJT_ID + " = " + ProjectTable + "." + PJT_ID
                + " WHERE "+ MTG_ID +" = ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingId)});

        c.moveToFirst();
        int iProjectId = c.getColumnIndex(PJT_ID);
        int iProjectName = c.getColumnIndex(PJT_name);
        Project project = new Project(c.getInt(iProjectId), c.getString(iProjectName));
        c.close();
        return project;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public ArrayList<Project> getprojectsListOld() {
        String[] columns = new String[]{PJT_ID, PJT_name};
        Cursor c = ourDatabase.query(ProjectTable, columns, null, null, null,
                null, PJT_name + " ASC");

        int iProjectId = c.getColumnIndex(PJT_ID);
        int iProjectName = c.getColumnIndex(PJT_name);

        ArrayList<Project> projectsList = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Project project = new Project(c.getInt(iProjectId), c.getString(iProjectName));
            projectsList.add(project);
        }
        c.close();
        return projectsList;
    }


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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public String[] getProjectsArrayOld() {
        String[] columns = new String[]{PJT_ID, PJT_name};
        Cursor c = ourDatabase.query(ProjectTable, columns, null, null, null,
                null, PJT_name + " ASC");

        int iProjectId = c.getColumnIndex(PJT_ID);
        int iProjectName = c.getColumnIndex(PJT_name);

        String[] projectsList = new String[c.getCount()];
        int i=0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            projectsList[i]=c.getString(iProjectName);
            i++;
        }
        c.close();
        return projectsList;
    }

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
    public long createMeetingTypeOld(MeetingType meetingType) {
        ContentValues cv = new ContentValues();
        cv.put(TMT_name, meetingType.getName());
        return ourDatabase.insert(MeetingTypeTable, null, cv);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void updateMeetingTypeOld(int meetingTypeId, String name)
            throws SQLException {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(TMT_name, name);
        ourDatabase.update(MeetingTypeTable, cvUpdate, TMT_ID + "=" + meetingTypeId, null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void deleteMeetingTypeOld(int id) {
        ourDatabase.delete(MeetingTypeTable, TMT_ID + "=" + id, null);
        ourDatabase.delete(JunctionMeetingTypeEventTypeTable, JCT_TMTTEV_TMT_ID + "=" + id, null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteMeetingTypeAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Add event types to meeting type
     * @param types ArrayList of eventType we want to add
     * @param idMeetingType id of the meetingType
     */
    /*public void addEventTypesToMeetingTypeOld(int idMeetingType, ArrayList<EventType> types) {
        ContentValues cv = new ContentValues();
        for (EventType type : types) {
            cv.put(JCT_TMTTEV_TMT_ID, idMeetingType);
            cv.put(JCT_TMTTEV_TEV_ID, type.getId());
            ourDatabase.insert(JunctionMeetingTypeEventTypeTable, null, cv);
        }
    }*/

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public MeetingType getMeetingTypeByIdOld(int meetingTypeId) {
        String MY_QUERY = "SELECT * FROM " + MeetingTypeTable + " WHERE " + TMT_ID + "= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingTypeId)});

        c.moveToFirst();
        int iMeetingTypeId = c.getColumnIndex(TMT_ID);
        int iMeetingTypeName = c.getColumnIndex(TMT_name);
        MeetingType meetingType = new MeetingType(c.getInt(iMeetingTypeId), c.getString(iMeetingTypeName));
        c.close();
        return meetingType;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public MeetingType getMeetingTypeByNameOld(String name) {
        String MY_QUERY = "SELECT * FROM " + MeetingTypeTable + " WHERE " + TMT_name + "= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{name});

        c.moveToFirst();
        int iMeetingTypeId = c.getColumnIndex(TMT_ID);
        int iMeetingTypeName = c.getColumnIndex(TMT_name);
        MeetingType meetingType = new MeetingType(c.getInt(iMeetingTypeId), c.getString(iMeetingTypeName));
        c.close();
        return meetingType;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public ArrayList<MeetingType> getMeetingTypesOld() {
        String[] columns = new String[] { TMT_ID, TMT_name };
        Cursor c = ourDatabase.query(MeetingTypeTable, columns, null, null, null,
                null, null);

        int iMeetingTypeName = c.getColumnIndex(TMT_name);
        int iMeetingTypeId = c.getColumnIndex(TMT_ID);

        ArrayList<MeetingType> meetingTypes = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            MeetingType type = new MeetingType(c.getInt(iMeetingTypeId), c.getString(iMeetingTypeName));
            meetingTypes.add(type);
        }
        c.close();
        return meetingTypes;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public String[] getMeetingTypeArrayOld() {
        String[] columns = new String[] { TMT_ID, TMT_name };
        Cursor c = ourDatabase.query(MeetingTypeTable, columns, null, null, null,
                null, null);

        int iMeetingTypeName = c.getColumnIndex(TMT_name);


        int nbMeetingType = c.getCount();
        String[] array = new String[nbMeetingType];

        int i =0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            array[i]=c.getString(iMeetingTypeName);
            i++;
        }

        c.close();
        return array;
    }


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
    public MeetingType getMeetingTypeByMeetingOld(int meetingId) {
        String MY_QUERY = "SELECT * FROM " + MeetingTypeTable
                + " INNER JOIN " + MeetingTable
                + " ON " + MeetingTable + "." + MTG_TMT_ID + " = " + MeetingTypeTable + "." + TMT_ID
                + " WHERE " + MTG_ID + "= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingId)});

        c.moveToFirst();
        int iMeetingTypeId = c.getColumnIndex(TMT_ID);
        int iMeetingTypeName = c.getColumnIndex(TMT_name);
        MeetingType meetingType = new MeetingType(c.getInt(iMeetingTypeId), c.getString(iMeetingTypeName));
        c.close();
        return meetingType;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
	public long createMeetingOld(Meeting meeting) {
		ContentValues cv = new ContentValues();
		cv.put(MTG_name, meeting.getName());
		cv.put(MTG_date, dateFormat.format(meeting.getDate()));
        cv.put(MTG_date_end, dateFormat.format(meeting.getEndingDate()));
		cv.put(MTG_TMT_ID, meeting.getTypeId());
		cv.put(MTG_PJT_ID, meeting.getProjectId());
		return ourDatabase.insert(MeetingTable, null, cv);
	}

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
                idLong = json.getLong(MTG_ID);
            } catch (Exception e) {
                Log.d("createMeetingAsync", e.toString());
            }
            return idLong;
        }
    }
    /**
     * Update the title of the meeting
     * @param meeting the meeting we want to edit
     * @throws SQLException
     */
    public void updateMeetingOld(Meeting meeting)
            throws SQLException {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(MTG_name, meeting.getName());
        cvUpdate.put(MTG_date, dateFormat.format(meeting.getDate()));
        ourDatabase.update(MeetingTable, cvUpdate, MTG_ID + "=" + meeting.getId(), null);
    }
    //TODO updateMeeting

    /**
     * Delete a meeting
     * @param id id of the meeting
     * @throws SQLException
     */
    /*public void deleteMeetingOld(int id) throws SQLException {  //à verifier
        String MY_QUERY = "SELECT * FROM "+ EventTable
                +" INNER JOIN "+ ObservationTable
                +" ON "+ EventTable +"."+ EVT_OBS_ID +"="+ ObservationTable +"."+ OBS_ID
                +" INNER JOIN "+ MeetingTable
                +" ON " + MeetingTable +"."+ MTG_ID +"="+ ObservationTable +"."+ OBS_MTG_ID
                +" WHERE "+ MeetingTable +"."+ MTG_ID +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[] { String.valueOf(id) });
        int iEventID = c.getColumnIndex(EVT_ID);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            deleteEvent(c.getInt(iEventID));
        }
        ourDatabase.delete(ObservationTable, OBS_ID + "=" + id, null);

        ourDatabase.delete(MeetingTable, MTG_ID + "=" + id, null);
        c.close();
    }*/

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteMeetingAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Remove all the events linked to a meeting
     * @param meetingID id of the meeting
     * @throws SQLException
     */
   /* public void removeEventsFromMeeting(int meetingID) throws SQLException {
        ourDatabase.delete(JunctionMeetingEventTypeTable,
                JCT_MTGTEV_MTG_ID + "=" + meetingID, null);
    }*/

    /**
     * Modify the ending date of a meeting
     */
    public void setMeetingEndOld(Meeting meeting){
        ContentValues cv = new ContentValues();
        cv.put(MTG_date_end, dateFormat.format(meeting.getEndingDate()));
        ourDatabase.update(MeetingTable, cv,MTG_ID + "=" + meeting.getId(), null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public Meeting getMeetingByIdOld(int meetingId) {

        String MY_QUERY = "SELECT * FROM "+ MeetingTable +" WHERE "+ MTG_ID +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingId)});

        c.moveToFirst();
        int iMeetingId = c.getColumnIndex(MTG_ID);
        int iProjectId= c.getColumnIndex(MTG_PJT_ID);
        int iMeetingName = c.getColumnIndex(MTG_name);
        int iMeetingDate = c.getColumnIndex(MTG_date);
        int iMeetingDateEnding = c.getColumnIndex(MTG_date_end);
        int iMeetingTypeId = c.getColumnIndex(MTG_TMT_ID);

        Meeting meeting=null;
        try {
            meeting = new Meeting(c.getInt(iMeetingId), c.getInt(iProjectId),
                    c.getInt(iMeetingTypeId), c.getString(iMeetingName),
                    dateFormat.parse(c.getString(iMeetingDate)),
                    dateFormat.parse(c.getString(iMeetingDateEnding)));

        } catch(Exception e) {
            e.getMessage();
        }
        c.close();
        //meeting.setEvents(getEventsFromMeeting(meetingId));
        return meeting;
    }


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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
     * @param meetingName id of the meeting
     * @return the meeting
     */
    public Meeting getMeetingByNameOld(String meetingName) {

        String MY_QUERY = "SELECT * FROM "+ MeetingTable +" WHERE "+ MTG_name +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingName)});

        c.moveToFirst();
        int iMeetingId = c.getColumnIndex(MTG_ID);
        int iProjectId= c.getColumnIndex(MTG_PJT_ID);
        int iMeetingName = c.getColumnIndex(MTG_name);
        int iMeetingDate = c.getColumnIndex(MTG_date);
        int iMeetingDateEnding = c.getColumnIndex(MTG_date_end);
        int iMeetingTypeId = c.getColumnIndex(MTG_TMT_ID);

        Meeting meeting=null;
        try {
            meeting = new Meeting(c.getInt(iMeetingId), c.getInt(iProjectId),
                    c.getInt(iMeetingTypeId), c.getString(iMeetingName),
                    dateFormat.parse(c.getString(iMeetingDate)),
                    dateFormat.parse(c.getString(iMeetingDateEnding)));

        } catch(Exception e) {
            e.getMessage();
        }
        c.close();
        //meeting.setEvents(getEventsFromMeeting(meetingId));
        return meeting;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public Meeting getMeetingByObservationOld(int observationId) {
        String MY_QUERY = "SELECT * FROM "+ MeetingTable
                + " INNER JOIN " + ObservationTable + " ON "
                + ObservationTable + "." + OBS_MTG_ID + " = " + MeetingTable + "." + MTG_ID
                +" WHERE "+ OBS_ID +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(observationId)});

        c.moveToFirst();
        int iMeetingId = c.getColumnIndex(MTG_ID);
        int iProjectId= c.getColumnIndex(MTG_PJT_ID);
        int iMeetingName = c.getColumnIndex(MTG_name);
        int iMeetingDate = c.getColumnIndex(MTG_date);
        int iMeetingDateEnding = c.getColumnIndex(MTG_date_end);
        int iMeetingTypeId = c.getColumnIndex(MTG_TMT_ID);

        Meeting meeting=null;
        try {
            meeting = new Meeting(c.getInt(iMeetingId), c.getInt(iProjectId),
                    c.getInt(iMeetingTypeId), c.getString(iMeetingName),
                    dateFormat.parse(c.getString(iMeetingDate)),
                    dateFormat.parse(c.getString(iMeetingDateEnding)));

        } catch(Exception e) {
            e.getMessage();
        }
        c.close();
        //meeting.setEvents(getEventsFromMeeting(meetingId));
        return meeting;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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

    public String[] getAllMeetingNameArrayOld() {
        String[] columns = new String[] { MTG_ID, MTG_PJT_ID, MTG_name, MTG_date, MTG_date_end, MTG_TMT_ID };
        Cursor c = ourDatabase.query(MeetingTable, columns, null, null, null,
                null, MTG_name + " ASC ");

        int iMeetingId = c.getColumnIndex(MTG_ID);
        int iProjectId= c.getColumnIndex(MTG_PJT_ID);
        int iMeetingName = c.getColumnIndex(MTG_name);
        int iMeetingDate = c.getColumnIndex(MTG_date);
        int iMeetingDateEnding = c.getColumnIndex(MTG_date_end);
        int iMeetingTypeId = c.getColumnIndex(MTG_TMT_ID);

        int nbMeeting = c.getCount();
        String[] array = new String[nbMeeting];
        int i = 0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            array[i] = c.getString(iMeetingName);
            i++;
        }
        c.close();
        return array;
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
/*     public void createEventOld(Event event, int obsId, Equipment equipment){
        ContentValues cv;
        if (event.getStudents()!=null) {
            for (Student student : event.getStudents()) {
                cv = new ContentValues();
                cv.put(EVT_date, dateFormat.format(event.getDate()));
                cv.put(EVT_OBS_ID, obsId);
                cv.put(EVT_comment, event.getComment());
                cv.put(EVT_TEV_ID, event.getType());
                if (equipment==null) {
                    cv.put(EVT_equipmentName, "");
                }
                else {
                    cv.put(EVT_equipmentName, equipment.getName());
                }
                cv.put(EVT_STD_ID, student.getId());
                ourDatabase.insert(EventTable, null, cv);
            }
        }
        else{
            cv = new ContentValues();
            cv.put(EVT_date, dateFormat.format(event.getDate()));
            cv.put(EVT_OBS_ID, obsId);
            cv.put(EVT_comment, event.getComment());
            cv.put(EVT_TEV_ID, event.getType());
            if (equipment==null) {
                cv.put(EVT_equipmentName, "");
            }
            else {
                cv.put(EVT_equipmentName, equipment.getName());
            }
            ourDatabase.insert(EventTable, null, cv);
        }
        //long eventId = ourDatabase.insert(EventTable, null, cv);
        *//*if (event.getStudents()!=null) {
            ContentValues cv2 = new ContentValues();
            cv2.put(JCT_EVTSTD_EVT_ID, eventId);


            for (Student student : event.getStudents()) {
                cv2.put(JCT_EVTSTD_STD_ID, student.getId());
                ourDatabase.insert(JunctionEventStudentTable, null, cv2);
            }*//*
    } */


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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("createEventStudentAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Delete an event
     * @param id id of the event
     * @throws SQLException
     */
    /*public void deleteEvent(int id) throws SQLException {
        ourDatabase.delete(EventTable, EVT_ID + "=" + id, null);
        //ourDatabase.delete(JunctionEventStudentTable, JCT_EVTSTD_EVT_ID + "=" + id, null);
    }*/


    /**
     * Search the events linked to a meeting
     * @param obsId the id of th meeting
     * @return a list of the events
     */
   /* public ArrayList<Event> getEventsFromObservationOld(int obsId) { //getEventsFromMeeting
        ArrayList<Event> events = new ArrayList<>();
        String[] columns = new String[] { EVT_ID, EVT_date, EVT_TEV_ID, EVT_comment,EVT_equipmentName };
        Cursor c = ourDatabase.query(EventTable, columns, EVT_OBS_ID + "="
                + obsId, null, null, null, EVT_date + " ASC");

        int iEventId = c.getColumnIndex(EVT_ID);
        int iEventDate = c.getColumnIndex(EVT_date);
        int iEventType = c.getColumnIndex(EVT_TEV_ID);
        int iEventComment = c.getColumnIndex(EVT_comment);
        int iEquipmentName = c.getColumnIndex(EVT_equipmentName);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                Event event = new Event(c.getInt(iEventId), c.getInt(iEventType),
                        dateFormat.parse(c.getString(iEventDate)), c.getString(iEventComment),
                        getStudentsFromEvent(c.getInt(iEventId)),c.getString(iEquipmentName));
                //event.setStudents(getStudentsFromEvent(c.getInt(iEventId)));
                events.add(event);
            } catch(Exception e) {
                e.getMessage();
            }
        }
        c.close();
        return events;
    } */

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public ArrayList<Event> getEventsFromObservationStudentEventTypeListsOld(int obsId, ArrayList<Student> students,
                                                                      ArrayList<EventType> eventTypes) {
        ArrayList<Event> events = new ArrayList<>();

        String MY_QUERY = "SELECT * FROM " + EventTable
                + " INNER JOIN " + ObservationTable
                + " ON " + ObservationTable + "." + OBS_ID + "=" + EventTable + "." + EVT_OBS_ID
                + " INNER JOIN " + StudentTable
                + " ON " + EventTable + "." + EVT_STD_ID + "=" + StudentTable + "." + STD_ID
                + " INNER JOIN "+ EventTypeTable
                + " ON "+ EventTypeTable +"."+ TEV_ID + "=" + EventTable + "." + EVT_TEV_ID
                + " WHERE " + OBS_ID + "=" + " ? "
                + " AND STD_login IN " + "( ? )"
                + " AND TEV_ID IN " + "( ? )";

        String whereStudents = "";
        for(Student student : students) {
            whereStudents+= STD_login + "=" + student.getLogin() + " OR ";
        }
        if (whereStudents.length()!=0) {
            whereStudents = whereStudents.substring(0,whereStudents.length()-4); //there was an " OR " at the end
        }

        String whereEventTypes = "";
        for(EventType eventType : eventTypes) {
            whereEventTypes+= TEV_name + "=" + eventType.getName() + " OR ";
        }
        if (whereEventTypes.length()!=0) {
            whereEventTypes = whereEventTypes.substring(0, whereEventTypes.length() - 4);
        }


        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(obsId), whereStudents, whereEventTypes});
        c.moveToFirst();

        int iEventId = c.getColumnIndex(EVT_ID);
        int iEventDate = c.getColumnIndex(EVT_date);
        int iEventType = c.getColumnIndex(EVT_TEV_ID);
        int iEventComment = c.getColumnIndex(EVT_comment);
        int iEquipmentName = c.getColumnIndex(EVT_equipmentName);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                Event event = new Event(c.getInt(iEventId), c.getInt(iEventType),
                        dateFormat.parse(c.getString(iEventDate)), c.getString(iEventComment),
                        getStudentFromEvent(c.getInt(iEventId)),c.getString(iEquipmentName));
                //event.setStudents(getStudentsFromEvent(c.getInt(iEventId)));
                events.add(event);
            } catch(Exception e) {
                e.getMessage();
            }
        }
        c.close();
        return events;
    }


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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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

    public String getEventTypeNameFromEventOld(Event event) {
        String MY_QUERY = "SELECT * FROM "+ EventTypeTable
                +" INNER JOIN "+ EventTable
                +" ON "+ EventTypeTable +"."+ TEV_ID +"="+ EventTable + "." + EVT_TEV_ID
                + " WHERE "+ EVT_ID + " = ?";

        Cursor cursor = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(event.getId())});

        int iTevName = cursor.getColumnIndex(TEV_name);
        cursor.moveToFirst();
        String name;
        if (event.getType()==0) {
            name="commentaire";
        }
        else {
            name= cursor.getString(iTevName);
        }
        cursor.close();
        return name;
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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
                name = json.getString(TEV_name);
            }
            catch (Exception e) {
                Log.d("EventTypNamFrmEventAsy", e.toString());
            }
            return name;
        }
    }


    public ArrayList<Event> getEventFromEventName(String eventName) {
        ArrayList<Event> events = new ArrayList<>();

        String MY_QUERY = "SELECT * FROM "+ EventTable
                +" INNER JOIN "+ EventTypeTable
                +" ON "+ EventTypeTable +"."+ TEV_ID +"="+ EventTable + "." + EVT_TEV_ID
                + " WHERE "+ TEV_name + " = ?";

        Cursor c = ourDatabase.rawQuery(MY_QUERY, new String[]{eventName});

        c.moveToFirst();

        int iEventId = c.getColumnIndex(EVT_ID);
        int iEventDate = c.getColumnIndex(EVT_date);
        int iEventType = c.getColumnIndex(EVT_TEV_ID);
        int iEventComment = c.getColumnIndex(EVT_comment);
        int iEquipmentName = c.getColumnIndex(EVT_equipmentName);


        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                Event event = new Event(c.getInt(iEventId), c.getInt(iEventType),
                        dateFormat.parse(c.getString(iEventDate)), c.getString(iEventComment),
                        getStudentFromEvent(c.getInt(iEventId)),c.getString(iEquipmentName));
                //event.setStudents(getStudentsFromEvent(c.getInt(iEventId)));
                events.add(event);
            } catch(Exception e) {
                e.getMessage();
            }
        }
        c.close();
        return events;
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
    public long createEventTypeOld(EventType eventType){
        ContentValues cv = new ContentValues();
        cv.put(TEV_name, eventType.getName());
        cv.put(TEV_equipmentName, eventType.getEquipmentName());
        return ourDatabase.insert(EventTypeTable, null, cv);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void updateEventTypeOld(int eventTypeId, String eventTypeName) throws SQLException {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(TEV_name, eventTypeName);
        ourDatabase.update(EventTypeTable, cvUpdate, TEV_ID + "=" + eventTypeId, null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public void deleteEventTypeOld(int id) throws SQLException {
        ourDatabase.delete(EventTypeTable, TEV_ID + "=" + id, null);
        ourDatabase.delete(JunctionMeetingTypeEventTypeTable, JCT_TMTTEV_TEV_ID + "=" + id, null);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
            } catch (Exception e) {
                Log.d("deleteEventTypeAsync", e.toString());
            }
            return null;
        }
    }
    /**
     * Add all the event's types linked to a meeting
     * @param idLastMeeting id of the meeting
     * @param types ArrayList of event's types
     */
    /*public void addEventTypesToMeeting(int idLastMeeting, ArrayList<EventType> types) {
        ContentValues cv = new ContentValues();
        for (EventType type : types) {
            cv.put(JCT_MTGTEV_TEV_ID, type.getId());
            cv.put(JCT_MTGTEV_MTG_ID, idLastMeeting);
            ourDatabase.insert(JunctionMeetingEventTypeTable, null, cv);
        }
    }*/

    /**
     * Get an event's type with its id
     * @param eventTypeId id of the event's type
     * @return the eventType
     */
    public EventType getEventTypeByIdOld(int eventTypeId) {
        String MY_QUERY = "SELECT * FROM " + EventTypeTable + " WHERE " + TEV_ID + "= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(eventTypeId)});
        c.moveToFirst();
        int iEventTypeId = c.getColumnIndex(TEV_ID);
        int iEventTypeName = c.getColumnIndex(TEV_name);
        int iEventTypeEquipmentName = c.getColumnIndex(TEV_equipmentName);
        EventType eventType = new EventType(c.getInt(iEventTypeId), c.getString(iEventTypeName),
                c.getString(iEventTypeEquipmentName));
        c.close();
        return eventType;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public EventType getEventTypeByNameOld(String name) throws SQLException {

        String[] columns = new String[] { TEV_ID, TEV_name, TEV_equipmentName};
        Cursor c = ourDatabase.query(EventTypeTable, columns, TEV_name + "= \""
                + name + "\"", null, null, null, null);

        c.moveToFirst();
        EventType eventType = null;
        if (c.getCount() != 0) {
            eventType = new EventType(c.getInt(0), c.getString(1), c.getString(2));
        }
        c.close();
        return eventType;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
                eventType = new EventType(json.getInt(TEV_ID), json.getString(TEV_name), json.getString(TEV_equipmentName));
            } catch (Exception e) {
                Log.d("getEventTypeByNameAsync", e.toString());
            }
            return eventType;
        }
    }



    public ArrayList<EventType> getEventTypeListByEquipmentOld(Equipment equipment) {
        String MY_QUERY = "SELECT * FROM "+ EventTypeTable
                +" WHERE "+ EventTypeTable +"."+ TEV_equipmentName +"= ?";

        Cursor cursor = ourDatabase.rawQuery(MY_QUERY,
                new String[]{equipment.getName()});

        int iTevId = cursor.getColumnIndex(TEV_ID);
        int iTevname = cursor.getColumnIndex(TEV_name);
        int iTevEquipmentName= cursor.getColumnIndex(TEV_equipmentName);
        ArrayList<EventType> eventTypes = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            EventType type = new EventType(cursor.getInt(iTevId),cursor.getString(iTevname),
                    cursor.getString(iTevEquipmentName));
            eventTypes.add(type);
        }
        cursor.close();
        return eventTypes;
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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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

    public String[] getEventTypeArrayByEquipmentOld(Equipment equipment) {
        String MY_QUERY = "SELECT * FROM "+ EventTypeTable
                +" WHERE "+ EventTypeTable +"."+ TEV_equipmentName +"= ?";

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{equipment.getName()});

        int iTevName = c.getColumnIndex(TEV_name);

        String[] eventTypeArray = new String[c.getCount()];

        int i=0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            eventTypeArray[i]=c.getString(iTevName);
            i++;
        }
        c.close();
        return eventTypeArray;
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
    public ArrayList<EventType> getEventTypeFromMeetingTypeListOld(int meetingTypeId) {
        String MY_QUERY = "SELECT * FROM " + EventTypeTable + " INNER JOIN "+ JunctionMeetingTypeEventTypeTable
                +" ON "+ EventTypeTable +"."+ TEV_ID +"="+ JunctionMeetingTypeEventTypeTable +"."+ JCT_TMTTEV_TEV_ID
                +" WHERE "+ JunctionMeetingTypeEventTypeTable +"."+ JCT_TMTTEV_TMT_ID +"= ? "
                +"ORDER BY "+ EventTypeTable +"."+ TEV_name +" ASC  ";

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingTypeId)});

        int iTevName = c.getColumnIndex(TEV_name);
        int iTevId = c.getColumnIndex(TEV_ID);
        int iTevEquipmentName= c.getColumnIndex(TEV_equipmentName);
        ArrayList<EventType> eventTypeList = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            EventType eventType = new EventType(c.getInt(iTevId), c.getString(iTevName), c.getString(iTevEquipmentName));
            eventTypeList.add(eventType);
        }
        c.close();
        return eventTypeList;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public int[] getEventTypesIdWithoutEquipmentByMeetingTypeOld(int meetingTypeID) {
        String MY_QUERY = "SELECT * FROM "+ EventTypeTable
                +" INNER JOIN "+ JunctionMeetingTypeEventTypeTable
                +" ON "+ EventTypeTable +"."+ TEV_ID +"="+ JunctionMeetingTypeEventTypeTable +"."+ JCT_TMTTEV_TEV_ID
                +" WHERE "+ JunctionMeetingTypeEventTypeTable +"."+ JCT_TMTTEV_TMT_ID +"= ?"
                + " AND " + TEV_equipmentName + " = ? "
                +" ORDER BY "+ EventTypeTable +"."+ TEV_name +" ASC  ";

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingTypeID), ""});

        int iTevId = c.getColumnIndex(TEV_ID);

        int nbTEV = c.getCount();
        int[] array = new int[nbTEV];
        int i = 0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            array[i] = c.getInt(iTevId);
            i++;
        }
        c.close();
        return array;
    }

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
    public String[] getEventTypeWithoutEquipmentFromMeetingTypeArrayOld(int meetingTypeId) {
        String MY_QUERY = "SELECT * FROM " + EventTypeTable
                + " INNER JOIN "+ JunctionMeetingTypeEventTypeTable
                +" ON "+ EventTypeTable +"."+ TEV_ID +"="+ JunctionMeetingTypeEventTypeTable +"."+ JCT_TMTTEV_TEV_ID
                +" WHERE "+ JunctionMeetingTypeEventTypeTable +"."+ JCT_TMTTEV_TMT_ID +"= ? "
                + " AND " + TEV_equipmentName + " = ? "
                +" ORDER BY "+ EventTypeTable +"."+ TEV_name +" ASC  ";

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingTypeId), ""});

        int iTevName = c.getColumnIndex(TEV_name);

        String[] eventTypeArray = new String[c.getCount()];

        int i=0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            eventTypeArray[i]=c.getString(iTevName);
            i++;
        }
        c.close();
        return eventTypeArray;
    }

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
    public ArrayList<EventType> getAllEventTypesListOld() {
        String[] columns = new String[] { TEV_ID, TEV_name}; //TEV_TMT_ID };
        Cursor c = ourDatabase.query(EventTypeTable, columns, null,
                null, null, null, null);

        int iTevId = c.getColumnIndex(TEV_ID);
        int iTevName = c.getColumnIndex(TEV_name);

        ArrayList<EventType> eventTypeList= new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            EventType eventType = new EventType(c.getInt(iTevId), c.getString(iTevName));
            eventTypeList.add(eventType);

        }
        c.close();
        return eventTypeList;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public String[] getAllEventTypesArrayOld() {
        String[] columns = new String[] { TEV_ID, TEV_name, TEV_equipmentName}; // TEV_TMT_ID };
        Cursor c = ourDatabase.query(EventTypeTable, columns, null,
                null, null, null, null);

        int iTevname = c.getColumnIndex(TEV_name);
        String[] eventTypes = new String[c.getCount()];
        int i = 0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            eventTypes[i] = c.getString(iTevname);
            i++;
        }
        c.close();
        return eventTypes;
    }

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

    /**
     * Search all the event types of the meeting
     * @param meetingID id of the meeting
     * @return an array of all the name of the event's types
     */
   /* public String[] getArrayOfEventTypesFromMeeting(int meetingID) {
        String MY_QUERY = "SELECT * FROM "+ EventTypeTable
                +" INNER JOIN "+ JunctionMeetingEventTypeTable
                +" ON "+ EventTypeTable +"."+ TEV_ID +"="+ JunctionMeetingEventTypeTable +"."+ JCT_MTGTEV_TEV_ID
                +" WHERE "+ JunctionMeetingEventTypeTable +"."+ JCT_MTGTEV_MTG_ID +"= ?";
        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(meetingID)});

        int iTevName = c.getColumnIndex(TEV_name);

        String[] eventTypes = new String[c.getCount()];
        int i = 0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            eventTypes[i] = c.getString(iTevName);
            i++;
        }
        c.close();
        return eventTypes;
    }
*/




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
    public long createObservationOld(Observation observation) {
        ContentValues cv = new ContentValues();
        cv.put(OBS_GRP_ID, observation.getIdGroup());
        cv.put(OBS_MTG_ID, observation.getIdMeeting());

        return ourDatabase.insert(ObservationTable, null, cv);
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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
    public Observation getObservationByMeetingAndGroupIdOld(int idMeeting, int idGroup) throws SQLException {
        String MY_QUERY = "SELECT * FROM "+ ObservationTable
                +" WHERE "+ OBS_GRP_ID +"= ?" + " AND " + OBS_MTG_ID + "= ? ";

        Cursor c = ourDatabase.rawQuery(MY_QUERY,
                new String[]{String.valueOf(idGroup), String.valueOf(idMeeting)});

        c.moveToFirst();
        int iId = c.getColumnIndex(OBS_ID);
        int iGroupId = c.getColumnIndex(OBS_GRP_ID);
        int iMeetingId= c.getColumnIndex(OBS_MTG_ID);
        Observation observation = new Observation(c.getInt(iId), c.getInt(iGroupId), c.getInt(iMeetingId));
        c.close();
        return observation;
    }

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
                JSONObject json = jsonParser.makeHttpRequest("http://projets.telecom-bretagne.eu/easyobs/database.php", "POST", params);
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