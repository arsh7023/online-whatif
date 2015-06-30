CREATE TABLE users
(
  user_id SERIAL NOT NULL, 
  email character varying(255) NOT NULL,
  enabled integer NOT NULL DEFAULT 1,
  firstname character varying(255) ,
  lastname character varying(255) ,
  password character varying(100),
  uuid character varying(100),
  CONSTRAINT users_pkey PRIMARY KEY (user_id),
  CONSTRAINT users_email UNIQUE (email)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO envision;

CREATE TABLE roles
(
  role_id serial NOT NULL,
  rolename CHARACTER varying(45) NOT NULL,
  CONSTRAINT roles_pkey PRIMARY KEY (role_id),
  CONSTRAINT users_role UNIQUE (rolename)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE roles
  OWNER TO envision;
 
CREATE TABLE user_roles
(
  user_role_id serial NOT NULL,
  role_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  CONSTRAINT user_roles_pkey PRIMARY KEY (user_role_id),
  CONSTRAINT fk_user_id FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT fk_role_id FOREIGN KEY (role_id)
      REFERENCES roles (role_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,     
  CONSTRAINT user_roles_role_username_key UNIQUE (role_id, user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_roles
  OWNER TO envision;
 
CREATE TABLE organisations
(
  org_id serial NOT NULL,
  orgname character varying(255),
  orgemail character varying(255),
  orgurl character varying(255),
  orgcountry character varying(255),
  orgstate character varying(50),
  orglga character varying(50),
  orgbounds character varying(255),
  orgextent character varying(255),
  orgcenter character varying(255),
  contact_id integer,
  CONSTRAINT org_pkey PRIMARY KEY (org_id),
  CONSTRAINT users_orgs UNIQUE (orgname)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organisations
  OWNER TO envision;
 
CREATE TABLE user_orgs
(
  user_org_id serial NOT NULL,
  org_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  CONSTRAINT user_org_pkey PRIMARY KEY (user_org_id),
  CONSTRAINT fk_orguser_id FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT fk_org_id FOREIGN KEY (org_id)
      REFERENCES organisations (org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,     
  CONSTRAINT user_orgs_org_username_key UNIQUE (org_id, user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_orgs
  OWNER TO envision;
 
CREATE TABLE license
(
  lic_id serial NOT NULL,
  licname CHARACTER varying(255) NOT NULL,
  lictext CHARACTER varying(255),
  licblob BYTEA,
  licver REAL NOT NULL,
  org_id INTEGER NOT NULL,
  CONSTRAINT fk_org_id FOREIGN KEY (org_id)
      REFERENCES organisations (org_id) MATCH SIMPLE,
  CONSTRAINT lic_pkey PRIMARY KEY (lic_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE license
  OWNER TO envision;
 
CREATE TABLE application
(
  app_id serial NOT NULL,
  appname CHARACTER varying(255) NOT NULL,
  appcontact CHARACTER varying(255) NOT NULL,
  appurl CHARACTER varying(255) NOT NULL,
  CONSTRAINT app_pkey PRIMARY KEY (app_id),
  CONSTRAINT users_apps UNIQUE (appname)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE application
  OWNER TO envision;
 
CREATE TABLE agreement
(
  agr_id serial NOT NULL,
  agrname CHARACTER varying(255),
  agreed INTEGER NOT NULL DEFAULT 0,
  aggtime TIMESTAMP DEFAULT NOW(),
  lic_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  app_id INTEGER NOT NULL,
  CONSTRAINT fk_lic_id FOREIGN KEY (lic_id)
      REFERENCES license (lic_id) MATCH SIMPLE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE,
   CONSTRAINT fk_app_id FOREIGN KEY (app_id)
      REFERENCES application (app_id) MATCH SIMPLE,       
  CONSTRAINT agr_pkey PRIMARY KEY (agr_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE agreement
  OWNER TO envision;
 
CREATE TABLE user_apps
(
  user_app_id serial NOT NULL,
  app_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  CONSTRAINT user_apps_pkey PRIMARY KEY (user_app_id),
  CONSTRAINT fk_user_id FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT fk_app_id FOREIGN KEY (app_id)
      REFERENCES application (app_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,     
  CONSTRAINT user_apps_role_username_key UNIQUE (app_id, user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_apps
  OWNER TO envision;

CREATE TABLE acclvls
(
  acclvl_id serial NOT NULL,
  acclvlname character varying(45) NOT NULL,
  CONSTRAINT acclvl_pkey PRIMARY KEY (acclvl_id),
  CONSTRAINT users_acclvl UNIQUE (acclvlname)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE acclvls
  OWNER TO envision;
 
CREATE TABLE user_acclvls
(
  user_acclvl_id serial NOT NULL,
  acclvl_id integer NOT NULL,
  user_id integer NOT NULL,
  CONSTRAINT user_acclvls_pkey PRIMARY KEY (user_acclvl_id),
  CONSTRAINT fk_user_id FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT fk_acclvl_id FOREIGN KEY (acclvl_id)
      REFERENCES acclvls (acclvl_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,     
  CONSTRAINT user_acclvls_acclvl_user_key UNIQUE (acclvl_id, user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_acclvls
  OWNER TO envision;

INSERT INTO application (appname, appcontact, appurl) 
values('Whatif','Whatif Admin','whatif-demo');

INSERT INTO users (email, enabled, firstname, lastname, PASSWORD)
values('admin@whatif-demo', '1', 'Whatif', 'Admin', '$2a$10$IMlSuqhi3F..6V4zG/Y78.DCg2DmXT.B7JsvZVpwf5d4FiLiNQo4K');
 
INSERT INTO roles (rolename) 
values('Admin');
INSERT INTO roles (rolename)
values('User');
