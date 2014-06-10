curl -w "\n" -X POST 'http://localhost:8080/api/type' -H "Content-Type:application/json" -d '{
    "name":"StockTick",
    "properties":{
      "symbol":"string",
      "price":"double",
      "ask":"double",
      "bid":"double"
    }
}'

curl -w "\n" -X POST 'http://localhost:8080/api/type' -H "Content-Type:application/json" -d '{
    "name":"AvgDataStream",
    "properties":{
      "symbol":"string",
      "price":"double",
      "standardDeviation":"double",
      "simpleMovingAverage":"double"
    }
}'

curl -w "\n" -X POST 'http://localhost:8080/api/type' -H "Content-Type:application/json" -d '{
    "name":"Bollinger",
    "properties":{
      "symbol":"string",
      "price":"double",
      "upperBand":"double",
      "lowerBand":"double",
      "bandwidth":"double",
      "middleBand":"double"
    }
}'

curl -w "\n" -X POST 'http://localhost:8080/api/statement' -H "Content-Type:application/json" -d '{
    "name":"PopulateAvgDataStream",
    "masterOnly":false,
    "started":true,
    "debug":false,
    "query":"
        INSERT INTO
            AvgDataStream
        SELECT
            symbol,
            last(price)   as price,
            stddev(price) as standardDeviation,
            avg(price)    as simpleMovingAverage
        FROM
            StockTick.std:groupwin(symbol).win:time(5 days)
        GROUP BY
            symbol
        OUTPUT EVERY 1 minutes
    "
}'

curl -w "\n" -X POST 'http://localhost:8080/api/statement' -H "Content-Type:application/json" -d '{
    "name":"PopulateBollinger",
    "masterOnly":false,
    "started":true,
    "debug":false,
    "query":"
        INSERT INTO
            Bollinger
        SELECT
            symbol,
            price,
            simpleMovingAverage                             as middleBand,
            simpleMovingAverage - 2 * standardDeviation     as lowerBand,
            simpleMovingAverage + 2 * standardDeviation     as upperBand,
            ( 4 * standardDeviation) / simpleMovingAverage  as bandwidth
        FROM
            AvgDataStream
    "
}'

curl -w "\n" -X POST 'http://localhost:8080/api/flow' -H "Content-Type:application/json" -d '{
  "name":"StockTickIn",
  "started":true,
  "masterOnly":false,
  "query":"
    create dataflow StockTickIn AMQPSource -> StockTickIn<StockTick>
    {
        collector:    {class: \"org.cad.interruptus.AMQPJsonToMap\"},
        host:         \"localhost\",
        exchange:     \"stocktick_in\",
        port:         5672,
        username:     \"guest\",
        password:     \"guest\",
        routingKey:   \"#\",
        logMessages:  true
    } EventBusSink(StockTickIn){}"
}'

curl -w "\n" -X POST 'http://localhost:8080/api/flow' -H "Content-Type:application/json" -d '{
  "name":"BollingerOut",
  "masterOnly":true,
  "started":true,
  "query":"
    create dataflow BollingerOut
      EventBusSource -> outstream<Bollinger>
      {

      }
      AMQPSink(outstream)
      {
        collector: {class: \"org.cad.interruptus.EventToAMQP\"},
        host:               \"localhost\",
        exchange:           \"bollinger_out\",
        username:           \"guest\",
        password:           \"guest\",
        routingKey:         \"#\",
        declareAutoDelete:  false,
        declareDurable:     true,
        logMessages:        true
    }
  "
}'

# {"type":"StockTick","body":{"symbol":"RHT","ask":51.55,"bid":51.54,"price":51.54}}
# {"type":"StockTick","body":{"symbol":"MSFT","ask":41.18,"bid":41.17,"price":41.1755}}
# {"type":"StockTick","body":{"symbol":"GOOG","ask":560.08,"bid":559.6,"price":559.93}}
# {"type":"StockTick","body":{"symbol":"COST","ask":117.72,"bid":117.7,"price":117.71}}
# {"type":"StockTick","body":{"symbol":"KO","ask":40.95,"bid":40.94,"price":40.94}}
# {"type":"StockTick","body":{"symbol":"AMZN","ask":327.72,"bid":327.57,"price":327.66}}

curl -w "\n" -X GET http://localhost:8080/api/flow/StockTickIn/state
curl -w "\n" -X GET http://localhost:8080/api/flow/BollingerOut/state
curl -w "\n" -X GET http://localhost:8080/api/statement/PopulateBollinger/state
curl -w "\n" -X GET http://localhost:8080/api/statement/PopulateAvgDataStream/state
