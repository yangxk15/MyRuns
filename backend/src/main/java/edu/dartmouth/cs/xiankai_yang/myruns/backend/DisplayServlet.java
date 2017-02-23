package edu.dartmouth.cs.xiankai_yang.myruns.backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.xiankai_yang.myruns.backend.data.ExerciseEntry;

import static edu.dartmouth.cs.xiankai_yang.myruns.backend.OfyService.ofy;

/**
 * Created by yangxk15 on 2/23/17.
 */

public class DisplayServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.write(
            "<html>" +
                    "<head>" +
                        "<style>" +
                            "table, th, td {" +
                                "border: 1px solid black;" +
                                "border-collapse: collapse;" +
                            "} " +
                            "th, td {" +
                                "padding: 5px;" +
                            "} " +
                            "th { " +
                                "text-align: left;" +
                            "} " +
                        "</style>" +
                    "</head>" +
                    "<body>" +
                        "<table style=\"width:100%\">" +
                            "<tr>" +
                                "<th>ID</th>" +
                                "<th>Input Type</th>" +
                                "<th>Activity Type</th>" +
                                "<th>Date Time</th>" +
                                "<th>Duration</th>" +
                                "<th>Distance</th>" +
                                "<th>Average Pace</th>" +
                                "<th>Average Speed</th>" +
                                "<th>Calories</th>" +
                                "<th>Climb</th>" +
                                "<th>Heart Rate</th>" +
                                "<th>Comment</th>" +
                                "<th>Delete</th>" +
                            "</tr>");

        List<ExerciseEntry> exerciseEntryList = ofy().load().type(ExerciseEntry.class).list();
        for (ExerciseEntry exerciseEntry : exerciseEntryList) {
            out.write("<tr>");
            out.write("<th>" + exerciseEntry.getId() + "</th>");
            out.write("<th>" + exerciseEntry.getInputType() + "</th>");
            out.write("<th>" + exerciseEntry.getActivityType() + "</th>");
            out.write("<th>" + exerciseEntry.getDateTime() + "</th>");
            out.write("<th>" + exerciseEntry.getDuration() + "</th>");
            out.write("<th>" + exerciseEntry.getDistance() + "</th>");
            out.write("<th>" + exerciseEntry.getAvgPace() + "</th>");
            out.write("<th>" + exerciseEntry.getAvgSpeed() + "</th>");
            out.write("<th>" + exerciseEntry.getCalorie() + "</th>");
            out.write("<th>" + exerciseEntry.getClimb() + "</th>");
            out.write("<th>" + exerciseEntry.getHeartRate() + "</th>");
            out.write("<th>" + exerciseEntry.getComment() + "</th>");
            out.write("<th><a href=\"/delete?id=" + exerciseEntry.getId() + "\">delete</a></th>");
            out.write("</tr>");
        }

        out.write(
                        "</table>" +
                    "</body>" +
            "</html>"
        );

        req.getRequestDispatcher("history.html").forward(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}
