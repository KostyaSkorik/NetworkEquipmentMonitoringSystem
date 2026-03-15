package by.kostya.skorik.persistence.adapter;

import by.kostya.skorik.domain.model.Metrics;
import by.kostya.skorik.domain.model.Router;
import by.kostya.skorik.persistence.entity.MetricsEntity;
import by.kostya.skorik.persistence.entity.RouterEntity;
import by.kostya.skorik.persistence.mapper.MetricsMapperImpl;
import by.kostya.skorik.persistence.mapper.RouterMapperImpl;
import by.kostya.skorik.persistence.repository.JpaMetricsRepository;
import by.kostya.skorik.persistence.repository.JpaRouterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {
        JpaMetricsRepository.class,
        MetricsJpaAdapter.class,
        RouterJpaAdapter.class,
        JpaRouterRepository.class
})
@EntityScan("by.kostya.skorik.persistence.entity") // Укажи путь к сущностям
@EnableJpaRepositories("by.kostya.skorik.persistence.repository")
@Import({MetricsMapperImpl.class, RouterMapperImpl.class})
class MetricsJpaAdapterTest {

    @Autowired
    private MetricsJpaAdapter metricsJpaAdapter;

    @Autowired
    private JpaMetricsRepository jpaMetricsRepository;

    @Autowired
    private JpaRouterRepository jpaRouterRepository;

    @Autowired
    private RouterJpaAdapter routerJpaAdapter;


    @Test
    void save() {

        Router router = new Router();
        router.setName("R1");
        router.setIp("10.0.0.1");

        boolean isSave = routerJpaAdapter.save(router);
        assertTrue(isSave);

        RouterEntity routerEntity = jpaRouterRepository.findAll().get(0);
        Long routerId = routerEntity.getId();

        Metrics metrics = new Metrics();
        metrics.setPollingTime(LocalDateTime.of(2026, Month.MARCH, 9, 15, 30));
        metrics.setRouterId(routerId);
        metrics.setInterfaceName("Int 1/0");
        metrics.setInputCounter(3431L);
        metrics.setOutputCounter(5324L);

        metricsJpaAdapter.save(metrics);

        MetricsEntity saved = jpaMetricsRepository.findAll().get(0);

        assertEquals(metrics.getInterfaceName(), saved.getInterfaceName());
        assertEquals(metrics.getInputCounter(), saved.getInputCounter());
        assertEquals(metrics.getPollingTime(), saved.getPollingTime());
        assertEquals(routerEntity, saved.getRouter());

    }

    @Test
    void getMetricsByTime() {
        Router router = new Router();
        router.setName("R1");
        router.setIp("10.0.0.1");

        boolean isSave = routerJpaAdapter.save(router);
        assertTrue(isSave);

        RouterEntity routerEntity = jpaRouterRepository.findAll().get(0);
        Long routerId = routerEntity.getId();


        Metrics metrics1 = new Metrics();
        metrics1.setPollingTime(LocalDateTime.of(2026, Month.MARCH, 9, 15, 30));
        metrics1.setRouterId(routerId);
        metrics1.setInterfaceName("Int 1/0");
        metrics1.setInputCounter(3431L);
        metrics1.setOutputCounter(5324L);
        metricsJpaAdapter.save(metrics1);

        Metrics metrics2 = new Metrics();
        metrics2.setPollingTime(LocalDateTime.of(2026, Month.MARCH, 12, 10, 10));
        metrics2.setRouterId(routerId);
        metrics2.setInterfaceName("Int 1/0");
        metrics2.setInputCounter(6431L);
        metrics2.setOutputCounter(9324L);
        metricsJpaAdapter.save(metrics2);

        LocalDateTime start = LocalDateTime.of(2026, Month.MARCH, 7, 10, 10);
        LocalDateTime end = LocalDateTime.of(2026, Month.MARCH, 13, 10, 10);

        List<Metrics> metricsList = metricsJpaAdapter.getMetricsByTime(end, start);

        Metrics firstMetricsInList = metricsList.get(0);
        assertEquals(3431L, firstMetricsInList.getInputCounter());
        assertEquals(5324L, firstMetricsInList.getOutputCounter());

        Metrics secondMetricsInList = metricsList.get(1);
        assertEquals(6431L, secondMetricsInList.getInputCounter());
        assertEquals(9324L, secondMetricsInList.getOutputCounter());

    }

    @Test
    void getLastSavedMetrics(){
        Router router = new Router();
        router.setName("R1");
        router.setIp("10.0.0.1");

        boolean isSave = routerJpaAdapter.save(router);
        assertTrue(isSave);

        RouterEntity routerEntity = jpaRouterRepository.findAll().get(0);
        Long routerId = routerEntity.getId();

        Optional<Metrics> lastMetricsNull = metricsJpaAdapter
                .getLastSavedMetrics(1L, "Int 1/0");

        assertTrue(lastMetricsNull.isEmpty());

        Metrics metrics1 = new Metrics();
        metrics1.setPollingTime(LocalDateTime.of(2026, Month.MARCH, 9, 15, 30));
        metrics1.setRouterId(routerId);
        metrics1.setInterfaceName("Int 1/0");
        metrics1.setInputCounter(3431L);
        metrics1.setOutputCounter(5324L);
        metricsJpaAdapter.save(metrics1);

        Metrics metrics2 = new Metrics();
        metrics2.setPollingTime(LocalDateTime.of(2026, Month.MARCH, 12, 10, 10));
        metrics2.setRouterId(routerId);
        metrics2.setInterfaceName("Int 1/0");
        metrics2.setInputCounter(6431L);
        metrics2.setOutputCounter(9324L);
        metricsJpaAdapter.save(metrics2);

        Metrics metrics3 = new Metrics();
        metrics3.setPollingTime(LocalDateTime.now());
        metrics3.setRouterId(routerId);
        metrics3.setInterfaceName("Int 2/0");
        metrics3.setInputCounter(9431L);
        metrics3.setOutputCounter(19324L);
        metricsJpaAdapter.save(metrics3);

        Metrics lastMetrics = metricsJpaAdapter
                .getLastSavedMetrics(1L, "Int 1/0")
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        assertEquals(metrics2.getInputCounter(),lastMetrics.getInputCounter());
    }

}