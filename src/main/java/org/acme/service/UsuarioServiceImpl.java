package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.acme.dto.UsuarioDTO;
import org.acme.dto.UsuarioResponseDTO;
import org.acme.model.Perfil;
import org.acme.model.Usuario;
import org.acme.repository.UsuarioRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ApplicationScoped
public class UsuarioServiceImpl implements UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    HashService hashService;

    @Inject
    Validator validator;

    @Override
    @Transactional
    public UsuarioResponseDTO create(UsuarioDTO dto) {
        validarDTO(dto);
        validarRegrasDeNegocio(dto);

        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(dto.username());
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());

        try {
            novoUsuario.setSenha(hashService.getHashSenha(dto.senha()));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o hash da senha.", e);
        }

        novoUsuario.setPerfil(Perfil.valueOf(dto.id_perfil()));

        usuarioRepository.persist(novoUsuario);

        return UsuarioResponseDTO.valueOf(novoUsuario);
    }

    private void validarDTO(UsuarioDTO dto) {
        Set<ConstraintViolation<UsuarioDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {

            throw new ConstraintViolationException(violations);
        }
    }

    private void validarRegrasDeNegocio(UsuarioDTO dto) {
        if (usuarioRepository.findByUsername(dto.username()) != null) {
            throw new ValidationException("O username '" + dto.username() + "' já está em uso.");
        }

        if (usuarioRepository.findByEmail(dto.email()) != null) {
            throw new ValidationException("O e-mail '" + dto.email() + "' já está em uso.");
        }
    }

    @Override
    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        validarDTO(dto);

        usuario.setUsername(dto.username());
        usuario.setEmail(dto.email());

        if (dto.senha() != null && !dto.senha().trim().isEmpty()) {
            try {
                usuario.setSenha(hashService.getHashSenha(dto.senha()));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao gerar o hash da senha.", e);
            }
        }

        return UsuarioResponseDTO.valueOf(usuario);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!usuarioRepository.deleteById(id)) {
            throw new NotFoundException("Usuário não encontrado para exclusão.");
        }
    }

    @Override
    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.listAll().stream()
                .map(UsuarioResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }
        return UsuarioResponseDTO.valueOf(usuario);
    }

    @Override
    public UsuarioResponseDTO findByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }
        return UsuarioResponseDTO.valueOf(usuario);
    }

    @Override
    public UsuarioResponseDTO findByUsernameAndSenha(String username, String senha) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario != null) {
            try {
                if (senha.equals(usuario.getSenha())) {
                    return UsuarioResponseDTO.valueOf(usuario);
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro ao gerar o hash da senha.", e);
            }
        }
        return null;
    }
}
