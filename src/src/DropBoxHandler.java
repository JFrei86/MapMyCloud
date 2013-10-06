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
    ArrayList<DbxEntry.File> files; //#badjavapractices
    ArrayList<DbxEntry> nLevels;
    
    public ArrayList<DbxEntry.File> getFiles() throws DbxException
    {
        getAllFiles();
        return files;
    }
    
    public ArrayList<DbxEntry> getFilesInDir(String dir, int n) throws DbxException
    {
        nLevels = new ArrayList<>();
        getFilesInDirHelper(dir,n);
        return nLevels;
        
    }
    public void getFilesInDirHelper(String dir, int n) throws DbxException
    {
        if(n==0)
        {
            return;
        }
        else
        {
            DbxEntry.WithChildren root = client.getMetadataWithChildren(dir);
            for(DbxEntry ent : root.children)
            {
                if(ent.isFile()){
                   nLevels.add(ent);
                }
                else if(ent.isFolder())
                {
                    int temp = n - 1;
                    String x = "" + temp; //java sucks -_-
                    System.out.println("found folder, new n will be: " + x);
                    nLevels.add(ent);
                    getFilesInDirHelper(ent.path, n-1);
                }
            }
        }
    }
    public DropBoxHandler(String auth_token, String projName) throws DbxException
    {
        DbxRequestConfig req_conf = new DbxRequestConfig(projName, Locale.getDefault().toString()); 
        client = new DbxClient(req_conf, auth_token);
        nLevels = new ArrayList<>();
        //System.out.println("dropbox handler creation success!");
        //getAllFiles(); //setups up master file list on construction #goodjavapractice
    }
    
    public DropBoxHandler(DbxClient c) throws DbxException
    {
        client = c;
        //getAllFiles(); //setups up master file list on construction #goodjavapractice
    }
    
    public void getFilesHelper(DbxEntry.Folder f) throws DbxException
    {
        DbxEntry.WithChildren root = client.getMetadataWithChildren(f.path);
        for(DbxEntry ent : root.children)
        {
            if(ent.isFolder())
            {
               getFilesHelper(ent.asFolder());
            }
            else
            {
                if(ent.isFile())
                {
                System.out.println("getting files..");
                files.add(ent.asFile());
                }
            }
        }
    }
     private void getAllFiles() throws DbxException
     {
         System.out.println("getting all files..");
         files = new ArrayList<>();
       DbxEntry.WithChildren root = client.getMetadataWithChildren("/");
       
        for(DbxEntry ent : root.children)
        {
            if(ent.isFolder())
            {
                //System.out.println("folder found! getting files inside");
               getFilesHelper(ent.asFolder());
            }
            else
            {
                System.out.println("getting files..");
                if(ent.isFile()){
                files.add(ent.asFile());
                }
            }
        }
           
     }
     
}
