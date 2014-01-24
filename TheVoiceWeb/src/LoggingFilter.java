import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter(servletNames = {"*"})
public class LoggingFilter implements Filter {
    ServletContext context;
    int counter;

    public void init(FilterConfig c) throws ServletException {
        context = c.getServletContext();
    }

    public void destroy() {}

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        String uri = ((HttpServletRequest)request).getRequestURI();
        int n = ++counter;
        context.log("starting processing request #"+n+" ("+uri+")");
        System.out.println("Filter was run on " + ((HttpServletRequest) request).getServletPath());
        long t1 = System.currentTimeMillis();
        chain.doFilter(request, response);
        long t2 = System.currentTimeMillis();
        context.log("done processing request #"+n+", "+(t2-t1)+" ms");
    }
}