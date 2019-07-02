
# create database blog;
# use blog;



create table blog_user(
        user_id varchar(32) not null,
        user_name varchar(64) not null comment '用户名称',
        user_email varchar(32) not null comment '用户邮箱',
        user_phone varchar(11) not null comment '用户电话',
        user_password varchar(100) not null comment '用户密码',
	user_role varchar(10) not null comment '用户角色',
        user_question varchar(64) not null comment '忘记密码问题',
        user_answer varchar(64) not null comment '忘记密码的回答',
        create_time timestamp not null default current_timestamp comment '创建时间',
        update_time timestamp not null default current_timestamp on update current_timestamp comment '修改时间',
        primary key (user_id),
        unique index_user_id (user_id)
) comment '用户实体表';


create table blog_article(
	article_id varchar(32) not null,
	article_user_id varchar(32) not null comment '文章属于谁',
	article_title varchar(200) not null comment '文章标题',
	article_content varchar(50) not null comment '文件位置',
        create_time timestamp not null default current_timestamp comment '创建时间',
        update_time timestamp not null default current_timestamp on update current_timestamp comment '修改时间',
	primary key (article_id),
	unique index_article_id (article_id)
) comment '文章实体类';


create table blog_comment(
	comment_id varchar(32) not null,
	comment_user_id varchar(32) not null comment '谁的评论',
	comment_article_id varchar(32) not null comment '评论的谁',
	comment_farther varchar(32) not null default '0' comment '评论的评论,默认0，评论的是文章，如果不是文章就是评论的评论的comment_id',
	comment_content varchar(200) not null comment '评论内容',
	primary key (comment_id),
	unique index_comment_id (comment_id)
) comment '评论实体类';
