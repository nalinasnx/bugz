package com.webaltry.bugz;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryConstraint implements Parcelable {
    public String field;
    public String value;
    
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel out, int arg1) {
        out.writeString(field);
        out.writeString(value);
    }
    public QueryConstraint(String field, String value) {
        this.field = field;
        this.value = value;
    }
    
    private QueryConstraint(Parcel in) {
        field = in.readString();
        value = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public QueryConstraint createFromParcel(Parcel in) {
            return new QueryConstraint(in);
        }

        public QueryConstraint[] newArray(int size) {
            return new QueryConstraint[size];
        }
    };

}
