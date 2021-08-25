package websystems.domains.reports;

public class ResponseWaitingTime {
    private String date;
    private String stand_time;
    private String start_time;
    private String ticket;
    private String service;
    private String wait_time;

    public ResponseWaitingTime() {
    }

    public ResponseWaitingTime(String date, String stand_time, String start_time, String ticket, String service, String wait_time) {
        this.date = date;
        this.stand_time = stand_time;
        this.start_time = start_time;
        this.ticket = ticket;
        this.service = service;
        this.wait_time = wait_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStand_time() {
        return stand_time;
    }

    public void setStand_time(String stand_time) {
        this.stand_time = stand_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getWait_time() {
        return wait_time;
    }

    public void setWait_time(String wait_time) {
        this.wait_time = wait_time;
    }
}

