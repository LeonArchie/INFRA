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
        def dockerfilePath = "${contextDir}/Dockerfile"
        if (!fileExists(dockerfilePath)) {
            error("Dockerfile not found in ${contextDir}")
        }
        
        // Используем абсолютный путь с заменой пробелов
        def safeWorkspace = env.WORKSPACE.replace(' ', '_')
        def absPath = "${safeWorkspace}/${contextDir}"
        
        sh """
        echo "📄 Dockerfile content (first 20 lines):"
        head -20 "${dockerfilePath}"
        echo "Building image from ${contextDir}..."
        docker build -t "${config.imageName}" -f "${dockerfilePath}" .
        """
        
        echo "✅ Image built successfully"
    } catch (Exception e) {
        error("❌ Docker build failed: ${e.getMessage()}")
    }
}