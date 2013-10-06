/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import org.apache.commons.lang3.StringUtils;
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
import com.dropbox.core.DbxDelta;
import java.util.Hashtable;
import com.dropbox.core.DbxPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map.*;
import java.util.*;
/**
 *
 * @author Michael
 */

public class DropBoxHandler {
    DbxClient client;
    ArrayList<DbxEntry.File> files; //#badjavapractices
    ArrayList<DbxEntry> nLevels;
    HashMap<String,Long> folderHash;
    HashMap<String,Long> fileHash;
    ArrayList<String> allFolderNames;
    ArrayList<String> allFolderNamesRelativePaths = new ArrayList<String>();
    //ArrayList<String,long> foldercache;
    Long fSize;
   // ArrayList<DbxEntry.Folder> allFolders;
    private void loadHash(File file) throws IOException, ClassNotFoundException
    { 
        if(!file.exists())
        {
            return;
        }
        FileInputStream f = new FileInputStream(file);
        ObjectInputStream s = new ObjectInputStream(f);
        folderHash = (HashMap<String, Long>) s.readObject();
        s.close();
    }
    public boolean isValidFolder(String x)
    {
        if(allFolderNamesRelativePaths.contains(x))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private void saveHash(File file) throws IOException
    {
        if(file.exists())
        {
            file.delete();
            file.createNewFile();
        }
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(folderHash);
        s.close();
    }
    
    private void createFileHash() throws DbxException, IOException, ClassNotFoundException
    {
        File file = new File("fileHashCache");
        if(file.exists())
        {
            loadHash(file);
            return;
        }
        fileHash = new HashMap<String,Long>();
        
        
        for(String path : allFolderNames) //optimized for efficeny
        {
            getFolderSize(path);
            System.out.println(path);
            folderHash.put(path,fSize);
        }
        
        saveHash(file); //cache that hash
    }
    
    private void createHash() throws DbxException, IOException, ClassNotFoundException
    {
        File file = new File("hashCache");
        if(file.exists())
        {
            loadHash(file);
            return;
        }
        folderHash = new HashMap<String,Long>();
        allFolderNames = new ArrayList<String>();
        allFolderNames.add("/");
       
        getAllFolders("/");
        Collections.sort(allFolderNames, new customComparator());
        //allFolderNames.
        for(String path : allFolderNames) //optimized for efficeny
        {
            getFolderSize(path);
            System.out.println(path);
            folderHash.put(path,fSize);
        }
        
        saveHash(file); //cache that hash
    }
    
    private void createHash(boolean update) throws DbxException, IOException, ClassNotFoundException
    {
        File file = new File("hashCache");
        if(file.exists())
        {
            loadHash(file);
            if(!update)
            {
                return;
            }
        }
        
        //folderHash = new HashMap<String,Long>();
        allFolderNames = new ArrayList<String>();
        allFolderNames.add("/");
       
        getAllFolders("/");
        Collections.sort(allFolderNames, new customComparator());
        
        for(String path : allFolderNames) //optimized for efficeny
        {
            if(folderHash.containsKey(path))
            {
                continue;
            }
            getFolderSize(path);
            System.out.println(path);
            folderHash.put(path,fSize);
        }
        
        saveHash(file); //cache that hash
    }
    
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
            if(root.children.isEmpty())
            {
                return; //no childs you got to the end
            }
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
    public DropBoxHandler(String auth_token, String projName) throws DbxException, IOException, ClassNotFoundException
    {
        DbxRequestConfig req_conf = new DbxRequestConfig(projName, Locale.getDefault().toString()); 
        client = new DbxClient(req_conf, auth_token);
        nLevels = new ArrayList<DbxEntry>();
        createHash();
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
    private void getAllFolders(String dir) throws DbxException
    {
        DbxEntry.WithChildren root = client.getMetadataWithChildren(dir);
        for(DbxEntry ent : root.children)
        {
            if(ent.isFolder() && !(folderHash.containsKey(ent.path)))
            {
                System.out.println("Folder added: " + ent.path);
                allFolderNamesRelativePaths.add(ent.name);
                allFolderNames.add(ent.path);
                getAllFolders(ent.path);
            }
            else
            {
                continue;
            }
        }
    }
    
    public Long getFolderSize(String dir) throws DbxException
    {
        //Long l = new Long();
        fSize = new Long(0);
   
        getSizeOfFolderHelper(dir);
        //System.out.print("Folder: " + dir + " -- ");
       // System.out.println(fSize);
       return fSize;
    }
    private void getSizeOfFolderHelper(String dir) throws DbxException
    {
       DbxEntry.WithChildren root = client.getMetadataWithChildren(dir);
       
       if(root.children.isEmpty())
       {
           return;
    }
       for(DbxEntry ent : root.children)
       {
           if(folderHash.containsKey(ent.path))
           {
               fSize += folderHash.get(ent.path); //basically saves time by using previously summed lower directories
           }
          
          //should never need an else to handle adding new folders, since only files provide a size
           else if(ent.isFile())
           {
               fSize += ent.asFile().numBytes;
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
