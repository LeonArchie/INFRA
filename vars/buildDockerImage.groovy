// vars/buildDockerImage.groovy

/**
 * Собирает Docker образ из указанной директории
 * @param config Map с параметрами:
 *   - imageName (String): Полное имя образа (обязательное)
 *   - contextDir (String): Директория с Dockerfile (по умолчанию '.')
 * @return void
 * 
 * Примеры использования:
 * 1. buildDockerImage(imageName: 'my-image:latest')
 * 2. buildDockerImage(imageName: 'my-image:1.0', contextDir: 'docker')
 */
def call(Map config = [:]) {
    // Валидация параметров
    if (!config.imageName) {
        error("Parameter 'imageName' is required")
    }
    
    def contextDir = config.contextDir ?: '.'
    
    echo """
    🛠️ Building Docker image:
    - Image: ${config.imageName}
    - Context: ${contextDir}
    """
    
    try {
        // Проверяем существование Dockerfile в указанной директории
        def dockerfilePath = "${contextDir}/Dockerfile"
        if (!fileExists(dockerfilePath)) {
            error("Dockerfile not found in ${contextDir}")
        }
        
        // Выводим информацию о Dockerfile
        sh """
        echo "📄 Dockerfile content (first 20 lines):"
        head -20 ${dockerfilePath} || true
        """
        
        // Собираем образ
        docker.build(config.imageName, "${contextDir}/")
        
        echo "✅ Image built successfully"
    } catch (Exception e) {
        error("❌ Docker build failed: ${e.getMessage()}")
    }
}