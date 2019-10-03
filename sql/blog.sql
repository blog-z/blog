
# create database blog;
# use blog;



create table blog_user(
        user_id char(15) not null,
        user_name varchar(20) not null comment '用户名称',
        user_email varchar(40) not null comment '用户邮箱',
        user_phone char(11) not null comment '用户电话',
        user_password char(100) not null comment '用户密码',
	    user_role char(4) not null comment '用户角色',
        user_question varchar(40) not null comment '忘记密码问题',
        user_answer varchar(40) not null comment '忘记密码的回答',
        create_time timestamp not null default current_timestamp comment '创建时间',
        update_time timestamp not null default current_timestamp on update current_timestamp comment '修改时间',
        primary key (user_id),
        unique index_user_id (user_id),
        index index_user_user (user_name,user_email,user_phone)
) comment '用户实体表';


create table blog_article(
	article_id char(15) not null,
	article_user_id char(15) not null comment '文章属于谁',
	article_title varchar(100) not null comment '文章标题',
	article_content varchar(50) not null comment '文件位置',
    create_time timestamp not null default current_timestamp comment '创建时间',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '修改时间',
	primary key (article_id),
	unique index_article_id (article_id)
) comment '文章实体类';


create table blog_comment(
	comment_id char(15) not null,
	comment_user_id char(15) not null comment '谁的评论',
	comment_article_id char(15) not null comment '评论的谁',
	comment_farther char(15) not null default '0' comment '评论的评论,默认0，评论的是文章，如果不是文章就是评论的评论的comment_id',
	comment_content varchar(200) not null comment '评论内容',
    create_time timestamp not null default current_timestamp comment '创建时间',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '修改时间',
	primary key (comment_id),
	unique index_comment_id (comment_id),
	index index_comment_fartherId(comment_farther)
) comment '评论实体类';

create table blog_message(
    message_id char(15) not null,
    message_user_id char(15) not null comment '谁发的消息',
    message_user_get_id char(15) not null comment '谁将收到这个消息',
    message_comment varchar(500) not null comment '消息内容',
    create_time timestamp not null default current_timestamp comment '创建时间',
    primary key (message_id),
    unique index_message_id (message_id),
    index index_message_user_get_id (message_user_get_id)
) comment '消息实体类';





