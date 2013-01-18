package pt.ist.bennu.vaadin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.security.UserView;
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
			return PersistentGroup.parse(view.group()).isMember(UserView.getUser());
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
		return new Link(title, new ExternalResource("#!" + view.getAnnotation(Functionality.class).path() + "/"
				+ Joiner.on('/').join(args)));
	}
}
