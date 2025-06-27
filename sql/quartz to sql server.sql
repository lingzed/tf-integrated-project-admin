-- ----------------------------
-- 1、存储每一个已配置的 jobDetail 的详细信息
-- ----------------------------
-- 删除并重新创建 QRTZ_JOB_DETAILS 表
IF OBJECT_ID('QRTZ_JOB_DETAILS', 'U') IS NOT NULL
DROP TABLE QRTZ_JOB_DETAILS;

create table QRTZ_JOB_DETAILS
(
    sched_name        nvarchar(120) not null,
    job_name          nvarchar(150) not null,
    job_group         nvarchar(150) not null,
    description       nvarchar(250),
    job_class_name    nvarchar(250) not null,
    is_durable        char          not null,
    is_nonconcurrent  char          not null,
    is_update_data    char          not null,
    requests_recovery char          not null,
    job_data          varbinary(max),
    primary key (sched_name, job_name, job_group)
)

exec sp_addextendedproperty 'MS_Description', N'任务详细信息表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'任务名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'job_name'
exec sp_addextendedproperty 'MS_Description', N'任务组名', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'job_group'
exec sp_addextendedproperty 'MS_Description', N'相关介绍', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'description'
exec sp_addextendedproperty 'MS_Description', N'执行任务类名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'job_class_name'
exec sp_addextendedproperty 'MS_Description', N'是否持久化', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'is_durable'
exec sp_addextendedproperty 'MS_Description', N'是否并发', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'is_nonconcurrent'
exec sp_addextendedproperty 'MS_Description', N'是否更新数据', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'is_update_data'
exec sp_addextendedproperty 'MS_Description', N'是否接受恢复执行', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'requests_recovery'
exec sp_addextendedproperty 'MS_Description', N'存放持久化job对象', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_JOB_DETAILS', 'COLUMN', 'job_data'

-- ----------------------------
-- 2、 存储已配置的 Trigger 的信息
-- ----------------------------
-- 删除并重新创建 QRTZ_TRIGGERS 表
IF OBJECT_ID('QRTZ_TRIGGERS', 'U') IS NOT NULL
DROP TABLE QRTZ_TRIGGERS;

create table QRTZ_TRIGGERS
(
    sched_name     nvarchar(120) not null,
    trigger_name   nvarchar(150) not null,
    trigger_group  nvarchar(150) not null,
    job_name       nvarchar(150) not null,
    job_group      nvarchar(150) not null,
    description    nvarchar(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority       int,
    trigger_state  nvarchar(16)  not null,
    trigger_type   nvarchar(8)   not null,
    start_time     bigint        not null,
    end_time       bigint,
    calendar_name  nvarchar(200),
    misfire_instr  smallint,
    job_data       varbinary(max),
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, job_name, job_group) references QRTZ_JOB_DETAILS
)

exec sp_addextendedproperty 'MS_Description', N'触发器详细信息表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'触发器的名字', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'trigger_name'
exec sp_addextendedproperty 'MS_Description', N'触发器所属组的名字', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'trigger_group'
exec sp_addextendedproperty 'MS_Description', N'qrtz_job_details表job_name的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'job_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_job_details表job_group的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'job_group'
exec sp_addextendedproperty 'MS_Description', N'相关介绍', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'description'
exec sp_addextendedproperty 'MS_Description', N'上一次触发时间（毫秒）', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'next_fire_time'
exec sp_addextendedproperty 'MS_Description', N'下一次触发时间（默认为-1表示不触发）', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'prev_fire_time'
exec sp_addextendedproperty 'MS_Description', N'优先级', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'priority'
exec sp_addextendedproperty 'MS_Description', N'触发器状态', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'trigger_state'
exec sp_addextendedproperty 'MS_Description', N'触发器的类型', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'trigger_type'
exec sp_addextendedproperty 'MS_Description', N'开始时间', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'start_time'
exec sp_addextendedproperty 'MS_Description', N'结束时间', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'end_time'
exec sp_addextendedproperty 'MS_Description', N'日程表名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'calendar_name'
exec sp_addextendedproperty 'MS_Description', N'补偿执行的策略', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'misfire_instr'
exec sp_addextendedproperty 'MS_Description', N'存放持久化job对象', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_TRIGGERS', 'COLUMN', 'job_data'

-- ----------------------------
-- 3、 存储简单的 Trigger，包括重复次数，间隔，以及已触发的次数
-- ----------------------------
-- 删除并重新创建 QRTZ_SIMPLE_TRIGGERS 表
IF OBJECT_ID('QRTZ_SIMPLE_TRIGGERS', 'U') IS NOT NULL
DROP TABLE QRTZ_SIMPLE_TRIGGERS;

create table QRTZ_SIMPLE_TRIGGERS
(
    sched_name      nvarchar(120) not null,
    trigger_name    nvarchar(150) not null,
    trigger_group   nvarchar(150) not null,
    repeat_count    bigint        not null,
    repeat_interval bigint        not null,
    times_triggered bigint        not null,
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references QRTZ_TRIGGERS
)

exec sp_addextendedproperty 'MS_Description', N'简单触发器的信息表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPLE_TRIGGERS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPLE_TRIGGERS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_name的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPLE_TRIGGERS', 'COLUMN', 'trigger_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_group的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPLE_TRIGGERS', 'COLUMN', 'trigger_group'
exec sp_addextendedproperty 'MS_Description', N'重复的次数统计', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPLE_TRIGGERS', 'COLUMN', 'repeat_count'
exec sp_addextendedproperty 'MS_Description', N'重复的间隔时间', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPLE_TRIGGERS', 'COLUMN', 'repeat_interval'
exec sp_addextendedproperty 'MS_Description', N'已经触发的次数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPLE_TRIGGERS', 'COLUMN', 'times_triggered'

-- ----------------------------
-- 4、 存储 Cron Trigger，包括 Cron 表达式和时区信息
-- ----------------------------
-- 删除并重新创建 QRTZ_CRON_TRIGGERS 表
IF OBJECT_ID('QRTZ_CRON_TRIGGERS', 'U') IS NOT NULL
DROP TABLE QRTZ_CRON_TRIGGERS;

create table QRTZ_CRON_TRIGGERS
(
    sched_name      nvarchar(120) not null,
    trigger_name    nvarchar(150) not null,
    trigger_group   nvarchar(150) not null,
    cron_expression nvarchar(200) not null,
    time_zone_id    nvarchar(80),
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references QRTZ_TRIGGERS
)

exec sp_addextendedproperty 'MS_Description', N'Cron类型的触发器表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CRON_TRIGGERS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CRON_TRIGGERS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_name的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CRON_TRIGGERS', 'COLUMN', 'trigger_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_group的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CRON_TRIGGERS', 'COLUMN', 'trigger_group'
exec sp_addextendedproperty 'MS_Description', N'cron表达式', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CRON_TRIGGERS', 'COLUMN', 'cron_expression'
exec sp_addextendedproperty 'MS_Description', N'时区', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CRON_TRIGGERS', 'COLUMN', 'time_zone_id'

-- ----------------------------
-- 5、 Trigger 作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型，JobStore 并不知道如何存储实例的时候)
-- ----------------------------
-- 删除并重新创建 QRTZ_BLOB_TRIGGERS 表
IF OBJECT_ID('QRTZ_BLOB_TRIGGERS', 'U') IS NOT NULL
DROP TABLE QRTZ_BLOB_TRIGGERS;

create table QRTZ_BLOB_TRIGGERS
(
    sched_name    nvarchar(120) not null,
    trigger_name  nvarchar(150) not null,
    trigger_group nvarchar(150) not null,
    blob_data     varbinary(max),
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references QRTZ_TRIGGERS
)

exec sp_addextendedproperty 'MS_Description', N'Blob类型的触发器表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_BLOB_TRIGGERS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_BLOB_TRIGGERS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_name的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_BLOB_TRIGGERS', 'COLUMN', 'trigger_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_group的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_BLOB_TRIGGERS', 'COLUMN', 'trigger_group'
exec sp_addextendedproperty 'MS_Description', N'存放持久化Trigger对象', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_BLOB_TRIGGERS', 'COLUMN', 'blob_data'

-- ----------------------------
-- 6、 以 Blob 类型存储存放日历信息， quartz可配置一个日历来指定一个时间范围
-- ----------------------------
-- 删除并重新创建 QRTZ_CALENDARS 表
IF OBJECT_ID('QRTZ_CALENDARS', 'U') IS NOT NULL
DROP TABLE QRTZ_CALENDARS;

create table QRTZ_CALENDARS
(
    sched_name    nvarchar(120)  not null,
    calendar_name nvarchar(200)  not null,
    calendar      varbinary(max) not null,
    primary key (sched_name, calendar_name)
)

exec sp_addextendedproperty 'MS_Description', N'日历信息表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CALENDARS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CALENDARS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'日历名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CALENDARS', 'COLUMN', 'calendar_name'
exec sp_addextendedproperty 'MS_Description', N'存放持久化calendar对象', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_CALENDARS', 'COLUMN', 'calendar'

-- ----------------------------
-- 7、 存储已暂停的 Trigger 组的信息
-- ----------------------------
-- 删除并重新创建 QRTZ_PAUSED_TRIGGER_GRPS 表
IF OBJECT_ID('QRTZ_PAUSED_TRIGGER_GRPS', 'U') IS NOT NULL
DROP TABLE QRTZ_PAUSED_TRIGGER_GRPS;

create table QRTZ_PAUSED_TRIGGER_GRPS
(
    sched_name    nvarchar(120) not null,
    trigger_group nvarchar(150) not null,
    primary key (sched_name, trigger_group)
)

exec sp_addextendedproperty 'MS_Description', N'暂停的触发器表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_PAUSED_TRIGGER_GRPS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_PAUSED_TRIGGER_GRPS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_group的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_PAUSED_TRIGGER_GRPS', 'COLUMN', 'trigger_group'

-- ----------------------------
-- 8、 存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息
-- ----------------------------
-- 删除并重新创建 QRTZ_FIRED_TRIGGERS 表
IF OBJECT_ID('QRTZ_FIRED_TRIGGERS', 'U') IS NOT NULL
DROP TABLE QRTZ_FIRED_TRIGGERS;

create table QRTZ_FIRED_TRIGGERS
(
    sched_name        nvarchar(120) not null,
    entry_id          nvarchar(95)  not null,
    trigger_name      nvarchar(150) not null,
    trigger_group     nvarchar(150) not null,
    instance_name     nvarchar(200) not null,
    fired_time        bigint        not null,
    sched_time        bigint        not null,
    priority          int           not null,
    state             nvarchar(16)  not null,
    job_name          nvarchar(200),
    job_group         nvarchar(200),
    is_nonconcurrent  char,
    requests_recovery char,
    primary key (sched_name, entry_id)
)

exec sp_addextendedproperty 'MS_Description', N'已触发的触发器表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'调度器实例id', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'entry_id'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_name的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'trigger_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_group的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'trigger_group'
exec sp_addextendedproperty 'MS_Description', N'调度器实例名', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'instance_name'
exec sp_addextendedproperty 'MS_Description', N'触发的时间', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'fired_time'
exec sp_addextendedproperty 'MS_Description', N'定时器制定的时间', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'sched_time'
exec sp_addextendedproperty 'MS_Description', N'优先级', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'priority'
exec sp_addextendedproperty 'MS_Description', N'状态', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'state'
exec sp_addextendedproperty 'MS_Description', N'任务名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'job_name'
exec sp_addextendedproperty 'MS_Description', N'任务组名', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'job_group'
exec sp_addextendedproperty 'MS_Description', N'是否并发', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'is_nonconcurrent'
exec sp_addextendedproperty 'MS_Description', N'是否接受恢复执行', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_FIRED_TRIGGERS', 'COLUMN', 'requests_recovery'

-- ----------------------------
-- 9、 存储少量的有关 Scheduler 的状态信息，假如是用于集群中，可以看到其他的 Scheduler 实例
-- ----------------------------
-- 删除并重新创建 QRTZ_SCHEDULER_STATE 表
IF OBJECT_ID('QRTZ_SCHEDULER_STATE', 'U') IS NOT NULL
DROP TABLE QRTZ_SCHEDULER_STATE;

create table QRTZ_SCHEDULER_STATE
(
    sched_name        nvarchar(120) not null,
    instance_name     nvarchar(200) not null,
    last_checkin_time bigint        not null,
    checkin_interval  bigint        not null,
    primary key (sched_name, instance_name)
)

exec sp_addextendedproperty 'MS_Description', N'调度器状态表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SCHEDULER_STATE'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SCHEDULER_STATE', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'实例名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SCHEDULER_STATE', 'COLUMN', 'instance_name'
exec sp_addextendedproperty 'MS_Description', N'上次检查时间', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SCHEDULER_STATE', 'COLUMN', 'last_checkin_time'
exec sp_addextendedproperty 'MS_Description', N'检查间隔时间', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SCHEDULER_STATE', 'COLUMN', 'checkin_interval'

-- ----------------------------
-- 10、 存储程序的悲观锁的信息(假如使用了悲观锁)
-- ----------------------------
-- 删除并重新创建 QRTZ_LOCKS 表
IF OBJECT_ID('QRTZ_LOCKS', 'U') IS NOT NULL
DROP TABLE QRTZ_LOCKS;

create table QRTZ_LOCKS
(
    sched_name nvarchar(120) not null,
    lock_name  nvarchar(40)  not null,
    primary key (sched_name, lock_name)
)

exec sp_addextendedproperty 'MS_Description', N'存储的悲观锁信息表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_LOCKS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_LOCKS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'悲观锁名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_LOCKS', 'COLUMN', 'lock_name'

-- ----------------------------
-- 11、 Quartz集群实现同步机制的行锁表
-- ----------------------------
-- 删除并重新创建 QRTZ_SIMPROP_TRIGGERS 表
IF OBJECT_ID('QRTZ_SIMPROP_TRIGGERS', 'U') IS NOT NULL
DROP TABLE QRTZ_SIMPROP_TRIGGERS;

create table QRTZ_SIMPROP_TRIGGERS
(
    sched_name    nvarchar(120) not null,
    trigger_name  nvarchar(150) not null,
    trigger_group nvarchar(150) not null,
    str_prop_1    nvarchar(512),
    str_prop_2    nvarchar(512),
    str_prop_3    nvarchar(512),
    int_prop_1    int,
    int_prop_2    int,
    long_prop_1   bigint,
    long_prop_2   bigint,
    dec_prop_1    numeric(13, 4),
    dec_prop_2    numeric(13, 4),
    bool_prop_1   char,
    bool_prop_2   char,
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references QRTZ_TRIGGERS
)

exec sp_addextendedproperty 'MS_Description', N'同步机制的行锁表', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS'
exec sp_addextendedproperty 'MS_Description', N'调度名称', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'sched_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_name的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'trigger_name'
exec sp_addextendedproperty 'MS_Description', N'qrtz_triggers表trigger_group的外键', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'trigger_group'
exec sp_addextendedproperty 'MS_Description', N'String类型的trigger的第一个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'str_prop_1'
exec sp_addextendedproperty 'MS_Description', N'String类型的trigger的第二个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'str_prop_2'
exec sp_addextendedproperty 'MS_Description', N'String类型的trigger的第三个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'str_prop_3'
exec sp_addextendedproperty 'MS_Description', N'int类型的trigger的第一个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'int_prop_1'
exec sp_addextendedproperty 'MS_Description', N'int类型的trigger的第二个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'int_prop_2'
exec sp_addextendedproperty 'MS_Description', N'long类型的trigger的第一个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'long_prop_1'
exec sp_addextendedproperty 'MS_Description', N'long类型的trigger的第二个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'long_prop_2'
exec sp_addextendedproperty 'MS_Description', N'decimal类型的trigger的第一个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'dec_prop_1'
exec sp_addextendedproperty 'MS_Description', N'decimal类型的trigger的第二个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'dec_prop_2'
exec sp_addextendedproperty 'MS_Description', N'Boolean类型的trigger的第一个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'bool_prop_1'
exec sp_addextendedproperty 'MS_Description', N'Boolean类型的trigger的第二个参数', 'SCHEMA', 'dbo', 'TABLE', 'QRTZ_SIMPROP_TRIGGERS', 'COLUMN', 'bool_prop_2'


































