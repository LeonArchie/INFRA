// vars/deployToRegistry.groovy

/**
 * Публикует Docker образ в registry
 * @param config Map с параметрами:
 *   - imageName (String): Полное имя образа (обязательное)
 *   - registryUrl (String): URL registry (обязательное)
 *   - credentialsId (String): ID credentials (обязательное)
 * @return void
 * 
 * Примеры использования:
 * 1. deployToRegistry(
 *      imageName: 'my-image:latest',
 *      registryUrl: 'ghcr.io',
 *      credentialsId: 'docker-creds'
 *    )
 */
def call(Map config = [:]) {
    // Валидация параметров
    def requiredParams = ['imageName', 'registryUrl', 'credentialsId']
    def missingParams = requiredParams.findAll { !config[it] }
    if (missingParams) {
        error("Missing required parameters: ${missingParams.join(', ')}")
    }
    
    echo """
    🚀 Deploying Docker image:
    - Image: ${config.imageName}
    - Registry: ${config.registryUrl}
    """
    
    try {
        // Логинимся в registry
        withCredentials([usernamePassword(
            credentialsId: config.credentialsId,
            usernameVariable: 'REGISTRY_USER',
            passwordVariable: 'REGISTRY_TOKEN'
        )]) {
            sh """
            echo \${REGISTRY_TOKEN} | docker login ${config.registryUrl} \
                -u \${REGISTRY_USER} \
                --password-stdin
            """
        }
        
        // Публикуем образ
        docker.withRegistry("https://${config.registryUrl}", config.credentialsId) {
            docker.image(config.imageName).push()
        }
        
        echo "✅ Image pushed successfully to ${config.registryUrl}"
    } catch (Exception e) {
        error("❌ Failed to deploy image: ${e.getMessage()}")
    }
}