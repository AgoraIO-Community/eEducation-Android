package io.agora.education;

public enum UserRole {
   STUDENT(2);

   private int value;

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int var1) {
      this.value = var1;
   }

   private UserRole(int value) {
      this.value = value;
   }
}