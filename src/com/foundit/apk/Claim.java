package com.foundit.apk;

public class Claim extends BaseEntity implements VerificationTarget {
    private String buktiKepemilikan;
    private String lampiranFoto;
    private ClaimStatus statusKlaim;

    // Relasi dari UML
    private Item itemDitebus;
    private User pengaju;

    public Claim(String id, String buktiKepemilikan, Item itemDitebus, User pengaju) {
        super(id);
        this.buktiKepemilikan = buktiKepemilikan;
        this.statusKlaim = ClaimStatus.PENDING;
        this.itemDitebus = itemDitebus;
        this.pengaju = pengaju;
    }

    @Override
    public void approve(User admin) {
        if (admin != null && admin.isAdmin()) {
            this.statusKlaim = ClaimStatus.APPROVED;
            if (this.itemDitebus != null) {
                this.itemDitebus.updateStatus(ItemStatus.COMPLETED);
            }
        }
    }

    @Override
    public void reject(User admin) {
        if (admin != null && admin.isAdmin()) {
            this.statusKlaim = ClaimStatus.REJECTED;
        }
    }

    @Override
    public boolean isPending() {
        return this.statusKlaim == ClaimStatus.PENDING;
    }

    // Getter dan Setter
    public String getBuktiKepemilikan() { return buktiKepemilikan; }
    public void setBuktiKepemilikan(String buktiKepemilikan) { this.buktiKepemilikan = buktiKepemilikan; }

    public String getLampiranFoto() { return lampiranFoto; }
    public void setLampiranFoto(String lampiranFoto) { this.lampiranFoto = lampiranFoto; }

    public ClaimStatus getStatusKlaim() { return statusKlaim; }

    public Item getItemDitebus() { return itemDitebus; }
    public void setItemDitebus(Item itemDitebus) { this.itemDitebus = itemDitebus; }

    public User getPengaju() { return pengaju; }
    public void setPengaju(User pengaju) { this.pengaju = pengaju; }
}