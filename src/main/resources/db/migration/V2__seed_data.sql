-- =====================================================
-- DADOS INICIAIS - Um admin, um user, um evento exemplo
-- =====================================================

-- Usuário ADMINISTRADOR (senha: admin123)
INSERT INTO Usuario (id, username, senha, perfil, email, dataCadastro)
VALUES (
    1,
    'admin',
    'L8WgDw8LUrM6YCfnW8/YM1Nhp0il+oTyhaUJNFNmZLfKXnVmwDXUIKNoI2UqdmRtne6VbKDWsNX1Qcu5TPwL2A==',
    1,
    'admin@fma.palmas.to.gov.br',
    CURRENT_TIMESTAMP
);

-- Usuário USER (senha: user123)
INSERT INTO Usuario (id, username, senha, perfil, email, dataCadastro)
VALUES (
    2,
    'user',
    '1hqwsHJyIo1VwsLkMdoOBIIj/pe8FtP0LYkNhKpcikh0bc8EMajBDr6gKJVPWff+I3y/D3SMZRgpMpxy9MRehg==',
    2,
    'user@teste.com',
    CURRENT_TIMESTAMP
);