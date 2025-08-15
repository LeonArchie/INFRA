// vars/createNamespace.groovy

/**
 * Создает неймспейс в Kubernetes, если он не существует
 * @param namespace Имя неймспейса (обязательный)
 * @return Результат операции
 */
def call(String namespace) {
    try {
        // Проверяем существование неймспейса
        echo "🔍 Checking if namespace ${namespace} exists..."
        def namespaceExists = sh(
            script: "kubectl get namespace ${namespace} >/dev/null 2>&1 && echo 'exists' || echo 'not_exists'",
            returnStdout: true
        ).trim()

        if (namespaceExists == 'not_exists') {
            echo "🆕 Creating namespace: ${namespace}"
            sh "kubectl create namespace ${namespace}"
            return [status: 'CREATED', namespace: namespace]
        } else {
            echo "ℹ️ Namespace ${namespace} already exists"
            return [status: 'EXISTS', namespace: namespace]
        }
    } catch (Exception e) {
        error "❌ Failed to create namespace: ${e.getMessage()}"
    }
}