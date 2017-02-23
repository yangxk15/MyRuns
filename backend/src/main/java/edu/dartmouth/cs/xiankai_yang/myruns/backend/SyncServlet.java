package edu.dartmouth.cs.xiankai_yang.myruns.backend;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.xiankai_yang.myruns.backend.data.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.backend.data.ExerciseEntryDomain;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by yangxk15 on 2/22/17.
 */

public class SyncServlet extends HttpServlet implements ExerciseEntryDomain {
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String localEntries = req.getParameter("local_entries");

        ofy().delete().keys(ofy().load().type(ExerciseEntry.class).keys().list()).now();

        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) new JSONParser().parse(localEntries);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            ofy().save().entity(new ExerciseEntry(
                    (String) jsonObject.get(_ID),
                    (String) jsonObject.get(_INPUT_TYPE),
                    (String) jsonObject.get(_ACTIVITY_TYPE),
                    (String) jsonObject.get(_DATE_TIME),
                    (String) jsonObject.get(_DURATION),
                    (String) jsonObject.get(_DISTANCE),
                    (String) jsonObject.get(_AVG_PACE),
                    (String) jsonObject.get(_AVG_SPEED),
                    (String) jsonObject.get(_CALORIES),
                    (String) jsonObject.get(_CLIMB),
                    (String) jsonObject.get(_HEART_RATE),
                    (String) jsonObject.get(_COMMENT)
            )).now();
        }

        req.getRequestDispatcher("/display").forward(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }

}
