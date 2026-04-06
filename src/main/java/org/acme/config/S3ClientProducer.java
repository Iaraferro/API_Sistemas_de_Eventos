package org.acme.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.jboss.logging.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

import java.net.URI;

@ApplicationScoped
public class S3ClientProducer {

    private static final Logger LOG = Logger.getLogger(S3ClientProducer.class);
    
    private static final String BUCKET_NAME = "eventos-arquivos";

    @Produces
    public S3Client s3Client() {
        String endpoint = "http://localhost:9000";
        String accessKey = "admin";
        String secretKey = "password";
        String region = "us-east-1";

        LOG.info("🔧 Configurando cliente S3/MinIO...");

        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .region(Region.of(region))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();

        // ✅ MELHORIA: Cria bucket automaticamente se não existir
        criarBucketSeNaoExistir(client, BUCKET_NAME);

        return client;
    }

    /**
     * Cria o bucket no MinIO se ele ainda não existir
     * Evita erro "Bucket not found" no primeiro uso
     */
    private void criarBucketSeNaoExistir(S3Client client, String bucketName) {
        try {
            // Tenta verificar se o bucket existe
            client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            
            LOG.infov("✅ Bucket ''{0}'' já existe e está acessível.", bucketName);
            
        } catch (Exception e) {
            // Bucket não existe, vamos criá-lo
            try {
                client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build());
                
                LOG.infov("✅ Bucket ''{0}'' criado com sucesso no MinIO!", bucketName);
                
            } catch (Exception ex) {
                LOG.errorv("❌ Erro ao criar bucket ''{0}'': {1}", bucketName, ex.getMessage());
                LOG.error("Verifique se o MinIO está rodando em " + "http://localhost:9000");
            }
        }
    }
}