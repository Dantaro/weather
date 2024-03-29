package eloper.dev.weather.database

import java.sql.DriverManager
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WeatherDao(
    private val dbLocation: String
) {
    fun insertRow(dto: WeatherDto) {
        DriverManager
            .getConnection("jdbc:sqlite:$dbLocation")
            .use { conn ->
                conn.autoCommit = false
                try {
                    val preparedStatement =
                        conn.prepareStatement("INSERT INTO weather_data (temperature_c, temperature_f, humidity, measured_at) VALUES (?, ?, ?, ?)")
                    preparedStatement.setFloat(1, dto.temperatureC)
                    preparedStatement.setFloat(2, dto.temperatureF)
                    preparedStatement.setFloat(3, dto.humidity)
                    preparedStatement.setString(4, dto.measuredAt.toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    preparedStatement.executeUpdate()
                    conn.commit()
                } catch (e: Exception) {
                    println(e.message ?: "Unknown error")
                    conn.rollback()
                }
            }
    }
}

data class WeatherDto(
    val id: Int? = null,
    val temperatureC: Float,
    val temperatureF: Float,
    val humidity: Float,
    val measuredAt: ZonedDateTime
)