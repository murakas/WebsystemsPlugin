package websystems.domains.reports;

public class ResponseAvgWaitingTime {
    private String userName;
    private String averageWaitingTime;

    public ResponseAvgWaitingTime(String userName, String averageWaitingTime) {
        this.userName = userName;
        this.averageWaitingTime = averageWaitingTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(String averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }
}
