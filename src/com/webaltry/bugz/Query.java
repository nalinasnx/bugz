
package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.j2bugzilla.rpc.BugSearch;

public class Query implements Parcelable {

    public long id;
    public boolean idValid;
    public String name;
    public String description;
    public ArrayList<QueryConstraint> constraints;

    // private ArrayList<Bug> results;

//    private Query(long id) {
//        // id = UUID.randomUUID().getLeastSignificantBits();
//        this.id = id;// nextId.incrementAndGet();
//        this.idValid = true;
//        this.constraints = new ArrayList<QueryConstraint>();
//    }

    public Query() {
        // this(id);

        this.idValid = false;
        this.constraints = new ArrayList<QueryConstraint>();
    }

     public boolean getIdValid() {
     return idValid;
     }
     public long getId() {
     return id;
     }

    public void addConstraint(String field, String value) {
        constraints.add(new QueryConstraint(field, value));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeByte((byte) (idValid ? 1 : 0));
        out.writeString(name);
        out.writeString(description);
        out.writeTypedList(constraints);

    }

    private Query(Parcel in) {
        // this();
        id = in.readLong();
        idValid = in.readByte() != 0;
        name = in.readString();
        description = in.readString();
        constraints = new ArrayList<QueryConstraint>();
        in.readTypedList(constraints, QueryConstraint.CREATOR);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Query createFromParcel(Parcel in) {
            return new Query(in);
        }

        public Query[] newArray(int size) {
            return new Query[size];
        }
    };

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class SearchAll extends BugSearch {

        private Map<Object, Object> altParams = new HashMap<Object, Object>();

        private static SearchQuery search_nothing = new SearchQuery(BugSearch.SearchLimiter.OWNER,
                "me");

        public SearchAll() {
            super(search_nothing);
        }

        @Override
        public Map<Object, Object> getParameterMap() {
            return Collections.unmodifiableMap(altParams);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class SearchConstraints extends BugSearch {

        private Map<Object, Object> altParams = new HashMap<Object, Object>();

        private static SearchQuery search_nothing = new SearchQuery(BugSearch.SearchLimiter.OWNER,
                "me");

        public SearchConstraints(ArrayList<QueryConstraint> constraints) {
            super(search_nothing);

            for (QueryConstraint constraint : constraints) {
                altParams.put(constraint.field, constraint.value);
            }
        }

        @Override
        public Map<Object, Object> getParameterMap() {
            return Collections.unmodifiableMap(altParams);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BugSearch getSearch() {

        if (constraints.isEmpty())
            return new SearchAll();

        return new SearchConstraints(constraints);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
