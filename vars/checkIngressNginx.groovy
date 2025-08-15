// vars/checkIngressNginx.groovy

/**
 * Проверяет ресурсы ingress-nginx в кластере
 * @param config Map с параметрами:
 *   - namespace (String): Namespace для проверки (по умолчанию 'ingress-nginx')
 * @return void
 * 
 * Примеры использования:
 * 1. checkIngressNginx()
 * 2. checkIngressNginx(namespace: 'my-ingress-ns')
 */
def call(Map config = [:]) {
    def namespace = config.namespace ?: 'ingress-nginx'
    
    try {
        echo "🔍 Checking ingress-nginx pods..."
        def pods = sh(
            script: "kubectl get pods -n ${namespace} -o wide",
            returnStdout: true
        ).trim()
        echo "📦 Pods in ${namespace}:\n${pods}"

        echo "🔍 Checking ingress-nginx services..."
        def services = sh(
            script: "kubectl get services -n ${namespace} -o wide",
            returnStdout: true
        ).trim()
        echo "🛎️ Services in ${namespace}:\n${services}"

    } catch (Exception e) {
        error "❌ Failed to get resources: ${e.getMessage()}"
    }
}