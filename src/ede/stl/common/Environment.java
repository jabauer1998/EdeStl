package ede.stl.common;

import java.util.ArrayList;
import java.io.FileReader;
import java.io.FileWriter;

public class Environment{
    private ArrayList<FormattedScanner> readOnlyFileDescriptorArray;
    private ArrayList<FileWriter> writableFileDescriptorArray;

    protected Environment(){
      this.readOnlyFileDescriptorArray = new ArrayList<FormattedScanner>();
      this.writableFileDescriptorArray = new ArrayList<FileWriter>();
    }

    public int createReadOnlyFileDescriptor(String fileName){
      try{
              FileReader reader = new FileReader(fileName);
              FormattedScanner scanner = new FormattedScanner(reader); 
              for(int i = 0; i < readOnlyFileDescriptorArray.size(); i++){
                  if(readOnlyFileDescriptorArray.get(i) == null){
                      readOnlyFileDescriptorArray.set(i, scanner);
                      return i;
                  }
              }

              readOnlyFileDescriptorArray.add(scanner);
              return readOnlyFileDescriptorArray.size() - 1;
          } catch(Exception exp) {
              return -1;
          }
      }

      public int createWritableFileDescriptor(String fileName){
    try{
              FileWriter writer = new FileWriter(fileName);
              for(int i = 0; i < writableFileDescriptorArray.size(); i++){
                  if(writableFileDescriptorArray.get(i) == null){
                      writableFileDescriptorArray.set(i, writer);
                      return i;
                  }
              }

              writableFileDescriptorArray.add(writer);
              return writableFileDescriptorArray.size() - 1;
          } catch(Exception exp) {
              return -1;
          }
      }

      public FormattedScanner getFileReader(int fileDescriptor){
    return readOnlyFileDescriptorArray.get(fileDescriptor);
      }

      public FileWriter getFileWriter(int fileDescriptor){
    return writableFileDescriptorArray.get(fileDescriptor);
      }

      public void clearFileReader(int fileDescriptor){
    readOnlyFileDescriptorArray.set(fileDescriptor, null);
      }

      public void clearFileWriter(int fileDescriptor){
    writableFileDescriptorArray.set(fileDescriptor, null);
      }
}