package it.unimib.devtrinity.moneymind.data.local.entity;

import com.google.firebase.Timestamp;

public abstract class BaseEntity {

    protected boolean deleted;
    protected Timestamp createdAt;
    protected Timestamp updatedAt;
    protected String userId;

    public BaseEntity() {

    }

    public BaseEntity(boolean deleted, Timestamp createdAt, Timestamp updatedAt, String userId) {
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
