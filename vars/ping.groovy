// vars/ping.groovy

/**
 * Выполняет ping указанных серверов
 * @param config Map с параметрами:
 *   - servers (List/String): Список серверов (массив или строка с разделителем \n)
 *   - count (int): Количество пакетов (по умолчанию 4)
 *   - platform (String): 'Linux' или 'Windows' (по умолчанию 'Linux')
 * @return void
 * 
 * Примеры использования:
 * 1. ping(servers: 'google.com\nyandex.ru', count: 5)
 * 2. ping(servers: ['8.8.8.8', 'example.com'], platform: 'Windows')
 */
def call(Map config = [:]) {
    // Парсим параметры
    def servers = config.servers instanceof String ? 
                 config.servers.split('\n') as List : 
                 config.servers ?: []
    def count = config.count?.toInteger() ?: 4
    def platform = config.platform ?: 'Linux'
    
    servers.each { server ->
        server = server.trim()
        if (!server) return
        
        echo "🔍 Pinging ${server} (${count} packets)"
        
        try {
            if (platform == 'Windows') {
                bat "ping -n ${count} ${server}"
            } else {
                sh "ping -c ${count} ${server}"
            }
        } catch (e) {
            echo "⚠️ Ping failed for ${server}: ${e}"
        }
    }
}