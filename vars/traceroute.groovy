// vars/traceroute.groovy

/**
 * Выполняет traceroute/tracert для указанных серверов
 * @param config Map с параметрами:
 *   - servers (List/String): Список серверов
 *   - maxHops (int): Максимальное количество прыжков (по умолчанию 30)
 *   - platform (String): 'Linux' или 'Windows' (по умолчанию 'Linux')
 * @return void
 * 
 * Примеры использования:
 * 1. traceroute(servers: 'google.com', maxHops: 15)
 * 2. traceroute(servers: ['yandex.ru', '8.8.8.8'], platform: 'Windows')
 */
def call(Map config = [:]) {
    // Парсим параметры
    def servers = config.servers instanceof String ? 
                 config.servers.split('\n') as List : 
                 config.servers ?: []
    def maxHops = config.maxHops?.toInteger() ?: 30
    def platform = config.platform ?: 'Linux'
    
    servers.each { server ->
        server = server.trim()
        if (!server) return
        
        echo "🌐 Tracing route to ${server} (max ${maxHops} hops)"
        
        try {
            if (platform == 'Windows') {
                bat "tracert -h ${maxHops} ${server}"
            } else {
                sh "traceroute -m ${maxHops} ${server}"
            }
        } catch (e) {
            echo "⚠️ Traceroute failed for ${server}: ${e}"
        }
    }
}