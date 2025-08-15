// vars/checkDocker.groovy

/**
 * Этот пайплайн проверяет наличие и версию Docker на агенте Jenkins.
 * Он состоит из одной стадии, которая выполняет команду 'docker --version'.
 */

def call() {
    pipeline {
        // Запускает пайплайн на любом доступном агенте Jenkins
        agent any
        
        stages {
            // Стадия 'Check Docker' - проверка установки Docker
            stage('Check Docker') {
                steps {
                    // Выполняет команду в shell для проверки версии Docker
                    sh 'docker --version'
                    // Эта команда выведет версию Docker, если он установлен,
                    // или ошибку, если Docker не найден в системе
                }
            }
        }
    }
}