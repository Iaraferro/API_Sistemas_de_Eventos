package org.acme.service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class FileService {

    @Inject
    S3Client s3Client;

    private final String BUCKET_NAME = "eventos-arquivos";

    public String salvar(String nomeArquivo, byte[] conteudo, String mimeType) {
        String nomeUnico = UUID.randomUUID() + "-" + nomeArquivo;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(nomeUnico)
                .contentType(mimeType)
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromBytes(conteudo));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar arquivo para S3/MinIO: " + e.getMessage(), e);
        }

        return nomeUnico;
    }

    public byte[] baixar(String nomeArquivo) {
        try {
            var resp = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(nomeArquivo)
                    .build());
            return resp.readAllBytes();
        } catch (NoSuchKeyException e) {
            throw new RuntimeException("Arquivo não encontrado: " + nomeArquivo, e);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo do S3/MinIO", e);
        }
    }

    public void deletar(String nomeArquivo) {
    try {
        s3Client.deleteObject(b -> b.bucket(BUCKET_NAME).key(nomeArquivo));
    } catch (Exception e) {
        throw new RuntimeException("Erro ao deletar arquivo no S3/MinIO: " + nomeArquivo, e);
    }
    }

    public List<String> listar() {
        return s3Client.listObjectsV2(b -> b.bucket(BUCKET_NAME))
                .contents()
                .stream()
                .map(s3Object -> s3Object.key())
                .toList();
    }
}
