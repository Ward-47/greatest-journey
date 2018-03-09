package dbhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

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
        Scanner parser = new Scanner(input);
        String start = "", avl = "", enrlCnt = "", abbr = "", num = "", type = "", sec = "", title = "", crHr = "", time = "", days = "", room = "", building = "", special = "", instructor = "", note = "", temp = "";
        String output = String.format("{\n");
        while (parser.hasNextLine()) {
            start = parser.next();
            parser.useDelimiter("\\s+");
            if (start.matches("^\\s*[(\\d].*")) {
                avl = start;
                enrlCnt = parser.next();
                abbr = parser.next();
                num = parser.next();
                if (parser.hasNext("\\S+\\s+\\d+")) {
                    type = parser.next();
                } else {
                    type = "";
                }
                sec = parser.next();
                parser.useDelimiter("\\d[.-]");
                title = parser.next();
                parser.useDelimiter("\\s+");
                crHr = parser.next();
                time = parser.next();
                if (time.equals("TBA")) {
                    days = "TBA";
                    room = "TBA";
                    building = "TBA";
                } else {
                    parser.useDelimiter("\\d+");
                    days = parser.next();
                    parser.useDelimiter("\\s+");
                    room = parser.next();

                    parser.useDelimiter("\\s{2,}");
                    building = parser.next();
                    parser.useDelimiter("\\s+");
                }
                temp = parser.nextLine();
                if (temp.trim().matches("^\\S* [A-Z]|^")) {
                    instructor = temp;
                } else {
                    String[] temp2 = temp.split("\\s{2,}");
                    special = temp2[0];
                    instructor = temp2[1];
                }

                if (parser.hasNext("\\s*[*]+")) {
                    note = parser.nextLine();
                }
            }
            output += String.format(
                    "  \"%s\" : {\n"
                    + "    \"Building\" : \"%s\",\n"
                    + "    \"Credit Hours\" : \"%s\",\n"
                    + "    \"Days\" : \"%s\",\n"
                    + "    \"Instructor\" : \"%s\",\n"
                    + "    \"Note\" : \"%s\",\n"
                    + "    \"Time\" : \"%s\".\n"
                    + "    \"Title\" : \"%s\",\n"
                    + "    \"Type\" : \"%s\",\n"
                    + "  }", num.trim(), building.trim(), crHr.trim(), days.trim(), instructor.trim(), note.trim(), time.trim(), title.trim(), type.trim());

        }

        return output += "}";
    }

    //Just some garbage test to make sure the above methods work
    public static void main(String args[]) {
        DBHelper a = new DBHelper();
        a.loadURL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/83ca4fa66b71a33a862581b40002423f?OpenDocument");
        String ab = a.getText();
        System.out.println();
        System.out.println();
        System.out.println(a.parseText(ab));

    }

}
