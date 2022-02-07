package store

import (
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

//accepted, scheduling, ready
type Ship struct {
	gorm.Model
	Name   string
	Status string
}

type ShipStore struct {
}

func NewShipStore() *ShipStore {
	gorm.Open(postgres.New(postgres.Config{}))
	return &ShipStore{}
}
