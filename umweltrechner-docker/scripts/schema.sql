CREATE DATABASE IF NOT EXISTS umweltrechner;

create table if not exists umweltrechner.formula
(
  id      varchar(36)   not null
  primary key,
  formula varchar(1000) null
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