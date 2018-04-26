package easyscheduleworking;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import javax.imageio.ImageIO;

class SchedulePainter {

    ArrayList<Course> courses = new ArrayList<>();

    SchedulePainter(ArrayList<Course> someCourses) {
        for (int i = 0; i < someCourses.size(); i++) {
            courses.add(someCourses.get(i));
        }
    }

    SchedulePainter() {
        courses = new ArrayList<>();
    }

    void addCourse(Course aCourse) {
        courses.add(aCourse);
    }
    
    void removeCourse(Course aCourse)
    {
        courses.remove(aCourse);
    }
    
    void removeCourse(int i)
    {
        courses.remove(i);
    }
    
    Course getCourse(int i)
    {
        return courses.get(i);
    }
    
    Color randomColor()
    {
        int R = (int) (Math.random() * 156 + 100);
        int G = (int) (Math.random() * 156 + 100);
        int B = (int) (Math.random() * 156 + 100);
        return new Color(R, G, B);
    }

    void printSchedule() throws IOException {
        File file = new File("C:\\Users\\fortg\\OneDrive\\Documents\\NetBeansProjects\\EasyScheduleWorking\\blank schedule.png");
        Image blank = ImageIO.read(file);
        BufferedImage bi = new BufferedImage(900, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        g.drawImage(blank, 0, 0, null);
        for (int i = 0; i < courses.size(); i++) {
            Color color = randomColor();
            g.setColor(color);
            int y = (int) ((courses.get(i).getStart().getTime() - new Time(7, 0, 0).getTime()) / 61017) + 99;
            int height = (int) (courses.get(i).getTime() * .98333333333);
            ArrayList<Integer> x = new ArrayList<>();
            if (courses.get(i).getDays().contains("M")) {
                x.add(95);
            }
            if (courses.get(i).getDays().contains("T") && courses.get(i).getDays().indexOf("T") <= 1 && courses.get(i).getDays().charAt(0) != 'W') {
                x.add(248);
            }
            if (courses.get(i).getDays().contains("W")) {
                x.add(401);
            }
            if (courses.get(i).getDays().contains("T") && courses.get(i).getDays().substring(courses.get(i).getDays().indexOf("T")).contains("T")) {
                x.add(554);
            }
            if (courses.get(i).getDays().contains("F")) {
                x.add(707);
            }
            for (int j = 0; j < x.size(); j++) {
                g.fillRect(x.get(j), y, 153, height);
                g.setColor(Color.black);
                g.drawRect(x.get(j), y, 153, height);
                g.drawString(courses.get(i).getName(), x.get(j) + 10, y + 20);
                g.drawString(courses.get(i).getAbbr()+" "+courses.get(i).getNum(), x.get(j) + 10, y + 32);
                g.drawString(courses.get(i).getProf(), x.get(j) + 10, y + 44);
                g.setColor(color);
            }
        }
        ImageIO.write(bi, "PNG", new File("new.PNG"));
    }
}
