CREATE TABLE metrics (
    id BIGSERIAL PRIMARY KEY,
    polling_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    router_id BIGINT NOT NULL,
    interface_name VARCHAR(255) NOT NULL,

    -- Сырые данные (счетчики байт)
    input_counter BIGINT NOT NULL,
    output_counter BIGINT NOT NULL,

    -- Вычисленная пропускная способность (бит/с)
    input_bandwidth DOUBLE PRECISION,
    output_bandwidth DOUBLE PRECISION,

    -- Вычисленная утилизация (%)
    input_utilization DOUBLE PRECISION,
    output_utilization DOUBLE PRECISION,

    status varchar(255) not null,

    CONSTRAINT fk_metrics_router
        FOREIGN KEY (router_id)
        REFERENCES routers (id)
        ON DELETE CASCADE
);