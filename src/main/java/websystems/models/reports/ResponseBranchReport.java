package websystems.models.reports;

public class ResponseBranchReport {
    private String quantityClients;
    private String avgClientWaitPeriod;
    private String avgTimeWork;

    public ResponseBranchReport() {
    }

    public ResponseBranchReport(String quantityClients, String avgClientWaitPeriod, String avgTimeWork) {
        this.quantityClients = quantityClients;
        this.avgClientWaitPeriod = avgClientWaitPeriod;
        this.avgTimeWork = avgTimeWork;
    }

    public String getQuantityClients() {
        return quantityClients;
    }

    public void setQuantityClients(String quantityClients) {
        this.quantityClients = quantityClients;
    }

    public String getAvgClientWaitPeriod() {
        return avgClientWaitPeriod;
    }

    public void setAvgClientWaitPeriod(String avgClientWaitPeriod) {
        this.avgClientWaitPeriod = avgClientWaitPeriod;
    }

    public String getAvgTimeWork() {
        return avgTimeWork;
    }

    public void setAvgTimeWork(String avgTimeWork) {
        this.avgTimeWork = avgTimeWork;
    }
}
