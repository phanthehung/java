include .env

build:
	docker network create -d bridge nab_hungphan || true
	docker-compose build

run:
	docker-compose up

stop:
	docker-compose down 
