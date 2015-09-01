package org.vaadin.artur;

import java.util.List;

import org.vaadin.artur.ErrorSimulatorUI.ErrorSimulatorServlet;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.communication.SessionRequestHandler;

public class ErrorSimulatorServletService extends VaadinServletService {

    public ErrorSimulatorServletService(
            ErrorSimulatorServlet errorSimulatorServlet,
            DeploymentConfiguration deploymentConfiguration)
                    throws ServiceException {
        super(errorSimulatorServlet, deploymentConfiguration);
    }

    @Override
    protected List<RequestHandler> createRequestHandlers()
            throws ServiceException {
        List<RequestHandler> l = super.createRequestHandlers();

        // Move SessionRequestHandler to the end so we can override
        // UidlRequestHandler with a session specific handler
        RequestHandler moveToEnd = null;
        for (RequestHandler rh : l) {
            if (rh instanceof SessionRequestHandler) {
                moveToEnd = rh;
                break;
            }
        }
        if (moveToEnd != null) {
            l.remove(moveToEnd);
            l.add(moveToEnd);
        }

        return l;
    }
}
