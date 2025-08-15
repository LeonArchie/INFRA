// vars/createNamespace.groovy

/**
 * Создает неймспейс в Kubernetes, если он не существует
 * @param namespace Имя неймспейса (обязательный)
 * @param credentialsId ID credentials с kubeconfig (опционально)
 * @return Результат операции
 */
def call(Map config = [:]) {
    // Проверяем обязательные параметры
    if (!config.namespace) {
        error "Не указано имя неймспейса (параметр namespace)"
    }

    try {
        // Проверяем существование неймспейса
        echo "🔍 Checking if namespace ${config.namespace} exists..."
        def namespaceExists = sh(
            script: "kubectl get namespace ${config.namespace} >/dev/null 2>&1 && echo 'exists' || echo 'not_exists'",
            returnStdout: true
        ).trim()

        if (namespaceExists == 'not_exists') {
            echo "🆕 Creating namespace: ${config.namespace}"
            
            // Пробуем создать неймспейс с явным указанием контекста
            def createCmd = "kubectl create namespace ${config.namespace}"
            if (config.context) {
                createCmd += " --context=${config.context}"
            }
            
            sh(script: createCmd, returnStatus: true) // Используем returnStatus чтобы не падать при ошибке
            
            // Проверяем, что неймспейс действительно создался
            def verify = sh(
                script: "kubectl get namespace ${config.namespace} >/dev/null 2>&1 && echo 'created' || echo 'failed'",
                returnStdout: true
            ).trim()
            
            if (verify == 'created') {
                echo "✅ Namespace ${config.namespace} successfully created"
                return [status: 'CREATED', namespace: config.namespace]
            } else {
                error "❌ Failed to create namespace ${config.namespace} - check RBAC permissions"
            }
        } else {
            echo "ℹ️ Namespace ${config.namespace} already exists"
            return [status: 'EXISTS', namespace: config.namespace]
        }
    } catch (Exception e) {
        error "❌ Namespace operation failed: ${e.getMessage()}"
    }
}