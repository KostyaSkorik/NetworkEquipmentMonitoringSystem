//package by.kostya.skorik.persistence.adapter;
//
//import by.kostya.skorik.domain.model.Router;
//import by.kostya.skorik.persistence.entity.RouterEntity;
//import by.kostya.skorik.persistence.mapper.RouterMapper;
//import by.kostya.skorik.persistence.mapper.RouterMapperImpl;
//import by.kostya.skorik.persistence.repository.JpaRouterRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
//import org.springframework.boot.persistence.autoconfigure.EntityScan;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.test.context.ContextConfiguration;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//@DataJpaTest
//@ContextConfiguration(classes = {
//        RouterJpaAdapter.class,
//        JpaRouterRepository.class
//})
//@EntityScan("by.kostya.skorik.persistence.entity")
//@EnableJpaRepositories("by.kostya.skorik.persistence.repository")
//@Import(RouterMapperImpl.class)
//class RouterJpaAdapterTest {
//
//    @Autowired
//    private RouterJpaAdapter routerJpaAdapter;
//
//    @Autowired
//    private JpaRouterRepository jpaRouterRepository;
//
//
//    @Test
//    void save() {
//        Router router = new Router();
//        router.setName("R1");
//        router.setIp("10.0.0.1");
//
//        boolean isSave = routerJpaAdapter.save(router);
//        assertTrue(isSave);
//
//
//        RouterEntity savedEntity = jpaRouterRepository.findById(1L)
//                .orElseThrow(()->new AssertionError("Router not found"));
//        assertEquals(router.getName(),savedEntity.getName());
//        assertEquals(router.getIp(),savedEntity.getIp());
//    }
//
//    @Test
//    void getRouterById() {
//        Router router = new Router();
//        router.setName("R2");
//        router.setIp("10.0.0.1");
//
//        boolean isSave = routerJpaAdapter.save(router);
//        assertTrue(isSave);
//
//        Router saved = routerJpaAdapter.getRouterById(2L);
//
//        assertEquals(router.getName(),saved.getName());
//
//    }
//}