delete from messages cascade;
delete from devices cascade;
delete from rooms cascade;

insert into rooms(id, name, floor) values(1000, 'Obyvak', 0);
insert into rooms(id, name, floor) values(1001, 'Loznice', 1);

insert into devices(id, type, identifier, room_id) values(1000, 0, 'Hejkino L', 1000);
insert into devices(id, type, identifier, room_id, node_id) values(1001, 1, 'Living Room Main Light', 1001, 1000);
insert into devices(id, type, identifier, room_id) values(2000, 0, 'Hejkino B', 1001);

insert into device_properties(device_id, name, value) values(1000, 'ip', '10.0.0.100');
insert into device_properties(device_id, name, value) values(1000, 'port', '2210');
insert into device_properties(device_id, name, value) values(2000, 'ip', '10.0.0.101');
insert into device_properties(device_id, name, value) values(2000, 'port', '12321');

-- messages

insert into messages(id, type, identifier, request_data, request_send, response_receive, response_type, response_data, node_id)
values(1001, 10, 43, null, '2013-12-14 17:13:58.483', '2013-12-14 17:14:01.321', 0, decode('0A0004', 'hex'), 1000);

insert into messages(id, type, identifier, request_data, request_send, response_receive, response_type, response_data, node_id)
values(1002, 5, 4, null, '2013-12-14 17:13:58.483', '2013-12-14 17:14:01.321', 1, null, 2000);