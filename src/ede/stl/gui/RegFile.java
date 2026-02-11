package ede.stl.gui;

public interface RegFile {
   public long getRegisterValue(String regName);
   public long getRegisterValue(int regNumber);

   public void setRegisterValue(String regName, long regValue);
   public void setRegisterValue(int regNumber, long regValue);
}
