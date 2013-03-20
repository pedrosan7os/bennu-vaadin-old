package pt.ist.bennu.vaadin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import pt.ist.bennu.core.domain.groups.BennuGroup;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.dispatch.Application;
import pt.ist.bennu.dispatch.Functionality;
import pt.ist.bennu.vaadin.annotation.VaadinViewInitializer;

import com.google.common.base.Joiner;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Link;

public abstract class BennuVaadinView extends CustomComponent implements View {
    private static Map<Class<? extends View>, Method> initializers = new HashMap<>();

    @Override
    public void enter(ViewChangeEvent event) {
        if (!initializers.containsKey(getClass())) {
            processInitializer(getClass());
        }
        Method initializer = initializers.get(getClass());
        if (initializer != null) {
            Object[] params = event.getParameters().split("/");
            try {
                initializer.invoke(this, params);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new Error(e);
            }
        }
    }

    public boolean isAllowed() {
        Functionality view = getClass().getAnnotation(Functionality.class);
        if (view != null) {
            return BennuGroup.parse(view.group()).isMember(Authenticate.getUser());
        }
        return true;
    }

    private static final void processInitializer(Class<? extends View> type) {
        for (Method method : type.getMethods()) {
            if (method.getAnnotation(VaadinViewInitializer.class) != null) {
                initializers.put(type, method);
                return;
            }
        }
        initializers.put(type, null);
    }

    public static Link link(String title, Class<? extends View> view, String... args) {
        Functionality functionality = view.getAnnotation(Functionality.class);
        String path = null;
        if (functionality != null) {
            Application application = functionality.app().getAnnotation(Application.class);
            if (application != null) {
                path = application.path() + "/" + functionality.path();
            }
        } else {
            Application application = view.getAnnotation(Application.class);
            if (application != null) {
                path = application.path();
            }
        }
        if (path == null) {
            throw new Error("Bad view configuration, must annotate with @Functionality or @Application");
        }

        if (args != null) {
            path = path + "/" + Joiner.on('/').join(args);
        }
        return new Link(title, new ExternalResource("#!" + path));
    }
}
