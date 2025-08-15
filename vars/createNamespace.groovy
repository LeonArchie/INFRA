// vars/createNamespace.groovy

/**
 * Создает namespace в Kubernetes, если он не существует
 * 
 * @param config Map с параметрами:
 *   - namespace: Имя namespace для создания (обязательный)
 *   - kubeconfig: Путь к kubeconfig файлу (по умолчанию '$KUBECONFIG')
 *   - credentialsId: ID credentials в Jenkins с kubeconfig (если нужно загрузить)
 */
def call(Map config = [:]) {
    // Проверяем обязательные параметры
    if (!config.namespace) {
        error("Parameter 'namespace' is required")
    }

    def namespace = config.namespace
    def kubeconfig = config.kubeconfig ?: env.KUBECONFIG
    def credentialsId = config.credentialsId

    // Если указан credentialsId, загружаем kubeconfig
    if (credentialsId) {
        withCredentials([file(credentialsId: credentialsId, variable: 'K8S_CRED_FILE')]) {
            kubeconfig = "${env.WORKSPACE}/kubeconfig_${UUID.randomUUID().toString()}"
            sh """
                cp '$K8S_CRED_FILE' '$kubeconfig'
                chmod 600 '$kubeconfig'
            """
        }
    }

    try {
        // Проверяем, существует ли namespace
        def namespaceExists = sh(
            script: "kubectl --kubeconfig='$kubeconfig' get namespace '$namespace' >/dev/null 2>&1 && echo 'exists' || echo 'not_exists'",
            returnStdout: true
        ).trim()

        if (namespaceExists == 'not_exists') {
            echo "Namespace $namespace не существует. Создаю..."
            sh "kubectl --kubeconfig='$kubeconfig' create namespace '$namespace'"
            echo "Namespace $namespace успешно создан."
        } else {
            echo "Namespace $namespace уже существует. Пропускаю создание."
        }
    } finally {
        // Удаляем временный kubeconfig, если он был создан
        if (credentialsId) {
            sh "rm -f '$kubeconfig' || true"
        }
    }
}