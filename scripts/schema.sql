CREATE DATABASE IF NOT EXISTS umweltrechner;

create table if not exists umweltrechner.formula
(
    id         varchar(36)  not null
        primary key,
    formula    varchar(512) null,
    created_at timestamp    null,
    created_by varchar(100) not null,
    changed_at timestamp    null,
    changed_by varchar(100) not null,
    constraint formula
        unique (formula)
);

create table if not exists umweltrechner.variable
(
    name          varchar(255) not null
        primary key,
    min_threshold double       null,
    max_threshold double       null,
    last_over_threshold timestamp(3) null
);

create table if not exists umweltrechner.sensor
(
    name            varchar(100)  not null
        primary key,
    location        varchar(100)  null,
    description     varchar(500)  null,
    value           double        null,
    unit            varchar(36)   null,
    created_at      timestamp     null
);

create table if not exists umweltrechner.customer_alerts
(
    id            varchar(36)  not null
        primary key,
    variable_name varchar(100) not null,
    phone_number  varchar(50)  null,
    email         varchar(255) null,
    last_notified timestamp    null,
    constraint customer_alerts_variable_name_fk
        foreign key (variable_name) references variable (name)
            on delete cascade
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
    stroke         varchar(16)  not null,
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