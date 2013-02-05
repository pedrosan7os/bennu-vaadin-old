package pt.ist.bennu.vaadin;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class ErrorView extends Panel implements View {
    @Override
    public void enter(ViewChangeEvent event) {
        setContent(new Label("Unknown fragment"));
    }
}
