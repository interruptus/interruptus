Interruptus
================

A framework for scalable monitoring.

<pre>
            _.---._
        .-'         '-.
     .'                 '.
    '       '.   .'       '
   / /        \ /        \ \
  '  |         :         |  '
 /   |         .         |   \
 |   \         |         /   |
 '. . \        |        / . .'
  |   .\      .'.      /.   |
  \  .  `-           -'  .  /
   '.      .. ... ..      .'
    |  `` ` .     . ` ``  |
    | .-_-.  '. .'  .-_-. |
   .'( (O) )|  :  |( (O) )'.
    \.'---'/   :   \'---'./
      \_ .'  . ' .  '. _/
     .' /             \ '.
     './ / /  / \ \  \ \.'
      : | | /|  : |  | :
      | : | \\  | '  : |
      | /\ \/ \ | : /\ :
      ' :/\ \ : ' ||  \ \
      / | /\ \| : ' \  \ \
     / / /  \/ /| :  |  \ \
    / / :   / /\ \ \ /   \ \
   ' /\ \  | /\ :.\ \    / |
   \ \ \ \ \/ / || \ \   \/
    \/  \|    \/ \/ |/
    
</pre>

Authors
=======

 Mark Steele <mark@control-alt-del.org>
 Fabio "Fantastico" B. Silva <fabio.bat.silva@gmail.com>


API USAGE
=========

```
## CREATE/LIST/START/STARTALL/STOP/STOPALL/DESTROY/DESTROYALL STATEMENTS

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/type <<EOF | python -m json.tool
{"name":"EventLog","properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"},{"name":"eventType","type":"string"}]}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement <<EOF | python -m json.tool
{"name": "eventlogdebug", "query":"SELECT * FROM EventLog", "debug": true}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/statement | python -m json.tool

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement/state <<EOF | python -m json.tool
{"name": "eventlogdebug"}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement/stop <<EOF | python -m json.tool
{"name": "eventlogdebug"}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement/state <<EOF | python -m json.tool
{"name": "eventlogdebug"}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement/start <<EOF | python -m json.tool
{"name": "eventlogdebug"}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement/state <<EOF | python -m json.tool
{"name": "eventlogdebug"}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/statement/stopAll | python -m json.tool

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement/state <<EOF | python -m json.tool
{"name": "eventlogdebug"}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/statement/startAll | python -m json.tool

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement/state <<EOF | python -m json.tool
{"name": "eventlogdebug"}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement/destroy <<EOF | python -m json.tool
{"name": "eventlogdebug"}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/statement | python -m json.tool

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/statement <<EOF | python -m json.tool
{"name": "eventlogdebug", "query":"SELECT * FROM EventLog", "debug": true}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/statement | python -m json.tool

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/statement/destroyAll | python -m json.tool

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/statement | python -m json.tool


## CREATE/DELETE/LIST TYPES

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/type <<EOF | python -m json.tool
{"name":"EventLog","properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"},{"name":"eventType","type":"string"}]}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/type | python -m json.tool

curl -X DELETE -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/type <<EOF | python -m json.tool
{"name":"EventLog","properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"},{"name":"eventType","type":"string"}]}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/type | python -m json.tool


## CREATE/CANCEL/LIST FLOWS

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/type <<EOF | python -m json.tool
{"name":"EventLog","properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"},{"name":"eventType","type":"string"}]}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/flow <<EOF | python -m json.tool
{"name": "EventsIn", "query":"create dataflow EventsIn AMQPSource -> EventsIn<EventLog> {  host: 'localhost',  exchange: 'metrics', port: 5672, username: 'guest',  password: 'guest',  routingKey: '#', collector: {class: 'org.control_alt_del.interruptus.AMQPJsonToMap'}, logMessages: true  } EventBusSink(EventsIn){}"}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/flow <<EOF | python -m json.tool
{"name": "EventsOut", "query":"create dataflow EventsOut EventBusSource -> outstream<EventLog> {} AMQPSink(outstream) { host: 'localhost', exchange: 'alerts', queueName: 'alerts', username: 'guest', password: 'guest', routingKey: '#', declareAutoDelete: false, declareDurable: true, collector: {class: 'org.control_alt_del.interruptus.EventToAMQP'},logMessages: true}"}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/flow

curl -X DELETE -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/interruptus/api/flow <<EOF | python -m json.tool
{"name": "EventsIn", "query":"create dataflow EventsIn AMQPSource -> EventsIn<EventLog> {  host: 'localhost',  exchange: 'metrics', port: 5672, username: 'guest',  password: 'guest',  routingKey: '#', collector: {class: 'org.control_alt_del.interruptus.AMQPJsonToMap'}, logMessages: true  } EventBusSink(EventsIn){}"}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/flow

# Manage config (updating/saving incomplete)
curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/interruptus/api/config | python -m json.tool
curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d "{}" http://localhost:8080/interruptus/api/config | python -m json.tool
```
