package websystems.models.reports;

public class ResponseOperatorIdle {
    String date;
    String userUuid;
    String previousServiceEnd;
    String nextServiceStart;
    String idleTime;
    Integer clientWaitPeriod;

    public ResponseOperatorIdle() {
    }

    public ResponseOperatorIdle(String date, String userUuid, String previousServiceEnd, String nextServiceStart, String idleTime, Integer clientWaitPeriod) {
        this.date = date;
        this.userUuid = userUuid;
        this.previousServiceEnd = previousServiceEnd;
        this.nextServiceStart = nextServiceStart;
        this.idleTime = idleTime;
        this.clientWaitPeriod = clientWaitPeriod;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getPreviousServiceEnd() {
        return previousServiceEnd;
    }

    public void setPreviousServiceEnd(String previousServiceEnd) {
        this.previousServiceEnd = previousServiceEnd;
    }

    public String getNextServiceStart() {
        return nextServiceStart;
    }

    public void setNextServiceStart(String nextServiceStart) {
        this.nextServiceStart = nextServiceStart;
    }

    public String getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(String idleTime) {
        this.idleTime = idleTime;
    }

    public Integer getClientWaitPeriod() {
        return clientWaitPeriod;
    }

    public void setClientWaitPeriod(Integer clientWaitPeriod) {
        this.clientWaitPeriod = clientWaitPeriod;
    }
}
