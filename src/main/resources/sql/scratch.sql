select * from topics;
select tm.id as "topic_member_id", tm.status_id, t.name as "topic_name", t.id, e.email, e.id from topic_members tm join entities e ON tm.entity_id = e.id join topics t ON tm.topic_id = t.id;
select * from topic_content;
select * from entities;
select * from entity_roles;
select * from topic_members;

delete from topic_members where creator_id = (select id from entities where email = 'adam@gmail.com');
delete from topics where creator_id = (select id from entities where email = 'adam@gmail.com');

