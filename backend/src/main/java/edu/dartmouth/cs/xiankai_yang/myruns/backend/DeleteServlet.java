package edu.dartmouth.cs.xiankai_yang.myruns.backend;

import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.xiankai_yang.myruns.backend.data.ExerciseEntry;

import static edu.dartmouth.cs.xiankai_yang.myruns.backend.OfyService.ofy;

/**
 * Created by yangxk15 on 2/22/17.
 */

public class DeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String id = req.getParameter("id");
        try {
            ofy().delete().type(ExerciseEntry.class).id(id).now();
            new MessagingEndpoint().sendMessage(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        req.getRequestDispatcher("/display").forward(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}