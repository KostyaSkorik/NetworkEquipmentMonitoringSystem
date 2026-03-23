CREATE TABLE alerts(
    id BIGSERIAL PRIMARY KEY,
    time timestamp without time zone NOT NULL,
    ip_source varchar(255) not null,
    router_name varchar(255) not null,
    trap_type varchar(255) not null,
    sys_up_time varchar(255) not null,
    interface_name varchar(255) not null,
    message varchar(255) not null
);