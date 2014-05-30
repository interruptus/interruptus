curl -X POST 'http://localhost:8080/api/type' -H "Content-Type:application/json" -d '{
    "name":"CollectdMetric",
    "properties":{
      "plugin":"string",
      "plugin_instance":"string",
      "type":"string",
      "type_instance":"string",
      "datacenter":"string",
      "time":"long",
      "value":"double",
      "name":"string",
      "host":"string"
    }
}'

# {"type":"CollectdMetric","body":{"plugin":"disk","plugin_instance":"disk_sdd1","datacenter":"east_coast","time":"1399577466","host":"mq01.ss","name":"usage","value":"1234591"}}

curl -X POST 'http://localhost:8080/api/statement' -H "Content-Type:application/json" -d '{
    "name":"eventlogdebug",
    "query":"SELECT * FROM CollectdMetric WHERE host = \"mq01.ss\"",
    "debug":true,
    "started":true
}'

curl -X POST 'http://localhost:8080/api/flow' -H "Content-Type:application/json" -d '{
  "name":"EventsIn",
  "started":true,
  "query":"
    create dataflow EventsIn AMQPSource -> EventsIn<CollectdMetric>
    {
        collector:    {class: \"org.cad.interruptus.AMQPJsonToMap\"},
        host:         \"localhost\",
        exchange:     \"collectd_metrics\",
        port:         5672,
        username:     \"guest\",
        password:     \"guest\",
        routingKey:   \"#\",
        logMessages:  true
    } EventBusSink(EventsIn){}"
}'

curl -X POST 'http://localhost:8080/api/flow' -H "Content-Type:application/json" -d '{
  "name":"EventsOut",
  "masterOnly":true,
  "started":true,
  "query":"
    create dataflow EventsOut
      EventBusSource -> outstream<CollectdMetric>
      {

      }
      AMQPSink(outstream)
      {
        collector: {class: \"org.cad.interruptus.EventToAMQP\"},
        host:               \"localhost\",
        exchange:           \"alerts\",
        queueName:          \"alerts\",
        username:           \"guest\",
        password:           \"guest\",
        routingKey:         \"#\",
        declareAutoDelete:  false,
        declareDurable:     true,
        logMessages:        true
    }
  "
}'

# curl -X POST http://localhost:8080/api/flow/EventsIn/start | python -m json.tool
# curl -X POST http://localhost:8080/api/flow/EventsOut/start | python -m json.tool
# curl -X POST http://localhost:8080/api/statement/eventlogdebug/start | python -m json.tool


curl -X GET http://localhost:8080/api/flow/EventsIn/state | python -m json.tool
curl -X GET http://localhost:8080/api/flow/EventsOut/state | python -m json.tool
curl -X GET http://localhost:8080/api/statement/eventlogdebug/state | python -m json.tool