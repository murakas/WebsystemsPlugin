package websystems.models.webclient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import ru.apertum.qsystem.server.model.QUser;

import javax.persistence.Column;

public class UserMfc extends QUser {

    @Expose
    @SerializedName("user_uuid")
    private String userUuid = "00000000-0000-0000-0000-000000000000";

    public UserMfc(QUser qUser, String userUuid) {
        super.setId(qUser.getId());
        super.setAdminAccess(qUser.getAdminAccess());
        super.setAdressRS(qUser.getAdressRS());
        super.setCustomer(qUser.getCustomer());
        super.setDeleted(qUser.getDeleted());
        super.setEnable(qUser.getEnable());
        super.setName(qUser.getName());
        super.setParallelAccess(qUser.getParallelAccess());
        super.setParolcheg(qUser.getParolcheg());
        super.setPause(qUser.isPause());
        super.setPlanServices(qUser.getPlanServices());
        super.setPoint(qUser.getPoint());
        super.setPointExt(qUser.getPointExt());
        super.setReportAccess(qUser.getReportAccess());
        super.setServicesCnt(qUser.getServicesCnt());
        super.setShadow(qUser.getShadow());
        super.setTabloText(qUser.getTabloText());
        this.setUserUuid(userUuid);
    }

    public UserMfc() {
    }

    @Column(name = "user_uuid")
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
}
