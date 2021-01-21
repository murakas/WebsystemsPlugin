package websystems.reports.models;

public class OperatorIdle {
    private String date;
    private String userStartTime;
    private String userFinishTime;
    private String userUuid;

    public OperatorIdle(String date, String userStartTime, String userFinishTime, String userUuid) {
        this.date = date;
        this.userStartTime = userStartTime;
        this.userFinishTime = userFinishTime;
        this.userUuid = userUuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserStartTime() {
        return userStartTime;
    }

    public void setUserStartTime(String userStartTime) {
        this.userStartTime = userStartTime;
    }

    public String getUserFinishTime() {
        return userFinishTime;
    }

    public void setUserFinishTime(String userFinishTime) {
        this.userFinishTime = userFinishTime;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
}
