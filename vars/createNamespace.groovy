// vars/createNamespace.groovy

/**
 * Создает неймспейс в Kubernetes, если он не существует
 * @param namespace Имя неймспейса (обязательный)
 * @param credentialsId ID credentials с kubeconfig (опционально)
 * @return Результат операции
 */

def call(Map config = [:]) {
    if (!config.namespace) {
        error "Namespace name is required"
    }

    def kubeconfig = "${env.WORKSPACE}/.kube/config"
    
    withCredentials([file(credentialsId: config.credentialsId ?: 'k8s_cluster_cred', variable: 'KUBECONFIG_FILE']) {
        sh """
            mkdir -p ${env.WORKSPACE}/.kube
            cp '$KUBECONFIG_FILE' '$kubeconfig'
            chmod 600 '$kubeconfig'
        """

        try {
            // Проверка существования неймспейса
            def namespaceExists = sh(
                script: "kubectl --kubeconfig='$kubeconfig' get namespace '${config.namespace}' >/dev/null 2>&1 && echo 'exists' || echo 'not_exists'",
                returnStdout: true
            ).trim()

            if (namespaceExists == 'not_exists') {
                echo "Creating namespace: ${config.namespace}"
                sh "kubectl --kubeconfig='$kubeconfig' create namespace '${config.namespace}'"
                return [status: 'CREATED', namespace: config.namespace]
            } else {
                echo "Namespace ${config.namespace} already exists"
                return [status: 'EXISTS', namespace: config.namespace]
            }
        } finally {
            sh "rm -f '$kubeconfig' || true"
        }
    }
}