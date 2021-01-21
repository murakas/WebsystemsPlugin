package mdm.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Murad
 */
public class DataMdmObjectsUpload implements Serializable {

    /*public DataMdmObjectsUpload() {
        //Для хибернейт
    }*/
    @Expose
    @SerializedName("mfc_id")
    private UUID sprEmployeesMfcId;

    public void setSprEmployeesMfcId(UUID sprEmployeesMfcId) {
        this.sprEmployeesMfcId = sprEmployeesMfcId;
    }

    public UUID getSprEmployeesMfcId() {
        return sprEmployeesMfcId;
    }

    @Expose
    @SerializedName("object_type_id")
    private Integer sprMdmObjectTypeId;

    public void setSprMdmObjectTypeId(Integer mdmObjectTypeId) {
        this.sprMdmObjectTypeId = mdmObjectTypeId;
    }

    public Integer getSprMdmObjectTypeId() {
        return sprMdmObjectTypeId;
    }

    @SerializedName("data_services_id")
    private UUID dataServicesId;

    public UUID getDataServicesId() {
        return dataServicesId;
    }

    public void setDataServicesId(UUID dataServicesId) {
        this.dataServicesId = dataServicesId;
    }

    @SerializedName("created_date")

    private Date createdDate;

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    @Expose
    @SerializedName("attributes_v2")//data_mdm_objects_attributes_upload
    public ArrayList<DataMdmObjectsAttributesUpload> attribute;

    public ArrayList<DataMdmObjectsAttributesUpload> getAttributes() {
        return attribute;
    }

    public void setAttributes(ArrayList<DataMdmObjectsAttributesUpload> attributes) {
        this.attribute = attributes;
    }
}
