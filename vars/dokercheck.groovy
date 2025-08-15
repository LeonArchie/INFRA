// vars/checkDocker.groovy

/**
 * Проверяет наличие и версию Docker на текущем агенте Jenkins
 * 
 * @param failIfMissing (Boolean) - Прерывать выполнение если Docker не найден (по умолчанию true)
 * @return String - Версия Docker или null если не установлен (при failIfMissing=false)
 * @throws Exception - Если Docker не найден и failIfMissing=true
 * 
 * Примеры использования:
 * 1. checkDocker() // Просто проверить, прервать если нет Docker
 * 2. def version = checkDocker(failIfMissing: false) // Проверить и получить версию
 */
def call(Map params = [:]) {
    def failIfMissing = params.get('failIfMissing', true)
    
    try {
        // Получаем версию Docker
        def version = sh(
            script: 'docker --version',
            returnStdout: true
        ).trim()
        
        echo "✅ Docker detected: ${version}"
        return version
        
    } catch (Exception e) {
        def errorMsg = "Docker is not available: ${e.getMessage()}"
        
        if (failIfMissing) {
            error "❌ ${errorMsg}\nPlease ensure Docker is installed and configured on the agent."
        } else {
            echo "⚠️ ${errorMsg}"
            return null
        }
    }
}