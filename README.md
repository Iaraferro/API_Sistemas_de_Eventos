# EcoEventos API

Backend de uma aplicação de gerenciamento de eventos, 
desenvolvido com **Quarkus** e **Java**.

## 🚀 Tecnologias

- **Quarkus** — framework Java nativo para cloud
- **Hibernate ORM + Panache** — persistência simplificada
- **JWT (SmallRye)** — autenticação stateless com tokens
- **MinIO** — armazenamento de arquivos (imagens e documentos)
- **Jakarta Validation** — validação de dados de entrada
- **RESTEasy Reactive** — endpoints REST reativos

## 📦 Funcionalidades

- Cadastro e autenticação de usuários com perfis (ADM / USER)
- Senhas protegidas com hash PBKDF2 + HMAC-SHA512
- Geração e validação de tokens JWT
- CRUD completo de eventos (nome, descrição, data, local, 
  categoria, organizador, contato, requisitos, participantes)
- Upload de arquivos e imagens vinculados a eventos via MinIO
- Conversão automática de enum `Perfil` para banco de dados

## 🔐 Segurança

A autenticação é feita via `POST /auth`, retornando um token 
JWT que deve ser enviado no header `Authorization: Bearer <token>` 
nas requisições protegidas.

## 📁 Estrutura

src/
├── model/        # Entidades JPA
├── dto/          # Objetos de transferência de dados
├── repository/   # Repositórios Panache
├── service/      # Regras de negócio
└── resource/     # Endpoints REST
