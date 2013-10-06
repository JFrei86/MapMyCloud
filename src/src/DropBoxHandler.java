/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import com.dropbox.core.DbxAccountInfo;
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
    long fSize;
    
    public ArrayList<DbxEntry.File> getFiles() throws DbxException
    {
        getAllFiles();
        return files;
    }
    
    public long getQuota() throws DbxException
    {
        DbxAccountInfo accinfo = client.getAccountInfo();
       
        return accinfo.quota.normal;
    }
    public long getFreeSpace() throws DbxException
    {
        DbxAccountInfo accinfo = client.getAccountInfo();
        
        return accinfo.quota.total;
    }
    public ArrayList<DbxEntry> getFilesInDir(String dir, int n) throws DbxException
    {
        nLevels = new ArrayList<DbxEntry>();
        getFilesInDirHelper(dir,n);
        return nLevels;
        
    }
    private void getFilesInDirHelper(String dir, int n) throws DbxException
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
        nLevels = new ArrayList<DbxEntry>();
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
    public long getFolderSize(String dir) throws DbxException
    {
        fSize = 0;
        getSizeOfFolderHelper(dir);
        System.out.print("Folder: " + dir + " -- ");
        System.out.println(fSize);
        return fSize;
    }
    private void getSizeOfFolderHelper(String dir) throws DbxException
    {
       DbxEntry.WithChildren root = client.getMetadataWithChildren(dir);
       for(DbxEntry ent : root.children)
       {
           if(ent.isFile())
           {
               fSize += ent.asFile().numBytes;
           }
           else if(ent.isFolder())
           {
               getSizeOfFolderHelper(ent.path);
           }
       }
    }
     private void getAllFiles() throws DbxException
     {
         System.out.println("getting all files.."); 
         files = new ArrayList<DbxEntry.File>();
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
