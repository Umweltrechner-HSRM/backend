CREATE DATABASE IF NOT EXISTS umweltrechner;

create table if not exists umweltrechner.formula
(
    id         varchar(36)   not null primary key,
    formula    varchar(1000) null,
    created_at timestamp     null,
    created_by varchar(100)  not null,
    changed_at timestamp     null,
    changed_by varchar(100)  not null
);

create table if not exists umweltrechner.variable
(
    name         varchar(255) not null primary key,
    minThreshold double       null,
    maxThreshold double       null
);

create table if not exists umweltrechner.sensor
(
    name            varchar(100)  not null
        primary key,
    location        varchar(100)  null,
    description     varchar(500)  null,
    value           double        null,
    unit            varchar(36)   null,
    simulation_code varchar(1000) null,
    created_at      timestamp     null
);

create table if not exists umweltrechner.customeralerts
(
    phone_number  varchar(50)  null,
    email         varchar(255) null,
    variable_name varchar(100) not null primary key,
    foreign key (variable_name) references variable (name)
);


create table if not exists umweltrechner.dashboard
(
    id         varchar(36)  not null
        primary key,
    name       varchar(100) not null,
    created_at timestamp    null,
    constraint name
        unique (name)
);

create table if not exists umweltrechner.dashboard_component
(
    id             varchar(36)  not null
        primary key,
    name           varchar(100) not null,
    type           varchar(32)  not null,
    variable       varchar(255) not null,
    variable_color varchar(16)  null,
    created_at     timestamp    null,
    created_by     varchar(100) null,
    changed_at     timestamp    null,
    changed_by     varchar(100) null
);

create table if not exists umweltrechner.dashboard_page
(
    dashboard_id           varchar(36) not null,
    dashboard_component_id varchar(36) not null,
    position               int         not null,
    primary key (dashboard_id, dashboard_component_id),
    constraint dashboard_page_dashboard_component_id_fk
        foreign key (dashboard_component_id) references dashboard_component (id),
    constraint dashboard_page_dashboard_id_fk
        foreign key (dashboard_id) references dashboard (id)
            on delete cascade
);