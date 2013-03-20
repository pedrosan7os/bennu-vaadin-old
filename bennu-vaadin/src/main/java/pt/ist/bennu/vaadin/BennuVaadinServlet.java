package pt.ist.bennu.vaadin;

import javax.servlet.ServletException;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;

public class BennuVaadinServlet extends VaadinServlet {
    private static class PortalBoostrapListener implements BootstrapListener {
        private static final String PORTAL_JS = "<script type=\"text/javascript\" src=\"bennu-portal/portal.js\"></script>";

        @Override
        public void modifyBootstrapPage(BootstrapPageResponse response) {
            // response.getDocument()
            // .body()
            // .prepend(
            // "<a href=\"#example/hello/manel\">cenas</a>"
            // + "<script type=\"text/javascript\" src=\"js/jquery-1.8.3.min.js\"></script>"
            // + "<script type=\"text/javascript\" src=\"js/jquery.ba-bbq.min.js\"></script>"
            // + "<script type=\"text/javascript\" src=\"js/portal-0.0.1.js\"></script>");
            response.getDocument().head().append(PORTAL_JS);
        }

        @Override
        public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
        }
    }

    private static BootstrapListener listener = new PortalBoostrapListener();

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException {
                event.getSession().addBootstrapListener(listener);
            }
        });
    }
}
