package com.foundit.model;

public class Claim extends BaseEntity {

    private String itemId;           // items_id
    private String userId;           // users_id (pengklaim)
    private String adminId;          // admins_id (pemroses), boleh null
    private String proofDescription; // bukti_kepemilikan
    private String attachment;       // lampiran_foto, boleh null
    private ClaimStatus status = ClaimStatus.MENUNGGU;

    // Data relasi (hasil JOIN) - hanya untuk ditampilkan
    private String itemTitle;
    private ItemStatus itemStatus;
    private String claimantName;
    private String adminName;

    public Claim() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(String proofDescription) {
        this.proofDescription = proofDescription;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    @Override
    public String toString() {
        return "Claim[id=" + getId() + ", item=" + itemId + ", user=" + userId + ", status=" + status + "]";
    }
}
