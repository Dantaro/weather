package eloper.dev.weather.sensor

interface SensorService<T> {
    suspend fun getReadout(): T?
}