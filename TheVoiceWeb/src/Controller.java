import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


//TODO: Check what impact keeping FileModel unchanged in a session has.
public class Controller extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        String command = request.getServletPath();
        FileModel fileModel;
        if(command.equals("/showjobs.do") || command.equals("/")) {
            fileModel = new FileModel();

            session.setAttribute("fileModel", fileModel);
            getServletContext().getRequestDispatcher("/showjobs.jsp").forward(request,response);
        }
        else if(command.equals("/edit.do")) {
            fileModel = (FileModel)session.getAttribute("fileModel");
            if(fileModel == null) {
                fileModel = new FileModel();
                session.setAttribute("fileModel", fileModel);
            }
            String file = request.getParameter("file");
            if(file != null) {
                fileModel.loadFile(file);
                session.setAttribute("fileModel", fileModel);
            }


            getServletContext().getRequestDispatcher("/edit.jsp").forward(request,response);
        }
        else if(command.equals("/delete.do")) {
            //TODO: Put into method
            fileModel = (FileModel)session.getAttribute("fileModel");
            if(fileModel == null) {
                fileModel = new FileModel();
                session.setAttribute("fileModel", fileModel);
            }
            String file = request.getParameter("file");
            if(file != null) {
                fileModel.deleteFile(file);
                session.setAttribute("fileModel", fileModel);
            }
            response.sendRedirect("/showjobs.do");
        }
        else if(command.equals("/new.do")) {
            fileModel = (FileModel)session.getAttribute("fileModel");
            if(fileModel == null) {
                fileModel = new FileModel();
                session.setAttribute("fileModel", fileModel);
            }
            getServletContext().getRequestDispatcher("/new.jsp").forward(request,response);
        } else if(command.equals("/viewfiles.do")) {
            fileModel = (FileModel)session.getAttribute("fileModel");
            if(fileModel == null) {
                fileModel = new FileModel();
                session.setAttribute("fileModel", fileModel);
            }
            getServletContext().getRequestDispatcher("/viewfiles.jsp").forward(request,response);
        } else if(command.equals("/time.do")) {
            fileModel = (FileModel)session.getAttribute("fileModel");
            if(fileModel == null) {
                fileModel = new FileModel();
                session.setAttribute("fileModel", fileModel);
            }
            getServletContext().getRequestDispatcher("/time.jsp").forward(request,response);
        } else if(command.equals("/manual.do")) {
            fileModel = (FileModel)session.getAttribute("fileModel");
            if(fileModel == null) {
                fileModel = new FileModel();
                session.setAttribute("fileModel", fileModel);
            }
            getServletContext().getRequestDispatcher("/manual.jsp").forward(request,response);
        }
        //TODO: This really needs to be fixed - style isn't a jsp file, but a static resource.
        else {
            System.out.println(command + " didn't hit anything.");
        }
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        String command = request.getServletPath();
        FileModel fileModel = (FileModel)session.getAttribute("fileModel");
        if(command.equals("/change.do")) {
            if(fileModel == null) {
                fileModel = new FileModel();
                session.setAttribute("fileModel", fileModel);
            }
            // Save content of the file
            String file = request.getParameter("file");
            String content = request.getParameter("fileContent");
            String referer = request.getParameter("referer");
            fileModel.saveFile(file, content);
            session.setAttribute("fileModel", fileModel);

            // Checking for time overlap with the existing jobs


            //getServletContext().getRequestDispatcher("/showjobs.do").forward(request,response);
            response.sendRedirect("/edit.do?file="+file + "&changed=done");
        }


    }
}
