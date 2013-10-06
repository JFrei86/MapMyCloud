package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import src.DropBoxHandler;

public class HTTPClient {
    
	public static String authorize(String app_key, String app_secret) throws IOException{
		String userLocale = Locale.getDefault().toString();
		DbxAppInfo appInfo = new DbxAppInfo(app_key, app_secret);
        DbxRequestConfig requestConfig = new DbxRequestConfig("examples-authorize", userLocale);
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(requestConfig, appInfo);

        String authorizeUrl = webAuth.start();
        System.out.println(authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first).");
        System.out.println("3. Copy the authorization code.");
        System.out.print("Enter the authorization code here: ");

        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (code == null) {
            System.exit(1); return "";
        }
        code = code.trim();

        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finish(code);
        }
        catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.start: " + ex.getMessage());
            System.exit(1); return "";
        }

        System.out.println("Authorization complete.");
        System.out.println("- User ID: " + authFinish.userId);
        System.out.println("- Access Token: " + authFinish.accessToken);
        return authFinish.accessToken;
	}

        public static void printArr(ArrayList<DbxEntry> files, int n)
        {
                for(DbxEntry ent : files)
                {
                   if(ent.isFolder())
                   {
                       System.out.println("Folder: " + ent.asFolder().name);
                   }
                   else
                   {
                    System.out.println("File: " + ent.asFile().name + ": " + ent.asFile().humanSize);
                   }
                }
        }
	public static void main(String[] args) throws IOException, DbxException {
		String auth_token = HTTPClient.authorize("k43q7eqwl6jialx", "k67mwnb3jjr8x6y");
                DropBoxHandler dbhandler = new DropBoxHandler(auth_token,"testinghackmit");
                ArrayList<DbxEntry> files = dbhandler.getFilesInDir("/",1);
                for(DbxEntry ent : files)
                {
                   if(ent.isFolder())
                   {
                       System.out.println("Folder: " + ent.asFolder().name);
                   }
                   else{
                    System.out.println("File: " + ent.asFile().name + " -- " + ent.asFile().humanSize);
                   }
                }
                dbhandler.getQuota();
                
                
	}

}
