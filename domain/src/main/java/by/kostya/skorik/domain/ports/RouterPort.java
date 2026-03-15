package by.kostya.skorik.domain.ports;

import by.kostya.skorik.domain.model.Router;

import java.util.List;

public interface RouterPort {

    boolean save(Router router);
    Router getRouterById(Long id);
    List<Router> findAllRouters();
}
