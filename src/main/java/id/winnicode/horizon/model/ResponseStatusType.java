package id.winnicode.horizon.model;

public enum ResponseStatusType {
    SUCCESS,
    FAIL,
    ERROR;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
