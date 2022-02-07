package main

import (
	"fmt"
	"net/http"

	"github.com/gorilla/mux"
)

func main() {

	r := mux.NewRouter()

	shipsRouter := r.PathPrefix("/ships").Subrouter()
	shipsRouter.Methods(http.MethodPost).HandlerFunc(CreateShipHandler)
	shipsRouter.Methods(http.MethodGet).HandlerFunc(ListShipsHandler)

	fmt.Println("Listening on port 3000")
	http.ListenAndServe(":3000", r)
}

func CreateShipHandler(w http.ResponseWriter, r *http.Request) {
	

	w.WriteHeader(http.StatusOK)
}

func ListShipsHandler(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusOK)
	fmt.Println("Hello world")
}
