/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xml.PieDatasetHandler;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxEntry;
/**
 *
 * @author Michael
 */
public class DropBoxHandler {
    DbxClient client;
    public ArrayList<DbxEntry.File> files; //#badjavapractices
    
    public ArrayList<DbxEntry.File> getFilesInDir(String dir)
    {
        ArrayList<DbxEntry.File> curDirFiles = new ArrayList<>();
         
        for(DbxEntry.File file : files)
        {
            if(file.path.equals(dir))
               curDirFiles.add(file);
        }
        
        return curDirFiles;
    }
    public DropBoxHandler(String auth_token, String projName) throws DbxException
    {
        DbxRequestConfig req_conf = new DbxRequestConfig(projName, Locale.getDefault().toString()); 
        client = new DbxClient(req_conf, auth_token);
        getAllFiles(); //setups up master file list on construction #goodjavapractice
    }
    
    public DropBoxHandler(DbxClient c) throws DbxException
    {
        client = c;
        getAllFiles(); //setups up master file list on construction #goodjavapractice
    }
    
    public void getFilesHelper(DbxEntry.Folder f) throws DbxException
    {
        DbxEntry.WithChildren root = client.getMetadataWithChildren(f.path);
        for(DbxEntry ent : root.children)
        {
            if(ent.isFolder())
               getFilesHelper(ent.asFolder());
            else
                 files.add(ent.asFile());
        }
    }
     private void getAllFiles() throws DbxException
     {
         files = new ArrayList<>();
       DbxEntry.WithChildren root = client.getMetadataWithChildren("/");
       
        for(DbxEntry ent : root.children)
        {
            if(ent.isFolder())
               getFilesHelper(ent.asFolder());
            else
                 files.add(ent.asFile());
        }
           
     }
     
}
