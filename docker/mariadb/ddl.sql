create or replace table user_account
(
    id                          bigint auto_increment
        primary key,
    login_id                    varchar(255) null,
    hashed_password             varchar(255) null,
    invalid_login_attempt_count int          null,
    created_dt                  datetime(6)  null,
    created_by                  varchar(255) null,
    last_modified_dt            datetime(6)  null,
    last_modified_by            varchar(255) null
);

create or replace table user_oauth2_token
(
    id               int auto_increment
        primary key,
    user_account_id  bigint               not null,
    access_token     varchar(255)         not null,
    refresh_token    varchar(255)         not null,
    deleted          tinyint(1) default 0 not null,
    created_dt       datetime(6)          not null,
    created_by       varchar(255)         null,
    last_modified_dt datetime(6)          not null,
    last_modified_by varchar(255)         null,
    constraint user_oauth2_token_access_token_uindex
        unique (access_token),
    constraint user_oauth2_token_refresh_token_uindex
        unique (refresh_token),
    constraint user_oauth2_token_user_account_id_fk
        foreign key (user_account_id) references user_account (id)
);