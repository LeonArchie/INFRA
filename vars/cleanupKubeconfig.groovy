// vars/cleanupKubeconfig.groovy

/**
 * Очищает временные файлы конфигурации Kubernetes
 * @param config Map с параметрами:
 *   - workspace (String): Путь к рабочей директории (по умолчанию env.WORKSPACE)
 * @return void
 * 
 * Примеры использования:
 * 1. cleanupKubeconfig()
 * 2. cleanupKubeconfig(workspace: '/custom/path')
 */
def call(Map config = [:]) {
    def workspace = config.workspace ?: env.WORKSPACE
    def safeWorkspace = workspace.replace(' ', '_')
    sh "rm -rf ${safeWorkspace}/.kube || true"
}