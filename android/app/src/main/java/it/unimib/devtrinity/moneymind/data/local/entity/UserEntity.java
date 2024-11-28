package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
   @PrimaryKey
   @NonNull
   private String id; // UID Firebase

   private String name;
   private String email;

   public String getId() { return id; }
   public void setId(String id) { this.id = id; }

   public String getName() { return name; }
   public void setName(String name) { this.name = name; }

   public String getEmail() { return email; }
   public void setEmail(String email) { this.email = email; }
}
