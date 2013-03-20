package pt.ist.bennu.vaadin.example;

import pt.ist.bennu.dispatch.Functionality;
import pt.ist.bennu.vaadin.BennuVaadinView;
import pt.ist.bennu.vaadin.annotation.VaadinViewInitializer;

import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

@Functionality(app = ExampleApp.class, path = "hello", bundle = "ExampleResources",
        description = "title.example.hello.description", title = "title.example.hello")
public class HelloWorldView extends BennuVaadinView {
    @VaadinViewInitializer
    public void enter(String name) {
        setCompositionRoot(new Label("Hello " + name));
    }

    public static Link link(String title, String... args) {
        return BennuVaadinView.link(title, HelloWorldView.class, args);
    }
}
