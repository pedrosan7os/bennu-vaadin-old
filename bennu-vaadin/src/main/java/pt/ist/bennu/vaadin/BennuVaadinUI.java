package pt.ist.bennu.vaadin;

import pt.ist.bennu.dispatch.VaadinViewAnnotationInitializer;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class BennuVaadinUI extends UI {
	@Override
	protected void init(VaadinRequest request) {
		Navigator navigator = new Navigator(this, this);
		VaadinViewAnnotationInitializer.initializeNavigator(navigator);
		navigator.setErrorView(new ErrorView());
		navigator.navigateTo(getPage().getUriFragment());
	}
}
