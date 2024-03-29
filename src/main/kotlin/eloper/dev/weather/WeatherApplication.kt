package eloper.dev.weather

import eloper.dev.weather.database.WeatherDao
import eloper.dev.weather.database.WeatherDto
import eloper.dev.weather.sensor.ThermometerReadout
import eloper.dev.weather.sensor.ThermometerService
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime
import java.util.Properties

fun main(args: Array<String>) {
    // Load properties and create services
    val propertiesLevel = if (args.isNotEmpty()) { ".${args[0]}" } else { "" }
    val properties: Properties = loadProperties("application$propertiesLevel.properties")
    val environment = initializeServices(properties)
    // Run the system
    runSystem(environment)
}

fun loadProperties(fileName: String): Properties {
    return Properties().apply {
        val inputStream = this::class.java.classLoader.getResourceAsStream(fileName)
        this.load(inputStream)
    }
}

fun initializeServices(properties: Properties): Environment {
    // Extract properties
    val dbLocation = properties.getProperty("database.location")
    val pythonLocation = properties.getProperty("python.location")
    // Create services
    val weatherDao = WeatherDao(dbLocation)
    val thermometerService = ThermometerService(pythonLocation)
    return Environment(weatherDao, thermometerService)
}

fun runSystem(env: Environment) {
    // Get the start time
    val measuredAt = ZonedDateTime.now()

    // Gather all the data from sensors
    var thermometerReadout: ThermometerReadout?
    runBlocking {
        // This approach lets us add new sensors and have them run in tandem
        val defThermometerReadout = async { env.thermometerService.getReadout() }
        thermometerReadout = defThermometerReadout.await()
    }

    // Built DTOs
    val weatherDto = WeatherDto(
        temperatureC = thermometerReadout?.temperature ?: 0f,
        temperatureF = thermometerReadout?.temperatureF ?: 0f,
        humidity = thermometerReadout?.humidity ?: 0f,
        measuredAt = measuredAt
    )

    // Store date
    env.weatherDao.insertRow(weatherDto)
}

data class Environment(
    val weatherDao: WeatherDao,
    val thermometerService: ThermometerService
)