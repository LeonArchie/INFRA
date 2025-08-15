
/**
 * Утилиты для работы с Git репозиториями
 */

// Проверка типа репозитория
def isPrivateRepo(String repoUrl, String credsId = null) {
    return repoUrl?.contains('github.com') && credsId?.trim()
}

// Извлечение имени репозитория
def extractRepoName(String repoUrl) {
    if (!repoUrl) return null
    return repoUrl.split('/').last().replace('.git', '')
}

// Валидация URL репозитория
def validateRepoUrl(String repoUrl) {
    if (!repoUrl) {
        error "URL репозитория не может быть пустым"
    }
    if (!(repoUrl.startsWith('http') || repoUrl.startsWith('git@'))) {
        error "Некорректный URL репозитория: ${repoUrl}"
    }
}