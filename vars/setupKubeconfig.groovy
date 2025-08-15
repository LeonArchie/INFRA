// vars/setupKubeconfig.groovy

/**
 * Настраивает доступ к Kubernetes кластеру
 * @param config Map с параметрами:
 *   - credentialsId (String): ID credentials в Jenkins (по умолчанию 'k8s_cluster_cred')
 *   - workspace (String): Путь к рабочей директории (по умолчанию env.WORKSPACE)
 * @return void
 * 
 * Примеры использования:
 * 1. setupKubeconfig()
 * 2. setupKubeconfig(credentialsId: 'my-kube-cred')
 */
def call(Map config = [:]) {
    def credentialsId = config.credentialsId ?: 'k8s_cluster_cred'
    def workspace = config.workspace ?: env.WORKSPACE
    
    withCredentials([file(credentialsId: credentialsId, variable: 'KUBECONFIG_FILE')]) {
        def safeWorkspace = workspace.replace(' ', '_')
        env.KUBECONFIG = "${safeWorkspace}/.kube/config"
        
        sh """
            mkdir -p "${safeWorkspace}/.kube"
            cp "${KUBECONFIG_FILE}" "${env.KUBECONFIG}"
            chmod 600 "${env.KUBECONFIG}"
        """
    }
}