{:transport {:port 1337 :ssl {:ca-cert "/etc/warp/certs/ca-crt.pem"
                              :cert    "/etc/warp/certs/server.pem"
                              :pkey    "/etc/warp/certs/server.key."}}
 :api {:port 8593
       :origins ["http://127.0.0.1:*" "http://localhost:*"]}
 :scenarios "site/scenarios"
 :keepalive 30}
