package dbhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class for taking input from LSU's course offerings pages found at
 * http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85?OpenView&Start=1&Count=30&CollapseView
 * and exporting it as a JSON string that our database can import.
 *
 * At the moment it only takes the URL and retrieves and parses out the text out
 * of the page's HTML. Still need to implement the parseText method as well as
 * document the whole thing.
 *
 * @author Zach
 */
class DBHelper {
    /**
     * 
     */
    BufferedReader in;

    /**
     *
     */
    DBHelper() {
    }

    /**
     *
     * @param strURL
     */
    void loadURL(String strURL) {
        try {
            URL url = new URL(strURL);
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (MalformedURLException ex) {
            System.err.println("MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
        }
    }

    /**
     *
     * @return
     */
    String getText() {
        String line;
        String bodyText = "";
        try {
            while ((line = in.readLine()) != null) {
                if (!(line.matches("^\\s*[A-Z<-].*|^\\s*[*]+\\s*"))) {
                    bodyText += line + "\n";
                }
            }
            in.close();
        } catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
        }
        return bodyText.trim();
    }

    /**
     *
     * @param input
     * @return
     */
    String parseText(String input) {
        return null;
    }

    //Just some garbage test to make sure the above methods work
    public static void main(String args[]) {
        DBHelper a = new DBHelper();
        a.loadURL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/83ca4fa66b71a33a862581b40002423f?OpenDocument");
        System.out.print(a.getText());

    }

}
