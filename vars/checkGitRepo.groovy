// vars/checkGitRepo.groovy

/**
 * Проверяет и клонирует Git репозиторий
 * @param config Map параметров:
 *   - repoUrl (String): URL репозитория (обязательный)
 *   - branch (String): Ветка (по умолчанию 'main')
 *   - credsId (String): ID credentials (опционально для приватных репо)
 * @return void
 * 
 * Примеры:
 * 1. checkGitRepo(repoUrl: 'https://github.com/user/repo.git')
 * 2. checkGitRepo(repoUrl: 'https://github.com/private/repo.git', credsId: 'my-creds')
 */
def call(Map config = [:]) {
    // Инициализация утилит
    def utilsREPO = new utilsREPO()
    
    // Валидация параметров
    utilsREPO.validateRepoUrl(config.repoUrl)
    def repoName = utilsREPO.extractRepoName(config.repoUrl)
    def isPrivate = utilsREPO.utilsREPO(config.repoUrl, config.credsId)

    echo "🔍 Проверяем репозиторий: ${repoName ?: config.repoUrl}"

    try {
        if (isPrivate) {
            echo "🔐 Используем credentials (${config.credsId}) для приватного репозитория"
            git(
                url: config.repoUrl,
                branch: config.branch ?: 'main',
                credentialsId: config.credsId
            )
        } else {
            echo "🌍 Клонируем публичный репозиторий"
            git(
                url: config.repoUrl,
                branch: config.branch ?: 'main'
            )
        }

        // Проверка содержимого
        def files = sh(script: 'ls -la', returnStdout: true).trim()
        echo "📂 Содержимое репозитория:\n${files}"
        
        return [status: 'SUCCESS', repo: repoName]
    } catch (Exception e) {
        error "❌ Ошибка клонирования: ${e.getMessage()}"
        return [status: 'FAILURE', error: e.getMessage()]
    }
}