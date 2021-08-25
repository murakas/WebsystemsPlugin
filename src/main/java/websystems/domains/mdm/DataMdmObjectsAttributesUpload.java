/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websystems.domains.mdm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Murad
 */
public class DataMdmObjectsAttributesUpload implements Serializable {

    public DataMdmObjectsAttributesUpload() {
    }

    public DataMdmObjectsAttributesUpload(Integer sprMdmObjectAttributeId, String mdmAttributeValue) {
        this.sprMdmObjectAttributeId = sprMdmObjectAttributeId;
        this.mdmAttributeValue = mdmAttributeValue;
    }

    @Expose
    @SerializedName("spr_mdm_object_attribute_v2_id")//spr_mdm_object_attribute_id
    private Integer sprMdmObjectAttributeId;

    public void setSprMdmObjectTypeId(Integer mdmObjectAttributeId) {
        this.sprMdmObjectAttributeId = mdmObjectAttributeId;
    }

    public Integer getSprMdmObjectAttributeId() {
        return sprMdmObjectAttributeId;
    }

    @Expose
    @SerializedName("mdm_attribute_value")//mdm_attribute_value
    private String mdmAttributeValue;

    public void setMdmAttributeValue(String mdmAttributeValue) {
        this.mdmAttributeValue = mdmAttributeValue;
    }

    public String getMdmAttributeValue() {
        return mdmAttributeValue;
    }

}
