---
name: EstruturaAPI
description: Guia de estrutura do backend Quarkus da API EcoEventos (Java 17, Hibernate Panache, JWT SmallRye, PostgreSQL, MinIO/S3). Use sempre que precisar entender, modificar ou criar resources, DTOs, services, repositórios ou endpoints REST.
---

# Estrutura da API - EcoEventos (Quarkus)

## Visão Geral

Backend REST do sistema **EcoEventos Palmas** para gerenciamento de eventos com autenticação JWT, perfis de acesso (Adm/User) e upload de arquivos em MinIO (compatível com S3).

- **Framework**: Quarkus 3.16.3
- **Linguagem**: Java 17
- **Persistência**: Hibernate ORM + Panache (PostgreSQL)
- **Autenticação**: JWT SmallRye (RSA pública/privada)
- **Hash de senhas**: PBKDF2 + HMAC-SHA512 (HashService)
- **Armazenamento**: MinIO via AWS SDK S3
- **Validação**: Jakarta Bean Validation
- **Documentação**: OpenAPI (SmallRye)
- **Porta**: `8080` (testes em `8082`)

## Stack & Dependências Chave

| Dependência | Função |
|---|---|
| `quarkus-rest` / `quarkus-rest-jackson` | Endpoints REST + JSON |
| `quarkus-hibernate-orm-panache` | ORM com Panache |
| `quarkus-jdbc-postgresql` | Driver PostgreSQL |
| `quarkus-smallrye-jwt` | Autenticação JWT |
| `quarkus-hibernate-validator` | Validação Bean |
| `quarkus-smallrye-openapi` | Swagger UI / OpenAPI |
| `software.amazon.awssdk:s3` | Cliente S3/MinIO |
| `org.mindrot:jbcrypt` | Hashing auxiliar |

## Estrutura de Pastas

```
API_Sistemas_de_Eventos/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/org/acme/
│   │   │   ├── config/
│   │   │   │   ├── GlobalExceptionMapper.java       # Tratamento global de exceções
│   │   │   │   ├── ValidationExceptionMapper.java   # Erros de validação (400)
│   │   │   │   └── S3ClientProducer.java            # Cria S3Client + bucket "eventos-arquivos"
│   │   │   ├── dto/                                 # Records JSON (entrada/saída)
│   │   │   │   ├── AuthDTO.java                     # username, senha
│   │   │   │   ├── UsuarioDTO.java                  # nome, email, username, senha, id_perfil
│   │   │   │   ├── UsuarioResponseDTO.java
│   │   │   │   ├── EventoDTO.java                   # DTO completo do evento (com @Valid)
│   │   │   │   ├── EventoResponseDTO.java
│   │   │   │   ├── ArquivoDTO.java / ArquivoResponseDTO.java
│   │   │   │   └── UploadArquivoEventoDTO.java
│   │   │   ├── model/                               # Entidades JPA
│   │   │   │   ├── DefaultEntity.java               # Base com id
│   │   │   │   ├── Usuario.java                     # username, senha, email, perfil
│   │   │   │   ├── Evento.java                      # PanacheEntity (id auto)
│   │   │   │   ├── ArquivoEvento.java
│   │   │   │   ├── Perfil.java                      # ENUM: ADM(1,"Adm"), USER(2,"User")
│   │   │   │   └── converterjpa/PerfilConverter.java
│   │   │   ├── repository/                          # Panache repositories
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── EventoRepository.java
│   │   │   │   └── ArquivoEventoRepository.java
│   │   │   ├── service/                             # Regras de negócio
│   │   │   │   ├── UsuarioService.java + Impl
│   │   │   │   ├── EventoService.java
│   │   │   │   ├── ArquivoEventoService.java
│   │   │   │   ├── FileService.java                 # Upload/download MinIO
│   │   │   │   ├── HashService.java + Impl          # PBKDF2 HMAC-SHA512
│   │   │   │   └── JwtService.java + Impl           # Gera token JWT
│   │   │   └── resource/                            # Endpoints REST
│   │   │       ├── AuthResource.java                # POST /auth
│   │   │       ├── UsuarioResource.java             # /usuarios
│   │   │       ├── EventoResource.java              # /eventos
│   │   │       ├── EventoImagemResource.java        # /eventos/{id}/imagem
│   │   │       ├── ArquivoEventoResource.java       # /eventos/{id}/arquivos
│   │   │       └── FileUploadResource.java          # /arquivos
│   │   ├── resources/
│   │   │   ├── application.properties               # DB, JWT, MinIO, CORS
│   │   │   ├── import.sql                           # Seed inicial
│   │   │   └── token/
│   │   │       ├── privateKey.pem                   # Assinatura JWT
│   │   │       └── publicKey.pem                    # Verificação JWT
│   │   └── docker/                                  # Dockerfiles (jvm, native, legacy)
│   └── test/java/org/acme/                          # Testes REST-assured + JUnit5
└── target/                                          # Build Maven
```

## Endpoints

### Autenticação — `AuthResource` (público)

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| POST | `/auth` | Login (body: `AuthDTO`) | 200 + token JWT (text/plain) + header `Authorization: Bearer <token>` |

- Gera JWT via `JwtService.generateJwt(username, perfilNome)`.
- Subject = username; groups = nome do perfil (`Adm` ou `User`).
- 401 se credenciais inválidas.

### Usuários — `UsuarioResource` (`/usuarios`)

| Método | Rota | Roles | Descrição |
|---|---|---|---|
| POST | `/usuarios` | público | Criar usuário (auto-registro) |
| GET | `/usuarios` | Adm | Listar todos |
| GET | `/usuarios/{id}` | Adm | Buscar por ID |
| GET | `/usuarios/perfil` | Adm, User | Perfil do usuário logado (via JWT subject) |
| PUT | `/usuarios/{id}` | Adm, User | Atualizar (User só os próprios dados) |
| DELETE | `/usuarios/{id}` | Adm | Deletar |

### Eventos — `EventoResource` (`/eventos`)

| Método | Rota | Roles | Descrição |
|---|---|---|---|
| GET | `/eventos` | público | Listar todos |
| GET | `/eventos/{id}` | público | Buscar por ID |
| GET | `/eventos/health` | público | Health check |
| POST | `/eventos` | Adm | Criar (body: `EventoDTO` validado) |
| PUT | `/eventos/{id}` | Adm | Atualizar |
| DELETE | `/eventos/{id}` | Adm | Deletar |

### Imagem Principal do Evento — `EventoImagemResource`

| Método | Rota | Roles | Descrição |
|---|---|---|---|
| POST | `/eventos/{idEvento}/imagem` | Adm | Upload multipart (`imagem`, `nomeArquivo`). Valida MIME image/*. Retorna `{nomeArquivo, urlAcesso}` |
| DELETE | `/eventos/{idEvento}/imagem` | Adm | Remove imagem principal |

### Arquivos Anexos do Evento — `ArquivoEventoResource`

| Método | Rota | Roles | Descrição |
|---|---|---|---|
| POST | `/eventos/{idEvento}/arquivos` | Adm | Upload multipart (`arquivo`, `nomeArquivo`) |

### Arquivos (MinIO) — `FileUploadResource` (`/arquivos`)

| Método | Rota | Roles | Descrição |
|---|---|---|---|
| POST | `/arquivos/upload` | público | Upload genérico multipart |
| GET | `/arquivos/{nome}` | público | Download (retorna bytes + mime detectado) |
| DELETE | `/arquivos/{nome}` | público | Deletar do bucket |

> ⚠️ `FileUploadResource` hoje não tem `@RolesAllowed` — considerar proteger em mudanças futuras.

## Autenticação & Segurança

### Fluxo JWT
1. Cliente → `POST /auth` com `{username, senha}`.
2. `HashService.getHashSenha(senha)` gera PBKDF2-HMAC-SHA512.
3. `UsuarioService.findByUsernameAndSenha` valida.
4. `JwtService.generateJwt` cria token assinado com `token/privateKey.pem`.
5. Cliente envia `Authorization: Bearer <token>` em requisições protegidas.
6. Quarkus valida com `token/publicKey.pem`.

### Configuração JWT (`application.properties`)
```properties
mp.jwt.verify.issuer=unitins-jwt
mp.jwt.verify.publickey.location=token/publicKey.pem
smallrye.jwt.sign.key.location=token/privateKey.pem
```

### Roles (`@RolesAllowed`)
- `"Adm"` — administrador pleno.
- `"User"` — usuário comum.
- Nomes vêm de `Perfil.NOME` (enum): `ADM(1,"Adm")`, `USER(2,"User")`.

## Entidades Principais

### Evento (`PanacheEntity`)
```java
private String nome;
private String descricao;
private LocalDateTime dataHora;
private String categoria;
private String local;
private String contato;
private String requisitos;
private Integer participantes;
private String organizador;
private String imagemPrincipal;        // nome do arquivo no MinIO
private String linkInscricao;          // URL externa (Google Forms)
@ElementCollection
private List<String> arquivos;         // nomes de anexos no MinIO
```

### Usuario (`DefaultEntity`)
```java
@Column(length = 30, unique = true) String username;
@Column(length = 120) String senha;    // hash PBKDF2
Perfil perfil;                          // Enum convertido via PerfilConverter
String email;
```

### Validações principais no `EventoDTO`
- `@NotBlank`: nome, descricao, local
- `@NotNull @Future`: dataHora
- `@Email`: contato
- `@Min(0)`: participantes
- `@Pattern("^https?://.*")`: linkInscricao

## Configuração Infra

### PostgreSQL
```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=123456
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/eventos
quarkus.hibernate-orm.database.generation=drop-and-create
```
> ⚠️ `drop-and-create` apaga o banco a cada restart. `import.sql` faz o seed.

### MinIO / S3 (`S3ClientProducer`)
- Endpoint: `http://localhost:9000`
- Credenciais: `admin` / `password`
- Bucket: `eventos-arquivos` (criado automaticamente no startup)
- Region: `us-east-1`, path-style habilitado

### CORS
- `quarkus.http.cors=true`
- Origens: `*`
- Métodos: `GET, POST, PUT, DELETE, OPTIONS, PATCH`
- Headers: `accept, authorization, content-type, x-requested-with, origin`
- Credenciais permitidas, cache 24h

## Convenções & Padrões

### Arquitetura em camadas
```
resource/  →  service/  →  repository/  →  model/
              ↑
              dto/ (entrada/saída)
```

- **Resources** apenas orquestram: `@Inject` service, `@Valid` no DTO, retornam `Response`.
- **DTOs são `record`s** imutáveis com validação Jakarta.
- **Services** carregam regra de negócio e `@Transactional` quando necessário.
- **Repositories** estendem `PanacheRepository` ou `PanacheEntity` direto.
- **Exceções** são lançadas livremente — `GlobalExceptionMapper` as transforma em respostas HTTP.

### Naming
- Entidades: PascalCase (`Evento`, `Usuario`, `ArquivoEvento`)
- DTOs: sufixo `DTO` / `ResponseDTO`
- Services: sufixo `Service` + `ServiceImpl` para bindings
- Resources: sufixo `Resource`
- Paths REST: plural em kebab/lowercase (`/eventos`, `/usuarios`)

### Logging
- Usar `org.jboss.logging.Logger` (`LOG.infov("msg {0}", var)`).

## Como Adicionar um Novo Endpoint

1. **DTO**: criar `record` em `dto/` com validações Jakarta.
2. **Entidade**: adicionar/modificar em `model/` se necessário (lembrando do `drop-and-create` e do `import.sql`).
3. **Repository**: criar classe em `repository/` estendendo `PanacheRepository<T>` se precisar de queries customizadas.
4. **Service**: criar interface + `Impl` com `@ApplicationScoped`, injetar `Repository`.
5. **Resource**: criar classe em `resource/` com `@Path`, anotar métodos com `@GET/@POST/@PUT/@DELETE`, `@Valid`, `@RolesAllowed`, `@Transactional` para escrita.
6. Se o endpoint precisar de identidade do usuário: `@Inject JsonWebToken jwt;` e `jwt.getSubject()`.

## Comandos Úteis

```bash
# Dev mode (hot reload na porta 8080)
./mvnw quarkus:dev

# Build JAR
./mvnw package

# Build nativo (GraalVM)
./mvnw package -Dnative

# Rodar testes
./mvnw test

# Docker (após build)
docker build -f src/main/docker/Dockerfile.jvm -t ecoeventos-api .
docker run -p 8080:8080 ecoeventos-api

# Limpar target
./limpar.ps1   # script PowerShell local
```

### Endpoints úteis durante dev
- Swagger UI: `http://localhost:8080/q/swagger-ui/`
- OpenAPI JSON: `http://localhost:8080/q/openapi`
- Dev UI: `http://localhost:8080/q/dev/`
- Health: `http://localhost:8080/eventos/health`

## Docker (sem Java local)

O projeto possui configuração Docker completa. Nenhum Java ou Maven é necessário no host.

### Arquivos Docker
| Arquivo | Descrição |
|---|---|
| `Dockerfile` | Multi-stage: Stage 1 compila com Maven 3.9 + JDK 17; Stage 2 roda com JRE 17 |
| `docker-compose.yml` | Sobe API + PostgreSQL 15 + MinIO |
| `.dockerignore` | Exclui `target/` e arquivos desnecessários do contexto |

### Comandos rápidos
```bash
# Subir tudo (primeira vez compila a imagem — pode demorar ~3-5 min)
docker compose up -d --build

# Subir sem recompilar imagem
docker compose up -d

# Ver logs da API em tempo real
docker compose logs -f api

# Parar sem apagar dados
docker compose down

# Apagar containers e volumes (banco e MinIO zerados)
docker compose down -v
```

### Portas expostas
| Serviço | Porta | URL |
|---|---|---|
| API Quarkus | 8080 | `http://localhost:8080` |
| PostgreSQL | 5432 | `localhost:5432` (banco `eventos`) |
| MinIO API | 9000 | `http://localhost:9000` |
| MinIO Console | 9001 | `http://localhost:9001` (admin/password) |

### Variáveis de ambiente (docker-compose.yml)
O Docker sobrescreve o `application.properties` via env vars (Quarkus auto-mapping):
```
QUARKUS_DATASOURCE_JDBC_URL   → jdbc:postgresql://db:5432/eventos
QUARKUS_DATASOURCE_USERNAME   → postgres
QUARKUS_DATASOURCE_PASSWORD   → 123456
MINIO_ENDPOINT                → http://minio:9000
MINIO_ACCESS_KEY              → admin
MINIO_SECRET_KEY              → password
```

### Angular conectando ao Docker
O Angular já aponta para `http://localhost:8080` — nenhuma mudança necessária. Os containers expõem as portas no `localhost` da máquina.

## Pré-requisitos Runtime

### Opção A — Docker (recomendado, sem Java)
Apenas **Docker Desktop** instalado e rodando. Executar `docker compose up -d --build`.

### Opção B — Local (requer Java 17)
1. **PostgreSQL** em `localhost:5432` com banco `eventos` (user `postgres` / senha `123456`).
2. **MinIO** em `localhost:9000` (user `admin` / senha `password`). Bucket `eventos-arquivos` é criado automaticamente no startup do `S3ClientProducer`.
3. Executar `./mvnw quarkus:dev`.

## Checklist ao Modificar a API

- [ ] DTO com validações Jakarta apropriadas
- [ ] `@RolesAllowed` nos endpoints sensíveis
- [ ] `@Transactional` em operações de escrita
- [ ] Exceções deixadas para o `GlobalExceptionMapper` (não retornar 500 genérico manualmente)
- [ ] Logging com `LOG.infov` incluindo quem chamou (`jwt.getSubject()`)
- [ ] Atualizar `import.sql` se o schema mudar (banco é recriado a cada restart)
- [ ] Testar via Swagger UI (`/q/swagger-ui/`) antes de integrar com o Angular
