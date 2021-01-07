package io.agora.education;

public enum RoomType {
   ONE_ON_ONE(0),
   SMALL_CLASS(1),
   LARGE_CLASS(2),
   BREAKOUT_CLASS(3),
   MEDIUM_CLASS(4);

   private int value;

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int var1) {
      this.value = var1;
   }

   private RoomType(int value) {
      this.value = value;
   }
}