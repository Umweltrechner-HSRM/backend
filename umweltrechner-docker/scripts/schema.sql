CREATE DATABASE IF NOT EXISTS umweltrechner;

create table if not exists umweltrechner.formula
(
  id      varchar(36)   not null primary key,
  formula varchar(1000) null
);

create table if not exists umweltrechner.variable
(
  name            varchar(255) not null primary key,
  minThreshold    double null,
  maxThreshold    double null
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
  phone_number    varchar(50) null,
  email           varchar(255) null,
  variable_name   varchar(100) not null primary key,
  foreign key (variable_name) references variable(name)
);
