# blockchain-zero

Implemented some ideas at the heart of blockchain.

Server is running on http://localhost:8080 and has 2 endpoints:

* *POST /add_data* add data in json format `{ data: "string of data" }`
* *GET /last_blocks/:count* shows `count` of last blocks

## Run

```sh
lein run
```

## Add data

```sh
curl -H "Content-Type: application/json" -d '{"data":"New data"}' http://localhost:8080/add_data
```
