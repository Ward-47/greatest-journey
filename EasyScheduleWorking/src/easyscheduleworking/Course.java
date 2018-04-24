package easyscheduleworking;


import java.sql.Time;

class Course {

    String abbr;        //CSC
    String num;         //3380
    String type;        //Lec/Lab
    String section;     
    String name;        //Object Oriented Design
    String crHr;        //Credit hours
    Time startTime;     //Time object start time
    Time endTime;       //Time object end time
    String days;        //MWF, TuTh, MTuWTh, etc
    String room;        //room#
    String building;    
    String special;     //special enrollment
    String instructor;  //Williams G
    String stringTime; //Time value as a string

    int timeMinutes;

    Course(String aCourseAbbr, String aCourseNum, String aType, String section, String aName, String crHr, Time aStart,
            Time aEnd, String days, String room, String building, String special, String aInstructor, String time) {
        this.abbr = aCourseAbbr;
        this.num = aCourseNum;
        this.type = aType;
        this.section = section;
        this.name = aName;
        this.crHr = crHr;
        this.startTime = aStart;
        this.endTime = aEnd;
        this.days = days;
        this.room = room;
        this.building = building;
        this.special = special;
        this.instructor = aInstructor;
        

        this.timeMinutes = (int) ((aEnd.getTime() - aStart.getTime()) / 60000);
    }


    String getAbbr() {
        return abbr;
    }

    String getNum() {
        return num;
    }

    String gtType() {
        return type;
    }

    String getSection() {
        return section;
    }

    String getName() {
        return name;
    }

    String getProf() {
        return instructor;
    }

    String getDays() {
        return days;
    }

    Time getStart() {
        return startTime;
    }

    double getTime() {
        return timeMinutes;
    }

    Time getEnd() {
        return endTime;
    }

    String getRoom() {
        return room;
    }

    String getBuilding() {
        return building;
    }

    String getSpecial() {
        return special;
    }

    String getHours() {
        return crHr;
    }
}
