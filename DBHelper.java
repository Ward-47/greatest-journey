package dbhelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class for taking input from LSU's course offerings pages found at
 * http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85?OpenView&Start=1&Count=30&CollapseView
 * and exporting it as an ArrayList of courses.
 *
 * At the moment it only takes the URL and retrieves and parses out the text out
 * of the page's HTML. Still need to implement the parseText method as well as
 * document the whole thing.
 *
 * @author Zach
 */
class DBHelper {

    /**
     * Buffered reader holding the current URL
     */
    BufferedReader in;

    /**
     *  Basic unparameterized constructor
     */
    DBHelper() {
    }

    /**
     * Changes the URL associated with this instance of DBHelper
     *
     * @param URL The URL as a URL object
     */
    void loadURL(URL URL) {
        try {
            in = new BufferedReader(new InputStreamReader(URL.openStream()));
        } catch (MalformedURLException ex) {
            System.err.println("MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
        }
    }

    /**
     * Changes the URL associated with this instance of DBHelper
     * 
     * @param strURL The new URL in string format
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
     * Scrapes the page's html to get an input string of courses.
     * 
     * @return An input string of courses ready to be parsed / displayed
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
     * Parses out input text into an arraylist of courses
     * 
     * @param input An input String in the same format as LSU's course offerings pages
     * @return An arraylist of courses
     */
    ArrayList<Course> parseText(String input) {
        ArrayList courseList = new ArrayList();
        Scanner parser = new Scanner(input);
        String line, avl = "", enrlCnt = "", abbr = "", num = "", type = "", sec = "", title = "", crHr = "", time = "", days = "", room = "", building = "", special = "", instructor = "";
        String output = "";
        line = parser.nextLine();
        while (parser.hasNextLine()) {
            //Reset all values
            avl = "";
            enrlCnt = "";
            abbr = "";
            num = "";
            type = "";
            sec = "";
            title = "";
            crHr = "";
            time = "";
            days = "";
            room = "";
            building = "";
            special = "";
            instructor = "";

            //Ensure line is a course line and parse out data
            if (line.matches("^[(\\d].*")) {
                avl = line.substring(0, 3).trim();
                enrlCnt = line.substring(4, 9).trim();
                abbr = line.substring(10, 14).trim();
                num = line.substring(15, 19).trim();
                type = line.substring(20, 25).trim();
                sec = line.substring(26, 30).trim();
                title = line.substring(31, 53).trim();
                crHr = line.substring(54, 58).trim();
                time = line.substring(59, 70).trim();
                days = line.substring(71, 77).trim();
                room = line.substring(78, 82).trim();
                building = line.substring(83, 98).trim();
                special = line.substring(99, 105).trim();
                instructor = line.substring(106).trim();

                //split the timne into a start and end time
                String[] split = time.split("-");
                int starthours = 0, endhours = 0, startminutes = 0, endminutes = 0;

                //Night class: Add 12 hours to start or end time
                if (split[1].charAt(split[1].length() - 1) == 'N') {
                    if (split[0].length() == 4) {
                        starthours = Integer.parseInt(split[0].substring(0, 1)) + 12;
                        startminutes = Integer.parseInt(split[0].substring(2, 3));
                    } else if (split[0].length() == 3) {
                        starthours = Integer.parseInt(split[0].substring(0, 0)) + 12;
                        startminutes = Integer.parseInt(split[0].substring(1, 2));
                    }

                    //Delete the night class marker
                    split[1] = split[1].replace("N", "");

                    if (split[1].length() == 4) {
                        endhours = Integer.parseInt(split[0].substring(0, 1)) + 12;
                        endminutes = Integer.parseInt(split[0].substring(2, 3));
                    } else if (split[1].length() == 3) {
                        endhours = Integer.parseInt(split[0].substring(0, 0)) + 12;
                        endminutes = Integer.parseInt(split[0].substring(1, 2));
                    }

                } //Not a night class 
                else {
                    if (split[0].length() == 4) {
                        starthours = Integer.parseInt(split[0].substring(0, 1));
                        startminutes = Integer.parseInt(split[0].substring(2, 3));
                    } else if (split[0].length() == 3) {
                        starthours = Integer.parseInt(split[0].substring(0, 0));
                        startminutes = Integer.parseInt(split[0].substring(1, 2));
                    }

                    if (split[1].length() == 4) {
                        endhours = Integer.parseInt(split[0].substring(0, 1)) + 12;
                        endminutes = Integer.parseInt(split[0].substring(2, 3));
                    } else if (split[1].length() == 3) {
                        endhours = Integer.parseInt(split[0].substring(0, 0)) + 12;
                        endminutes = Integer.parseInt(split[0].substring(1, 2));
                    }
                    
                    if(startminutes == 50)
                    {
                        starthours++;
                        startminutes = 0;
                    }
                    if(endminutes == 50)
                    {
                        endhours++;
                        endhours = 0;
                    }
                    if(startminutes == 20)
                        startminutes = 30;
                    if(endminutes == 20)
                        endminutes = 30;

                    //starts/ends afternoon add 12 hours
                    if (starthours < 7) {
                        starthours += 12;
                    }
                    if (endhours <= 7) {
                        endhours += 12;
                    }
                }
                
                //Create localtime objects to feed into Time objects
                LocalTime localStart = LocalTime.of(starthours, startminutes);
                LocalTime localEnd = LocalTime.of(endhours, endminutes);

                //Create Time objects compatable with SchedulePainter
                Time start = new Time(starthours, startminutes, 0);
                Time end = new Time(endhours, endminutes, 0);
                Course newCourse = new Course(abbr, num, type, sec, title, crHr, start, end, days, room, building, special, instructor);
                courseList.add(newCourse);
            }

        }

        return courseList;
    }

//Just some garbage test to make sure the above methods work
    public static void main(String args[]) throws MalformedURLException, FileNotFoundException {

        URL[] allURL = {
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/828f62c3b11e2bc286258252002c2544?OpenDocument"), //ACCOUNTING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/e17ea9fb2fb3d2ae86258252002c2568?OpenDocument"), //AEROSPACE STUDIES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/4278fb99e677044186258252002c2539?OpenDocument"), //AFRICAN & AFRICAN-AMERICAN STUD
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/6bceaa70b3272c8c86258252002c254b?OpenDocument"), //AGRIC & EXTENSION ED & EVALUAT
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/e9a27e3a5315bd6786258252002c254c?OpenDocument"), //AGRICULTURAL ECONOMICS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/039c4edf6e3a164e86258252002c254e?OpenDocument"), //AGRICULTURE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/32d14753374107db86258252002c2553?OpenDocument"), //AGRONOMY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/b0622e8cf37def1c86258252002c2556?OpenDocument"), //ANIMAL SCIENCES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/ed12c9d9198032fb86258252002c2559?OpenDocument"), //ANTHROPOLOGY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/a7aee170d4283e9386258252002c255c?OpenDocument"), //ARABIC
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/e656e1036a9a9f8f86258252002c255d?OpenDocument"), //ARCHITECTURE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/9f2b5fb860f7e63586258252002c2562?OpenDocument"), //ART
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/3a66deed6b88c30c86258252002c2565?OpenDocument"), //ART HISTORY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/4a9890aec720121686258252002c256a?OpenDocument"), //ASTRONOMY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/14be38883d28f91586258252002c256d?OpenDocument"), //ATHLETIC TRAINING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/bc38479f0f834c8486258252002c2572?OpenDocument"), //BASIC SCIENCES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/1ece310547e5198c86258252002c2573?OpenDocument"), //BIOLOGICAL ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/8ba05ccb39dbd0a786258252002c257b?OpenDocument"), //BIOLOGICAL SCIENCES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/e43a7336906523b686258252002c256f?OpenDocument"), //BUSINESS ADMINISTRATION
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/fe1f789b46f3409586258252002c2580?OpenDocument"), //BUSINESS LAW
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/470cd8d31ecb7e0d86258252002c2599?OpenDocument"), //CHEMICAL ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/df6140b622df1fba86258252002c25a4?OpenDocument"), //CHEMISTRY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/9b53fcd209dbb82a86258252002c2596?OpenDocument"), //CHILD AND FAMILY STUDIES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/4f584f6e1bdc7d0686258252002c25ad?OpenDocument"), //CHINESE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/f029141c6d89fa2f86258252002c258e?OpenDocument"), //CIVIL ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/3b548780c909ff7786258252002c25b0?OpenDocument"), //CLASSICAL STUDIES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/1a151a1489344a5f86258252002c25bd?OpenDocument"), //COMMUNICATION DISORDERS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/720f3f3172efc00686258252002c25ba?OpenDocument"), //COMMUNICATION STUDIES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/53761e8096cf5fc986258252002c2581?OpenDocument"), //COMPARATIVE BIOMEDICAL SCIENCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/1159422d2935807a86258252002c25be?OpenDocument"), //COMPARATIVE LITERATURE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/62cdc1025f30735686258252002c25c6?OpenDocument"), //COMPUTER SCIENCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/882f9df15fc2772b86258252002c25b5?OpenDocument"), //CONSTRUCTION MANAGEMENT
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/a224f810b872769086258252002c25d3?OpenDocument"), //CURRICULUM & INSTRUCTION
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/821d756a7db9ff9786258252002c25cb?OpenDocument"), //DIGITAL MEDIA ARTS & ENGINEERI
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/198c2c32976196ab86258252002c25c8?OpenDocument"), //DOCTOR OF DESIGN
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/707244fc61548e4e86258252002c25cc?OpenDocument"), //ECONOMICS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/7ee9816e02656d1586258252002c25d9?OpenDocument"), //EDUC LEADERSHIP RESEARCH COUNS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/92e27ab256cb0af086258252002c25d6?OpenDocument"), //ELECTRICAL ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/ed0f98bf3792f38086258252002c25e7?OpenDocument"), //ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/e9d1a9a883764f2886258252002c25e4?OpenDocument"), //ENGLISH
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/b00e964f3fa4091e86258252002c25ea?OpenDocument"), //ENTOMOLOGY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/572c998bba17a4c986258252002c25ed?OpenDocument"), //ENTREPRENEURSHIP
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/2eef146029f4ff5c86258252002c25f2?OpenDocument"), //ENVIRONMENTAL ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/f6087be264d4eb1e86258252002c25dc?OpenDocument"), //ENVIRONMENTAL MANAGEMENT SYSTE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/61404a1ea987ddc986258252002c25f0?OpenDocument"), //ENVIRONMENTAL STUDIES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/727154eaf47ca2c286258252002c25f8?OpenDocument"), //EXPERIMENTAL STATISTICS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/9588dc56341925fc86258252002c25fd?OpenDocument"), //FINANCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/5524c4d0bd46371986258252002c25fe?OpenDocument"), //FRENCH
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/c82bffbcdbdb4eff86258252002c2602?OpenDocument"), //GENERAL BUSINESS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/bdf10bd3f8fa618c86258252002c2605?OpenDocument"), //GEOGRAPHY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/a25659514d39a00086258252002c2608?OpenDocument"), //GEOLOGY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/001ae36691c62fed86258252002c260b?OpenDocument"), //GERMAN
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/b31a31bb47f473dd86258252002c260c?OpenDocument"), //GREEK
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/672fc86388cb4c7286258252002c260e?OpenDocument"), //HEBREW
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/ef644a9b763a42d986258252002c2611?OpenDocument"), //HISTORY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/c5dbe1942a8d808e86258252002c2614?OpenDocument"), //HONORS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/39fb84a5ab53e0a986258252002c2616?OpenDocument"), //HORTICULTURE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/476c664f44ec654d86258252002c261e?OpenDocument"), //INDUSTRIAL ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/7781e3092f6b68bb86258252002c2622?OpenDocument"), //INFO SYSTEMS AND DECISION SCIE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/cf844095942a74ee86258252002c261b?OpenDocument"), //INTERIOR DESIGN
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/dda65eabba3543ca86258252002c261f?OpenDocument"), //INTERNATIONAL STUDIES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/8fda90a81b143aa486258252002c2624?OpenDocument"), //ITALIAN
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/bf599682c49c5af486258252002c262a?OpenDocument"), //KINESIOLOGY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/138f9a6377c6e10786258252002c262d?OpenDocument"), //LANDSCAPE ARCHITECTURE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/d58db4c8eaa2a40a86258252002c263a?OpenDocument"), //LATIN
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/473d4550120561fd86258252002c263d?OpenDocument"), //LEADERSHIP & HUMAN RESOURCES D
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/94c18d07ae9f9bcc86258252002c2640?OpenDocument"), //LIBERAL ARTS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/5cd2e5851d4c777786258252002c2643?OpenDocument"), //LIBRARY & INFORMATION SCIENCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/fc8296d50c0d471a86258252002c263b?OpenDocument"), //LIFE COURSE & AGING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/9398d6bceef5855486258252002c2642?OpenDocument"), //LINGUISTICS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/b132a7a03d441cad86258252002c2656?OpenDocument"), //MANAGEMENT
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/8d0142f3bc0f5b1586258252002c265b?OpenDocument"), //MARKETING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/ca295c95c2a5703d86258252002c264e?OpenDocument"), //MASS COMMUNICATION
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/465493d09e5b703086258252002c2649?OpenDocument"), //MATHEMATICS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/7f86e7502736bcd686258252002c2651?OpenDocument"), //MECHANICAL ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/03b2559fa45f6f7e86258252002c2654?OpenDocument"), //MEDICAL PHYSICS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/edfb69afde2555e286258252002c2659?OpenDocument"), //MILITARY SCIENCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/9cab963fa671e6a986258252002c2662?OpenDocument"), //MUSIC
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/83dec385628e74e786258252002c265c?OpenDocument"), //MUSIC EDUCATION
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/91efd24c2156f85986258252002c2667?OpenDocument"), //NUTRITION & FOOD SCIENCES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/34784fc8adf0828286258252002c2669?OpenDocument"), //OCEANOGRAPHY & COASTAL SCIENCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/f04b76f472ea7acd86258252002c266d?OpenDocument"), //PATHOBIOLOGICAL SCIENCES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/6df2f6b41033da3186258252002c2672?OpenDocument"), //PETROLEUM ENGINEERING
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/4ec00a6ced2fc7f486258252002c2674?OpenDocument"), //PHILOSOPHY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/8bc0b3625add04a586258252002c2677?OpenDocument"), //PHYSICAL SCIENCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/86703858cb3fe21a86258252002c267b?OpenDocument"), //PHYSICS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/7689eb29e8ec871586258252002c267f?OpenDocument"), //PLANT HEALTH
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/268a3fa3efdee98586258252002c2680?OpenDocument"), //POLITICAL SCIENCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/aa25082f8b14741986258252002c2685?OpenDocument"), //PSYCHOLOGY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/5fcde11ead9b247086258252002c266c?OpenDocument"), //PUBLIC ADMINISTRATION
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/bd85f1d43eb288fe86258252002c2686?OpenDocument"), //RELIGIOUS STUDIES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/f799ddddd82ecafa86258252002c268b?OpenDocument"), //RENEWABLE NATURAL RESOURCES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/5bf4a53bcddf2a1886258252002c268d?OpenDocument"), //SCREEN ARTS
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/4ea34b9d54587e6586258252002c269b?OpenDocument"), //SOCIAL WORK
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/87eeebc5ed45316086258252002c2691?OpenDocument"), //SOCIOLOGY
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/2f43f14821c9aade86258252002c2694?OpenDocument"), //SPANISH
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/1f45b337898492d986258252002c2696?OpenDocument"), //STUDENT SUPPORT SERVICES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/88edc35614fedc3286258252002c269c?OpenDocument"), //TEXTILES,APPAREL & MERCHANDISI
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/e53d786f17c6e86386258252002c269f?OpenDocument"), //THEATRE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/a9aa3cf84a15d3b986258252002c26a1?OpenDocument"), //UNIVERSITY STUDIES
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/d04e2ba23557e0f186258252002c26a2?OpenDocument"), //VETRINARY CLINICAL SCIENCE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/aacddf740840abf486258252002c26a6?OpenDocument"), //VETERINARY MEDICINE
            new URL("http://appl101.lsu.edu/booklet2.nsf/bed33d8925ab561b8625651700585b85/9562574cfef6900686258252002c26a9?OpenDocument"), //WOMAN'S AND GENDER STUDIES
        };
        String[] allName = {"ACCOUNTING", "AEROSPACE STUDIES", "AFRICAN & AFRICAN-AMERICAN STU", "AGRIC & EXTENSION ED & EVALUAT", "AGRICULTURAL ECONOMICS", "AGRICULTURE", "AGRONOMY",
            "ANIMAL SCIENCES", "ANTHROPOLOGY", "ARABIC", "ARCHITECTURE", "ART", "ART HISTORY", "ASTRONOMY", "ATHLETIC TRAINING", "BASIC SCIENCES", "BIOLOGICAL ENGINEERING", "BIOLOGICAL SCIENCES",
            "BUSINESS ADMINISTRATION", "BUSINESS LAW", "CHEMICAL ENGINEERING", "CHEMISTRY", "CHILD AND FAMILY STUDIES", "CHINESE", "CIVIL ENGINEERING", "CLASSICAL STUDIES", "COMMUNICATION DISORDERS",
            "COMMUNICATION STUDIES", "COMPARATIVE BIOMEDICAL SCIENCE", "COMPARATIVE LITERATURE", "COMPUTER SCIENCE", "CONSTRUCTION MANAGEMENT", "CURRICULUM & INSTRUCTION", "DIGITAL MEDIA ARTS & ENGINEERI",
            "DOCTOR OF DESIGN", "ECONOMICS", "EDUC LEADERSHIP RESEARCH COUNS", "ELECTRICAL ENGINEERING", "ENGINEERING", "ENGLISH", "ENTOMOLOGY", "ENTREPRENEURSHIP", "ENVIRONMENTAL ENGINEERING",
            "ENVIRONMENTAL MANAGEMENT SYSTE", "ENVIRONMENTAL SCIENCES", "EXPERIMENTAL STATISTICS", "FINANCE", "FRENCH", "GENERAL BUSINESS", "GEOGRAPHY", "GEOLOGY", "GERMAN", "GREEK",
            "HEBREW", "HISTORY", "HONORS", "HORTICULTURE", "INDUSTRIAL ENGINEERING", "INFO SYSTEMS AND DECISION SCIE", "INTERIOR DESIGN", "INTERNATIONAL STUDIES", "ITALIAN", "KINESIOLOGY",
            "LANDSCAPE ARCHITECTURE", "LATIN", "LEADERSHIP & HUMAN RESOURCES D", "LIBERAL ARTS", "LIBRARY & INFORMATION SCIENCE", "LIFE COURSE AND AGING", "LINGUISTICS", "MANAGEMENT",
            "MARKETING", "MASS COMMUNICATION", "MATHEMATICS", "MECHANICAL ENGINEERING", "MEDICAL PHYSICS", "MILITARY SCIENCE", "MUSIC", "MUSIC EDUCATION", "NUTRITION & FOOD SCIENCES",
            "OCEANOGRAPHY & COASTAL SCIENCE", "PATHOBIOLOGICAL SCIENCES", "PETROLEUM ENGINEERING", "PHILOSOPHY", "PHYSICAL SCIENCE", "PHYSICS", "PLANT HEALTH", "POLITICAL SCIENCE", "PSYCHOLOGY",
            "PUBLIC ADMINISTRATION", "RELIGIOUS STUDIES", "RENEWABLE NATURA L RESOURCES", "SCREEN ARTS", "SOCIAL WORK", "SOCIOLOGY", "SPANISH", "STUDENT SUPPORT SERVICES",
            "TEXTILES,APPAREL & MERCHANDISI", "THEATRE", "UNIVERSITY STUDIES", "VETERINARY CLINICAL SCIENCES", "VETERINARY MEDICINE", "WOMEN'S AND GENDER STUDIES"};
        /*
         PrintWriter out = new PrintWriter("export.json"); out.printf("" +
         "{\n" + " \"Courses\" : {\n"); DBHelper DBExport = new DBHelper();
         for (int i = 0; i < 3; i++) { DBExport.loadURL(allURL[i]);
         out.printf(" \"%s\" : {\n", allName[i]); String txt =
         DBExport.getText(); out.print(DBExport.parseText(txt)); out.printf("
         },\n"); } out.printf("" + " }\n" + "}\n"); out.close();
         */
    }
}
