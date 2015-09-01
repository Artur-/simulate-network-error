package org.vaadin.artur;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
public class ErrorSimulatorUI extends UI {

    UidlErrorCodeSender errorCodeResponder = new UidlErrorCodeSender();
    private ReconnectConfigPanelForReal panel;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getSession().addRequestHandler(errorCodeResponder);

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        panel = new ReconnectConfigPanelForReal();
        layout.addComponent(panel);

    }

    @WebServlet(urlPatterns = "/*", name = "ErrorSimlatorServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ErrorSimulatorUI.class, productionMode = false)
    public static class ErrorSimulatorServlet extends VaadinServlet {
        @Override
        protected VaadinServletService createServletService(
                DeploymentConfiguration deploymentConfiguration)
                        throws ServiceException {
            ErrorSimulatorServletService service = new ErrorSimulatorServletService(
                    this, deploymentConfiguration);
            service.init();
            return service;
        }
    }
}
