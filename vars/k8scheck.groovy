// vars/verifyKubernetesAccess.groovy

/**
 * Функция для проверки доступа к Kubernetes кластеру
 * Основные функции:
 * 1. Получает kubeconfig из Jenkins credentials
 * 2. Настраивает временный доступ к кластеру
 * 3. Проверяет соединение с кластером
 * 4. Проверяет доступность нод
 * 5. Очищает временные файлы после выполнения
 *
 * @param config Map с параметрами:
 *   - credentialsId: ID credentials в Jenkins с kubeconfig (по умолчанию 'k8s_cluster_cred')
 */
def call(Map config = [:]) {
    // Получаем ID credentials из параметров или используем значение по умолчанию
    def credentialsId = config.credentialsId ?: 'k8s_cluster_cred'
    
    // Используем Jenkins credentials для получения kubeconfig файла
    withCredentials([file(credentialsId: credentialsId, variable: 'KUBECONFIG_FILE')]) {
        // Создаем безопасный путь к рабочей директории (без пробелов)
        def safeWorkspace = env.WORKSPACE.replace(' ', '_')
        // Устанавливаем переменную окружения KUBECONFIG
        env.KUBECONFIG = "${safeWorkspace}/.kube/config"
        
        // Создаем .kube директорию и копируем kubeconfig
        sh """
            mkdir -p "${safeWorkspace}/.kube"
            cp "${KUBECONFIG_FILE}" "${env.KUBECONFIG}"
            chmod 600 "${env.KUBECONFIG}"  # Устанавливаем безопасные права доступа
        """

        try {
            // Проверка 1: Подключение к кластеру
            echo "🔍 Checking cluster connection..."
            def clusterInfo = sh(script: 'kubectl cluster-info', returnStdout: true).trim()
            echo "✅ Cluster Info:\n${clusterInfo}"

            // Проверка 2: Состояние нод кластера
            echo "🖥️ Checking nodes..."
            def nodes = sh(
                script: 'kubectl get nodes -o wide --no-headers',  # Широкий формат вывода без заголовков
                returnStdout: true
            ).trim()
            echo "Active Nodes:\n${nodes}"

        } catch (Exception e) {
            // В случае ошибки прерываем выполнение с сообщением
            error "❌ Connection failed: ${e.getMessage()}"
        } finally {
            // Всегда очищаем временные файлы, даже при ошибке
            sh "rm -rf ${safeWorkspace}/.kube || true"  # Игнорируем ошибки удаления
        }
    }
}