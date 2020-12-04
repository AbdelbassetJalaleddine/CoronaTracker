package jalaleddine.abdelbasset.coronatracker.CustomObjects;

public class ContactInformation {
    private String Name,Gender,LastSeen;
    private boolean Corona;
    public ContactInformation(String name, String gender, String lastSeen, boolean corona) {
        Name = name;
        Gender = gender;
        LastSeen = lastSeen;
        Corona = corona;
    }

    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ContactInformation(String name, String gender, String lastSeen, boolean corona, long timestamp) {
        Name = name;
        Gender = gender;
        LastSeen = lastSeen;
        Corona = corona;
        this.timestamp = timestamp;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastSeen() {
        return LastSeen;
    }

    public void setLastSeen(String lastSeen) {
        LastSeen = lastSeen;
    }

    public boolean isCorona() {
        return Corona;
    }

    public void setCorona(boolean corona) {
        Corona = corona;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

public String toString(){
    return "Contact Info: "+ " Name: " + getName() + " Gender: " + getGender() + " Last Seen: " + getLastSeen() + " Corona: " + isCorona() + " Time Stamp: " + getTimestamp();
    }

}
