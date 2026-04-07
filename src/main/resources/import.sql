-- =====================================================
-- SEED DATA - Dados iniciais para testes
-- =====================================================

-- Usuário ADMINISTRADOR
-- Username: admin, Senha: admin123
INSERT INTO Usuario (id, username, senha, perfil, email, dataCadastro) 
VALUES (
    1, 
    'admin', 
    'L8WgDw8LUrM6YCfnW8/YM1Nhp0il+oTyhaUJNFNmZLfKXnVmwDXUIKNoI2UqdmRtne6VbKDWsNX1Qcu5TPwL2A==',
    1,
    'admin@fma.palmas.to.gov.br',
    CURRENT_TIMESTAMP
);

-- Usuário USER
-- Username: user, Senha: user123  
INSERT INTO Usuario (id, username, senha, perfil, email, dataCadastro) 
VALUES (
    2, 
    'user', 
    '1hqwsHJyIo1VwsLkMdoOBIIj/pe8FtP0LYkNhKpcikh0bc8EMajBDr6gKJVPWff+I3y/D3SMZRgpMpxy9MRehg==',
    2,
    'user@teste.com',
    CURRENT_TIMESTAMP
);

-- Evento de exemplo
INSERT INTO Evento (
    id, nome, descricao, dataHora, local, categoria, 
    organizador, contato, requisitos, participantes, 
    imagemPrincipal, linkInscricao
) VALUES (
    1,
    'Palestra sobre Sustentabilidade',
    'Evento de conscientização ambiental promovido pela FMA',
    TIMESTAMP '2024-12-15 14:00:00',
    'Auditório FMA',
    'Palestra',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Trazer caneta e caderno',
    100,
    null,
    'https://forms.google.com/exemplo'
);

-- Ajustar sequences
ALTER SEQUENCE Evento_SEQ RESTART WITH 100;