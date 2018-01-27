# blockchain-zero

Implemented some ideas at the heart of blockchain.

Server is running on http://localhost:8080 and has 2 endpoints:

* *POST /add_data* add data in json format `{ data: "string of data" }`
* *GET /last_blocks/:count* shows `count` of last blocks

Block format:

```
{
  previous_block_hash: '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08',
  rows: ['data1','data2','data3','data4','data5'],
  timestamp: 12123889,
  block_hash: '1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014'
}
```

## Run

```sh
lein run
```

## Or with Docker

```sh
docker build -t blockchain-zero .
docker run -it --rm -p 8080:8080 blockchain-zero
```

## REST API

### Get last blocks

```sh
curl http://localhost:8080/blockchain/get_blocks/10
```

### Add transaction

```sh
curl -H "Content-Type: application/json" -d '{"from":"95", "to":"93", "amount":10}' http://localhost:8080/management/add_transaction
```

### Add neighbor

```sh
curl -H "Content-Type: application/json" -d '{"id":95, "url":"http://192.168.44.95:8080"}' http://localhost:8080/management/add_link
```

### Get status

```sh
curl http://localhost:8080/management/status
```

### Register node

```sh
curl -X POST -H "Content-Type: application/json" -d '{
    "id": 93,
    "name": "ROFLMAO",
    "url": "192.168.44.93:8080"
}' "http://192.168.44.61:8080/add_node"

```
