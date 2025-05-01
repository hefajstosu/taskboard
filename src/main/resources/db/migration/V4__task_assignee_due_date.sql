alter table tasks add column assignee_id bigint references app_users (id) on delete set null;
alter table tasks add column due_date date;

create index idx_tasks_assignee on tasks (assignee_id);
