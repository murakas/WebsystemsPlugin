package websystems.reports.models;

public class ResponseReportUser {
    private String user;
    private String timestampStart;
    private String ticket;

    public ResponseReportUser(String user, String timestampStart, String ticket) {
        this.user = user;
        this.timestampStart = timestampStart;
        this.ticket = ticket;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(String timestampStart) {
        this.timestampStart = timestampStart;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
