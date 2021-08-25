package websystems.domains.mdm;

public class ObjectsIdentifierMfc {
    private String ticketIssued;
    private String ticketCalled;
    private String ticketFormation;
    private String ticketResult;

    public ObjectsIdentifierMfc() {
    }

    public ObjectsIdentifierMfc(String ticketIssued, String ticketCalled, String ticketFormation, String ticketResult) {
        this.ticketIssued = ticketIssued;
        this.ticketCalled = ticketCalled;
        this.ticketFormation = ticketFormation;
        this.ticketResult = ticketResult;
    }

    public String getTicketIssued() {
        return ticketIssued;
    }

    public void setTicketIssued(String ticketIssued) {
        this.ticketIssued = ticketIssued;
    }

    public String getTicketCalled() {
        return ticketCalled;
    }

    public void setTicketCalled(String ticketCalled) {
        this.ticketCalled = ticketCalled;
    }

    public String getTicketFormation() {
        return ticketFormation;
    }

    public void setTicketFormation(String ticketFormation) {
        this.ticketFormation = ticketFormation;
    }

    public String getTicketResult() {
        return ticketResult;
    }

    public void setTicketResult(String ticketResult) {
        this.ticketResult = ticketResult;
    }
}
