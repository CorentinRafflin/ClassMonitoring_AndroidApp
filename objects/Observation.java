package com.easynote.objects;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import com.easynote.tools.Database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by coren_000 on 16/03/2018.
 */

public class Observation implements Parcelable {

    private int id;

    private int id_group;

    private int id_meeting;

    private ArrayList<Event> events;

    public Observation(int id, int id_group, int id_meeting) {
        this.id=id;
        this.id_group = id_group;
        this.id_meeting = id_meeting;
    }

    public Observation(int id_group, int id_meeting) {
        this.id_group = id_group;
        this.id_meeting = id_meeting;
    }

    public Observation(Parcel parcel) {
        //same order as in writeToParcel method
        this.id = parcel.readInt();
        this.id_group = parcel.readInt();
        this.id_meeting = parcel.readInt();
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeInt(id_group);
        dest.writeInt(id_meeting);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Observation createFromParcel(Parcel parcel) {
            return new Observation(parcel);
        }
        @Override
        public Observation[] newArray(int size) {
            return new Observation[size];
        }
    };

    public void setId(int id ) { this.id=id; }

    public void setEventList(ArrayList<Event> events ) { this.events=events; }

    public int getId() {
        return id;
    }

    public int getIdGroup() {
        return id_group;
    }

    public int getIdMeeting() { return id_meeting; }

    public void setEvents(ArrayList<Event> events) {
       this.events = events;
    }

    public String getHistoric(Activity activity) {
        String historic = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        for (Event event : this.events) {
            if (event.getStudent() != null) {
                //il y a un étudiant

                if (event.getComment().equals("")) {
                    //mais pas de commentaire
                    historic += String.format("%s : %s %s \n", dateFormat.format(event.getDate()),
                            event.getStudentInfo(), event.getName(activity));
                }
                else {
                    //et un commentaire
                    historic += String.format("%s : %s. Commentaire : %s \n", dateFormat.format(event.getDate()),
                            event.getStudentInfo(), event.getComment());
                }
            }
            else {
                //il n'y a pas d'étudiant

                if (event.getComment().equals("")) {
                    //et pas de commentaire
                    historic += String.format("%s : %s \n", dateFormat.format(event.getDate()), event.getName(activity));
                }
                else {
                    //et un commentaire
                    historic += String.format("%s Commentaire : %s \n", dateFormat.format(event.getDate()),
                            event.getComment());
                }
            }
        }
        return historic;
    }


}
