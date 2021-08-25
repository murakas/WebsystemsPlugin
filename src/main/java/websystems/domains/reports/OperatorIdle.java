package websystems.domains.reports;

public class OperatorIdle {
    private String date;
    private String userStartTime;
    private String userFinishTime;
    private String userUuid;
    private Integer clientWaitPeriod;

    public OperatorIdle(String date, String userStartTime, String userFinishTime, String userUuid, Integer clientWaitPeriod) {
        this.date = date;
        this.userStartTime = userStartTime;
        this.userFinishTime = userFinishTime;
        this.userUuid = userUuid;
        this.clientWaitPeriod = clientWaitPeriod;
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

    public Integer getClientWaitPeriod() {
        return clientWaitPeriod;
    }

    public void setClientWaitPeriod(Integer clientWaitPeriod) {
        this.clientWaitPeriod = clientWaitPeriod;
    }
}
