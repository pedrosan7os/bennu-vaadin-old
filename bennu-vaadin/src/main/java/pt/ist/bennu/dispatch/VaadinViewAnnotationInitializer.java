package pt.ist.bennu.dispatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import pt.ist.bennu.dispatch.model.ApplicationInfo;
import pt.ist.bennu.dispatch.model.BundleDetails;
import pt.ist.bennu.dispatch.model.FunctionalityInfo;
import pt.ist.bennu.vaadin.BennuVaadinView;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;

@HandlesTypes({ Functionality.class })
public class VaadinViewAnnotationInitializer implements ServletContainerInitializer {
    private static Map<String, Class<? extends View>> views = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup(Set<Class<?>> classes, ServletContext context) throws ServletException {
        if (classes != null) {
            Map<Class<?>, ApplicationInfo> apps = new HashMap<>();
            for (Class<?> type : classes) {
                Functionality functionality = type.getAnnotation(Functionality.class);
                if (functionality != null && View.class.isAssignableFrom(type)) {
                    extractFunctionality(apps, functionality);
                    views.put(functionality.app().getAnnotation(Application.class).path() + "/" + functionality.path(),
                            (Class<? extends View>) type);
                }
            }
            for (ApplicationInfo application : apps.values()) {
                AppServer.registerApp(application);
            }
        }
    }

    public void extractFunctionality(Map<Class<?>, ApplicationInfo> apps, Functionality functionality) {
        if (!apps.containsKey(functionality.app())) {
            extractApp(apps, functionality.app());
        }
        final ApplicationInfo applicationInfo = apps.get(functionality.app());
        final String path = applicationInfo.getPath() + "/" + functionality.path();
        applicationInfo.addFunctionality(new FunctionalityInfo(path, functionality.group(), new BundleDetails(functionality
                .bundle(), functionality.title(), functionality.description())));
    }

    @SuppressWarnings("unchecked")
    private void extractApp(Map<Class<?>, ApplicationInfo> apps, Class<?> app) {
        Application application = app.getAnnotation(Application.class);
        if (application != null) {
            apps.put(
                    app,
                    new ApplicationInfo("vaadin#" + application.path(), application.group(), new BundleDetails(application
                            .bundle(), application.title(), application.description())));
            views.put(application.path(), (Class<? extends View>) app);
        } else {
            throw new Error();
        }
    }

    public static void initializeNavigator(Navigator navigator) {
        for (Entry<String, Class<? extends View>> view : views.entrySet()) {
            navigator.addView(view.getKey(), view.getValue());
        }
        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                if (event.getNewView() instanceof BennuVaadinView) {
                    return ((BennuVaadinView) event.getNewView()).isAllowed();
                }
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
            }
        });
    }
}
