/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import org.apache.commons.lang3.StringUtils;
import java.util.Comparator;

public class customComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) { //more / means deeper!
        if(StringUtils.countMatches(o1, "/") > StringUtils.countMatches(o2, "/")){
            return 1;
    }
        if(StringUtils.countMatches(o1, "/") == StringUtils.countMatches(o2, "/"))
        {
            return 0;
        }
        
        return -1;
    }
}