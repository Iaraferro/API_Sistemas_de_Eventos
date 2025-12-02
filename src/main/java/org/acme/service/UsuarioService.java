package org.acme.service;

import java.util.List;

import jakarta.validation.Valid;
import org.acme.dto.UsuarioDTO;
import org.acme.dto.UsuarioResponseDTO;

public interface UsuarioService {

    UsuarioResponseDTO create(@Valid UsuarioDTO dto);
    List<UsuarioResponseDTO> findAll();
    UsuarioResponseDTO findById(Long id);
    UsuarioResponseDTO findByUsername(String username);
    UsuarioResponseDTO findByUsernameAndSenha(String username, String senha);
    UsuarioResponseDTO update(Long id, UsuarioDTO dto);
    void delete(Long id);
}