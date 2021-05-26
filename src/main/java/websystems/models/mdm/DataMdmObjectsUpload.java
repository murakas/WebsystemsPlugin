package websystems.models.mdm;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * @author Murad
 */
public class DataMdmObjectsUpload implements Serializable {

    @Expose
    @SerializedName("spr_employees_mfc_id")
    private String sprEmployeesMfcId;

    public void setSprEmployeesMfcId(String sprEmployeesMfcId) {
        this.sprEmployeesMfcId = sprEmployeesMfcId;
    }

    public String getSprEmployeesMfcId() {
        return sprEmployeesMfcId;
    }

    @Expose
    @SerializedName("spr_mdm_object_type_v2_id")
    private Integer sprMdmObjectTypeId;

    public void setSprMdmObjectTypeId(Integer mdmObjectTypeId) {
        this.sprMdmObjectTypeId = mdmObjectTypeId;
    }

    public Integer getSprMdmObjectTypeId() {
        return sprMdmObjectTypeId;
    }

    @SerializedName("data_services_id")
    private String dataServicesId;

    public String getDataServicesId() {
        return dataServicesId;
    }

    public void setDataServicesId(String dataServicesId) {
        this.dataServicesId = dataServicesId;
    }

    @SerializedName("created_date")

    private String createdDate;

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    @Expose
    @SerializedName("data_mdm_objects_attributes_upload_v2")
    public ArrayList<DataMdmObjectsAttributesUpload> attribute;

    public ArrayList<DataMdmObjectsAttributesUpload> getAttribute() {
        return attribute;
    }

    public void setAttribute(ArrayList<DataMdmObjectsAttributesUpload> attribute) {
        this.attribute = attribute;
    }
}
