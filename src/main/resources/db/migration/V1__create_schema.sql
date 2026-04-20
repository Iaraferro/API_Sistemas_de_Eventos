-- =====================================================
-- SCHEMA - Sistema de Eventos FMA
-- =====================================================

CREATE TABLE IF NOT EXISTS Usuario (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(30)  UNIQUE NOT NULL,
    senha       VARCHAR(120) NOT NULL,
    perfil      INTEGER      NOT NULL,
    email       VARCHAR(255),
    dataCadastro  TIMESTAMP,
    dataAlteracao TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Evento (
    id              BIGINT       PRIMARY KEY,
    nome            VARCHAR(255) NOT NULL,
    descricao       VARCHAR(5000) NOT NULL,
    dataHora        TIMESTAMP,
    categoria       VARCHAR(255),
    local           VARCHAR(255),
    contato         VARCHAR(255),
    requisitos      VARCHAR(255),
    participantes   INTEGER,
    organizador     VARCHAR(255),
    imagemPrincipal VARCHAR(255),
    linkInscricao   VARCHAR(255)
);

CREATE SEQUENCE IF NOT EXISTS Evento_SEQ START WITH 100 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS Evento_arquivos (
    Evento_id BIGINT NOT NULL REFERENCES Evento(id),
    arquivos  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Inscricao (
    id             BIGSERIAL PRIMARY KEY,
    evento_id      BIGINT REFERENCES Evento(id),
    nome           VARCHAR(255),
    email          VARCHAR(255),
    telefone       VARCHAR(255),
    dataInscricao  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ArquivoEvento (
    id           BIGSERIAL PRIMARY KEY,
    nomeOriginal VARCHAR(255),
    nomeSalvo    VARCHAR(255),
    mimeType     VARCHAR(255),
    dataUpload   TIMESTAMP,
    evento_id    BIGINT REFERENCES Evento(id)
);