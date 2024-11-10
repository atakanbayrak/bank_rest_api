# Sekom Bank Applikasyonu

### Çalıştırma Rehberi

Docker deamon çalışır durumda olmalıdır.

* [Ana dizin üzerinde "docker-compose up" yaparak çalıştırabilirsiniz.]()
* [Bağımlılıklar düzenlendiği için öncelikli bir uygulama ayağa kaldırmanıza gerek yoktur.]()
* [Yapılan herhangi bir değişiklik sonrası "mvn clean package" yapılması ardından image yaratılması gerekli.]()

### Kullanılan Teknolojiler

* [Spring Boot]()
* [Redis]()
* [PostgreSQL]()
* [Prometheus]()
* [Grafana]()
* [Docker]()
* [Maven Build]()
* [Lombok]()
* [Swagger]()

### Mimari

Proje genelinde 5 katmanlı bir mimari kullanılmıştır. Bu katmanlar;
- [Controller]()
- [Service]()
- [Repository]()
- [Model]()
- [Configuration]()

### Entities

- [Bank]()
- [Account]()
- [Transaction]()
- [AccountHolder]()

### Relations

- [Bank - Account] -> (One to Many)
- [Account - Transaction] -> (One to Many)
- [AccountHolder - Transaction] -> (One to Many)
- [Account - AccountHolder] -> (One to One)

### Data Transfer Objeleri

- [BankDTO]()
- [AccountDTO]()
- [TransactionDTO]()
- [AccountHolderDTO]()

### Request Objeleri

- [BankRequest]()
- [AccountRequest]()
- [TransactionRequest]()
- [AccountHolderRequest]()

### Konfigürasyonlar

- [RedisConfiguration]()
- [SwaggerConfiguration]()
- [PrometheusConfiguration]()

### Docker Temelli Servisler

- [PostgreSQL]()
- [Redis]()
- [Prometheus]()
- [Grafana]()
- [Spring Boot App]()





