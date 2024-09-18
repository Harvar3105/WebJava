package app.helpers;

public class IdPicker {

    private long value = 0L;

    public long getNewId(){
        return value++;
    }
}
