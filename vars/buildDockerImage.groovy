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
    // Обязательные проверки
    if (!config.imageName) {
        error("Parameter 'imageName' is required")
    }
    
    if (!fileExists('Dockerfile')) {
        error("Dockerfile not found in project root")
    }

    try {
        echo """
        🛠️ Building Docker image from root:
        - Image: ${config.imageName}
        - Using root Dockerfile
        """
        
        // Выводим информацию о Dockerfile для отладки
        sh '''
        echo "=== Dockerfile content ==="
        head -20 Dockerfile
        echo "=== Project structure ==="
        find . -maxdepth 3 -type d
        '''
        
        // Сборка образа
        sh "docker build -t ${config.imageName} ."
        
        echo "✅ Image built successfully"
    } catch (Exception e) {
        error("❌ Docker build failed: ${e.getMessage()}")
    }
}