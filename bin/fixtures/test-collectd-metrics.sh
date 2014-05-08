curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/api/type <<EOF | python -m json.tool
{
  "name":"CollectdMetric",
  "properties":[
    {"name":"plugin","type":"string"},
    {"name":"plugin_instance","type":"string"},
    {"name":"type","type":"string"},
    {"name":"type_instance","type":"string"},
    {"name":"datacenter","type":"string"},
    {"name":"time","type":"long"},
    {"name":"value","type":"double"},
    {"name":"name","type":"string"},
    {"name":"host","type":"string"}
  ]
}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/api/statement <<EOF | python -m json.tool
{"name": "eventlogdebug", "query":"SELECT * FROM CollectdMetric", "debug": true}
EOF

curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" -d @- http://localhost:8080/api/flow <<EOF | python -m json.tool
{"name": "EventsIn", "query":"create dataflow EventsIn AMQPSource -> EventsIn<CollectdMetric> {  host: 'localhost',  exchange: 'collectd-metrics', port: 5672, username: 'guest',  password: 'guest',  routingKey: '#', collector: {class: 'org.cad.interruptus.AMQPJsonToMap'}, logMessages: true  } EventBusSink(EventsIn){}"}
EOF

curl -X GET -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/api/statement/eventlogdebug/state | python -m json.tool