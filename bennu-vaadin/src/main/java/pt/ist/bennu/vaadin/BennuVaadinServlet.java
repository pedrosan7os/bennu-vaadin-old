package pt.ist.bennu.vaadin;

import java.util.Collections;

import javax.servlet.ServletException;

import org.jsoup.nodes.Element;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;

public class BennuVaadinServlet extends VaadinServlet {
    private BootstrapListener listener;

    private static class PortalBoostrapListener implements BootstrapListener {
        private static final String[] JS = new String[] { "/js/libs/jquery/jquery.js",
                "/js/libs/mustache/mustache-min.js", "/bennu-portal/portal.js" };

        private static final String JS_FORMAT = "<script type=\"text/javascript\" src=\"%s\"></script>";
        private static final String LINK_FORMAT = "<link rel=\"stylesheet/less\" href=\"%s\" />";

        private final String contextPath;

        public PortalBoostrapListener(String contextPath) {
            this.contextPath = contextPath;
        }

        @Override
        public void modifyBootstrapPage(BootstrapPageResponse response) {
            final Element head = response.getDocument().head();

            for (String js : JS) {
                head.append(String.format(JS_FORMAT, contextPath + js));
            }
            response.getDocument().body().classNames(Collections.EMPTY_SET);
        }

        @Override
        public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
        }

    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        listener = new PortalBoostrapListener(getServletContext().getContextPath());
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException {
                event.getSession().addBootstrapListener(listener);
            }
        });
    }
}
