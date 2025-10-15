create table if not exists setting
(
    key   text,
    value text,
    primary key (key)
);

create table if not exists mailbox
(
    host      text,
    username  text,
    smtp_host text,
    imap_host text,
    password  text,
    folders    text [],
    errors    text [],
    primary key (host, username)
);

create table if not exists mail
(
    id            text,
    of            text,
    mail_from     text [],
    mail_reply_to text [],
    mail_to       text [],
    mail_cc       text [],
    mail_bcc      text [],
    sent_date     timestamp,
    subject       text,
    primary key (id)
);

create table if not exists mail_part
(
    id           text,
    super_id     text,
    content_type text,
    content      text,
    primary key (id)
);

create table if not exists headers
(
    id           text,
    mail_part_id text,
    key          text,
    value        text,
    primary key (id)
);