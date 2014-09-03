package com.sjsu.db2;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
 
public class Unzip {
 
    public String unzipFile(String filePath){
         
        FileInputStream fis = null;
        ZipInputStream zipIs = null;
        ZipEntry zEntry = null;
        String opFilePath = null;
        try {
            fis = new FileInputStream(filePath);
            zipIs = new ZipInputStream(new BufferedInputStream(fis));
            while((zEntry = zipIs.getNextEntry()) != null){
                try{
                    byte[] tmp = new byte[4*1024];
                    FileOutputStream fos = null;
                    opFilePath = "/home/nitish2008/Downloads/"+zEntry.getName();
                    System.out.println(zEntry.getName());
                    System.out.println("Extracting file to "+opFilePath);
                    fos = new FileOutputStream(opFilePath);
                    int size = 0;
                    while((size = zipIs.read(tmp)) != -1){
                        fos.write(tmp, 0 , size);
                    }
                    
                    System.out.println("Extraction Complete");
                    fos.flush();
                    fos.close();
                    
                } catch(Exception ex){
                     System.out.println(ex.getMessage());
                }
            }
            
                        zipIs.close();
                        
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(opFilePath);
        return opFilePath;
    }
     
   
}
